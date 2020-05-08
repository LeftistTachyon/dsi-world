package com.github.leftisttachyon.dsiworld.util;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * A {@link RuntimeException} that represents a 404 error
 *
 * @author Jed Wang
 * @since 1.0.0
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    /**
     * {@inheritDoc}
     */
    public ResourceNotFoundException() {
        super();
    }

    /**
     * {@inheritDoc}
     *
     * @param message the message to send with this ResourceNotFoundException
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
