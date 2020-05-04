package com.github.leftisttachyon.dsiworld.data;

import com.github.leftisttachyon.dsiworld.service.IdGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * A class that makes creating users very simple.
 *
 * @author Jed Wang
 * @since 1.0.0
 */
@Scope("singleton")
@Component
public class UserFactory {
    /**
     * The service that generates ID's for the users
     */
    private final IdGeneratorService idService;

    /**
     * Creates a new UserFactory
     *
     * @param idService the id generator that should be used
     */
    @Autowired
    public UserFactory(IdGeneratorService idService) {
        this.idService = idService;
    }

    /**
     * Creates a new user with the specified information.
     *
     * @param username the username to use
     * @return the newly created user
     */
    public User createUser(String username) {
        String newId = idService.getNextId();
        return new User(username, newId);
    }
}
