package com.github.leftisttachyon.dsiworld.service;

import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;

/**
 * An interface that outlines a service that encrypts and decrypts files.
 *
 * @author Jed Wang
 * @since 1.0.0
 */
public interface EncryptionFileService {
    /**
     * Returns a {@link CipherInputStream} that reads from the given {@link File}.
     *
     * @param f the {@link File} to read from
     * @return a {@link CipherInputStream} that reads from the given {@link File}
     * @throws InvalidKeyException                if an invalid key is instantiated
     * @throws IOException                        if an invalid {@link File} is given
     * @throws InvalidAlgorithmParameterException if the {@link CipherInputStream} could not be instantiated
     */
    CipherInputStream getCipherInputStream(File f) throws InvalidKeyException, IOException,
            InvalidAlgorithmParameterException;

    /**
     * Returns a {@link BufferedReader} that wraps the {@link CipherInputStream} from
     * {@link #getCipherInputStream(File)}.
     *
     * @param f the {@link File to read from}
     * @return a {@link BufferedReader} that reads encrypted data from the given {@link File}
     * @throws InvalidKeyException                if an invalid key is instantiated
     * @throws IOException                        if an invalid {@link File} is given
     * @throws InvalidAlgorithmParameterException if the {@link CipherInputStream} could not be instantiated
     */
    default BufferedReader getEncryptedBufferedReader(File f) throws InvalidKeyException, IOException,
            InvalidAlgorithmParameterException {
        return new BufferedReader(new InputStreamReader(getCipherInputStream(f)));
    }

    /**
     * Returns a {@link CipherOutputStream} that writes into the given {@link File}.
     *
     * @param f the {@link File} to write into
     * @return a {@link CipherOutputStream} that writes into the given {@link File}
     * @throws IOException         if something goes wrong with the file
     * @throws InvalidKeyException if the key is incorrectly instantiated
     */
    CipherOutputStream getCipherOutputStream(File f) throws IOException, InvalidKeyException;

    /**
     * Returns a {@link PrintWriter} that wraps the {@link CipherOutputStream}
     * from {@link #getCipherOutputStream(File)}.
     *
     * @param f the {@link File} to write into
     * @return a {@link PrintWriter} that writes encrypted file into the given {@link File}
     * @throws IOException         if something goes wrong with the file
     * @throws InvalidKeyException if the key is incorrectly instantiated
     */
    default PrintWriter getEncryptedPrintWriter(File f) throws IOException, InvalidKeyException {
        return new PrintWriter(getCipherOutputStream(f));
    }
}
