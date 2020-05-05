package com.github.leftisttachyon.dsiworld.data;

import org.springframework.stereotype.Component;

import java.util.List;

/**
 * A class that deals with batch operations with users.
 *
 * @author Jed Wang
 * @since 1.0.0
 */
@Component
public class UserList {
    /**
     * The internal list of users associated with this user list
     */
    private List<User> list;
}
