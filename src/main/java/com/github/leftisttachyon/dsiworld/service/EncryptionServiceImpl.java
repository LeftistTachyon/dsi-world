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
    public CipherInputStream getCipherInputStream(File f) throws InvalidKeyException, IOException, InvalidAlgorithmParameterException {
        FileInputStream fIn = new FileInputStream(f);

        byte[] fileIv = new byte[16];
        if (fIn.read(fileIv) != 16) {
            log.warn("Something isn't right...");
        }
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(fileIv));

        return new CipherInputStream(fIn, cipher);
    }

    @Override
    public CipherOutputStream getCipherOutputStream(File f) throws IOException, InvalidKeyException {
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] iv = cipher.getIV();

        FileOutputStream fOut = new FileOutputStream(f);
        fOut.write(iv);

        return new CipherOutputStream(fOut, cipher);
    }

    @Override
    public SealedObject sealObject(Serializable s) throws IOException, IllegalBlockSizeException, InvalidKeyException {
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return new SealedObject(s, cipher);
    }

    @Override
    public Object unsealObject(SealedObject so) throws ClassNotFoundException, NoSuchAlgorithmException,
            InvalidKeyException, IOException {
        return so.getObject(secretKey);
    }
}
