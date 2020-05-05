package com.github.leftisttachyon.dsiworld;

import com.github.leftisttachyon.dsiworld.data.User;
import com.github.leftisttachyon.dsiworld.data.UserFactory;
import com.github.leftisttachyon.dsiworld.data.UserList;
import com.github.leftisttachyon.dsiworld.model.ContainerModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

/**
 * A collection of tests that ensure that the {@link User} class acts as expected
 *
 * @author Jed Wang
 * @since 1.0.0
 */
@SpringBootTest
public class UserObjectTests {
    /**
     * The user factory to use
     */
    @Autowired
    private UserFactory userFactory;
    /**
     * The user list to use
     */
    @Autowired
    private UserList userList;

    /**
     * A thing to test setter injection
     */
    @Test
    public void setterInjectionTest() {
        User u = userFactory.createUser("jeff");
        String id = u.getId();
        System.out.println("id: " + id);
        Assertions.assertNotNull(id);

        ContainerModel container = u.getUserContainer();
        System.out.println("container: " + container);
        Assertions.assertNotNull(container);
    }

    /**
     * A thing to test creating users
     *
     * @throws IOException if something goes wrong
     */
    @Test
    public void uploadUserTest() throws IOException {
//        User user = userFactory.createUser("LeftistTachyon");
//        user.setEmail("randydonaldjr@gmail.com");
//        user.setPassword(ArrayUtils.toObject("*********".toCharArray()));
//
//        userList.addUser(user);
//
//        userList.save();
    }

    /**
     * Prints all user info from the user list
     */
    @Test
    public void printUserInfo() {
        for (User u : userList) {
            System.out.println(u.getUsername() + " <" + u.getEmail() + "> : " + u.getId());
        }
    }
}
