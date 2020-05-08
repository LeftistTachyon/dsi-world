package com.github.leftisttachyon.dsiworld.service;

import com.github.leftisttachyon.dsiworld.model.BlobModel;
import com.github.leftisttachyon.dsiworld.util.BeanAnnotations;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.ApplicationScope;

import java.io.*;
import java.util.HashSet;

/**
 * An implementation of the {@link IdGeneratorService} interface.
 *
 * @author Jed Wang
 * @since 1.0.0
 */
@Slf4j
@Service
@ApplicationScope
public class IdGeneratorServiceImpl implements IdGeneratorService {
    /**
     * The BlobModel to use when dealing with ID's
     */
    private final BlobModel idBlob;
    /**
     * The EncryptionService to use to encrypt file info
     */
    private final EncryptionService encryption;
    /**
     * A set of already claimed id's
     */
    private HashSet<Long> claimed;

    /**
     * Creates a new IdGeneratorServiceImpl instance.
     */
    @Autowired
    IdGeneratorServiceImpl(@BeanAnnotations.IdBlob BlobModel idBlob,
                                  EncryptionService encryption) {
        this.idBlob = idBlob;
        this.encryption = encryption;

        File blob;
        try {
            blob = idBlob.getBlob();
            try (ObjectInputStream ois = encryption.getEncryptedObjectInputStream(blob)) {
                claimed = (HashSet<Long>) ois.readObject();
            } catch (EOFException e) {
                log.info("The ID file is improperly formatted or nonexistent");
                claimed = new HashSet<>();
            } catch (IOException | ClassNotFoundException e) {
                log.error("ID Generator could not be instantiated", e);
                claimed = new HashSet<>();
            }
        } catch (Exception e) {
            log.warn("The file could not be downloaded", e);
            claimed = new HashSet<>();
        }
    }

    @Override
    public synchronized String getNextId() {
        long id;
        do {
            id = getRandLong();
        } while (id == 541307 || claimed.contains(id));
        claimed.add(id);

        try {
            updateBlob();
        } catch (IOException e) {
            log.error("The blob could not be updated due to errors with the file system", e);
        }

        return Long.toString(id, 36);
    }

    @Override
    public void close() {
        idBlob.close();
    }

    /**
     * Returns a random long in a specific range
     *
     * @return a random long in a specific range
     */
    long getRandLong() {
        return (long) (Math.random() * 4_738_381_338_321_570_240L + 46_656L);
    }

    /**
     * Updates the ID blob.
     *
     * @throws IOException if something goes wrong while writing to a temp file
     */
    private void updateBlob() throws IOException {
        File temp = File.createTempFile("used-ids", "dat");
        try (ObjectOutputStream oos = encryption.getEncryptedObjectOutputStream(temp)) {
            oos.writeObject(claimed);
        }

        idBlob.uploadFile(temp);
    }
}
