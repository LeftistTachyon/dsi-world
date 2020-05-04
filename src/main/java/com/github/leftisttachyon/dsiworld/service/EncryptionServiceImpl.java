package com.github.leftisttachyon.dsiworld.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * A class that implements the {@link EncryptionService} interface.
 *
 * @author Jed Wang
 * @since 1.0.0
 */
@Service
@Slf4j
public class EncryptionServiceImpl implements EncryptionService {
    /**
     * The cipher to use
     */
    private final Cipher cipher;
    /**
     * The secret key to use
     */
    private final SecretKey secretKey;

    /**
     * The constructor
     */
    EncryptionServiceImpl(@Value("${encryption.seed}") long seed) {
        Cipher tempC = null;
        try {
            tempC = Cipher.getInstance("AES/CBC/PKCS5Padding");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            log.error("javax.crypto.Cypher could not be initialized", e);
        } finally {
            cipher = tempC;
        }

        Random r = new Random(seed);
        byte[] key = new byte[16];
        r.nextBytes(key);
        secretKey = new SecretKeySpec(key, "AES");
    }

    @Override
    public CipherInputStream getCipherInputStream(File f) throws IOException {
        FileInputStream fIn = new FileInputStream(f);

        byte[] fileIv = new byte[16];
        if (fIn.read(fileIv) != 16) {
            log.warn("Something isn't right...");
        }
        try {
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(fileIv));
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
            log.error("An invalid key or algorithm was specified", e);
            return null;
        }

        return new CipherInputStream(fIn, cipher);
    }

    @Override
    public CipherOutputStream getCipherOutputStream(File f) throws IOException {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        } catch (InvalidKeyException e) {
            log.warn("An invalid key was specified", e);
            return null;
        }
        byte[] iv = cipher.getIV();

        FileOutputStream fOut = new FileOutputStream(f);
        fOut.write(iv);

        return new CipherOutputStream(fOut, cipher);
    }

    @Override
    public SealedObject sealObject(Serializable s) throws IOException {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        } catch (InvalidKeyException e) {
            log.error("An invalid key was specified");
            return null;
        }
        try {
            return new SealedObject(s, cipher);
        } catch (IllegalBlockSizeException e) {
            log.error("An invalid block size was specified in the cipher", e);
            return null;
        }
    }

    @Override
    public Object unsealObject(SealedObject so) throws ClassNotFoundException, IOException {
        try {
            return so.getObject(secretKey);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("An invalid algorithm or key was specified", e);
            return null;
        }
    }
}
