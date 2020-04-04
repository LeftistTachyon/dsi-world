package com.github.leftisttachyon.dsiworld.service;

import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SealedObject;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * An interface that outlines a service that encrypts and decrypts files.
 *
 * @author Jed Wang
 * @since 1.0.0
 */
public interface EncryptionService {
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
     * Returns a {@link ObjectInputStream} that wraps the {@link CipherInputStream} from
     * {@link #getCipherInputStream(File)}.
     *
     * @param f the {@link File to read from}
     * @return a {@link ObjectInputStream} that reads encrypted data from the given {@link File}
     * @throws InvalidKeyException                if an invalid key is instantiated
     * @throws IOException                        if an invalid {@link File} is given
     * @throws InvalidAlgorithmParameterException if the {@link CipherInputStream} could not be instantiated
     */
    default ObjectInputStream getEncryptedObjectInputStream(File f) throws InvalidAlgorithmParameterException,
            InvalidKeyException, IOException {
        return new ObjectInputStream(getCipherInputStream(f));
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

    /**
     * Returns a {@link ObjectOutputStream} that wraps the {@link CipherOutputStream}
     * from {@link #getCipherOutputStream(File)}.
     *
     * @param f the {@link File} to write into
     * @return a {@link ObjectOutputStream} that writes encrypted file into the given {@link File}
     * @throws IOException         if something goes wrong with the file
     * @throws InvalidKeyException if the key is incorrectly instantiated
     */
    default ObjectOutputStream getEncryptedObjectOutputStream(File f) throws IOException, InvalidKeyException {
        return new ObjectOutputStream(getCipherOutputStream(f));
    }

    /**
     * Converts the given {@link Serializable} object into a {@link SealedObject}.
     *
     * @param s the {@link Serializable} object to seal away
     * @return a {@link SealedObject} that wraps the given object
     * @throws IOException               if something goes wrong while wrapping the object
     * @throws IllegalBlockSizeException if something goes wrong while wrapping the object
     * @throws InvalidKeyException       if something goes wrong while wrapping the object
     */
    SealedObject sealObject(Serializable s) throws IOException, IllegalBlockSizeException, InvalidKeyException;

    /**
     * Converts the given {@link SealedObject} into an {@link Object}.
     *
     * @param so the {@link SealedObject} to unseal
     * @return the {@link Object} contained within the given {@link SealedObject}
     * @throws ClassNotFoundException   if an invalid object is in the given {@link SealedObject}
     * @throws NoSuchAlgorithmException if an invalid algorithm is used to seal the object
     * @throws InvalidKeyException      if an invalid key is used to unseal the object
     * @throws IOException              if something goes wrong while unsealing the object
     */
    Object unsealObject(SealedObject so) throws ClassNotFoundException, NoSuchAlgorithmException, InvalidKeyException,
            IOException;
}
