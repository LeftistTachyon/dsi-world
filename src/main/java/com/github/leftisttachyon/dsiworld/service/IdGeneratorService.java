package com.github.leftisttachyon.dsiworld.service;

/**
 * An interface that outlines a service that generates string UUIDs.
 *
 * @author Jed Wang
 * @since 1.0.0
 */
public interface IdGeneratorService extends AutoCloseable {
    /**
     * Generates a new Azure blob-viable ID.
     * @return the newly generated ID
     */
    String getNextId();

    @Override
    void close();
}
