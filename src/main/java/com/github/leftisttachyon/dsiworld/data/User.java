package com.github.leftisttachyon.dsiworld.data;

import lombok.Data;

import java.io.Serializable;

/**
 * A class that represents a user.
 *
 * @author Jed Wang
 * @since 1.0.0
 */
@Data
public final class User implements Serializable {
    /**
     * The number to associate with this class
     */
    private static final long serialVersionUID = -698412456457L;

    /**
     * The username for this user.
     */
    private String username;
}
