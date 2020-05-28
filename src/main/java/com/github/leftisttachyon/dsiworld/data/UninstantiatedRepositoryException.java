package com.github.leftisttachyon.dsiworld.data;

import org.eclipse.jgit.api.errors.GitAPIException;

/**
 * An exception that represents attempting to manipulate an uninstantiated {@link Repository} object.
 *
 * @author Jed Wang
 * @since 1.0.0
 */
public class UninstantiatedRepositoryException extends GitAPIException {
    /**
     * Creates a new {@link UninstantiatedRepositoryException}.
     *
     * @param message the message
     * @param cause   the underlying cause of this exception
     */
    public UninstantiatedRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new {@link UninstantiatedRepositoryException}.
     *
     * @param message the message
     */
    public UninstantiatedRepositoryException(String message) {
        super(message);
    }
}
