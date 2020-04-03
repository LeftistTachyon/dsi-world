package com.github.leftisttachyon.dsiworld.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * A class that implements the {@link EncryptionFileService} interface.
 *
 * @author Jed Wang
 * @since 1.0.0
 */
@Service
@Slf4j
public class EncryptionFileServiceImpl implements EncryptionFileService {
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
    EncryptionFileServiceImpl() {
        Cipher tempC = null;
        try {
            tempC = Cipher.getInstance("AES/CBC/PKCS5Padding");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            log.error("javax.crypto.Cypher could not be initialized", e);
        } finally {
            cipher = tempC;
        }

        SecretKey tempSK = null;
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256);
            tempSK = keyGen.generateKey();
        } catch (NoSuchAlgorithmException e) {
            log.error("javax.crypto.SecretKey could not be initialized", e);
        }
        secretKey = tempSK;
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
}
