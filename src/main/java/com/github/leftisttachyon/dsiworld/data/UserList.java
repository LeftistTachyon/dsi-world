package com.github.leftisttachyon.dsiworld.data;

import com.github.leftisttachyon.dsiworld.model.BlobModel;
import com.github.leftisttachyon.dsiworld.service.EncryptionService;
import com.github.leftisttachyon.dsiworld.util.BeanAnnotations;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.ApplicationScope;

import javax.annotation.PreDestroy;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A class that deals with batch operations with users.
 *
 * @author Jed Wang
 * @since 1.0.0
 */
@ApplicationScope
@Component
@Slf4j
public class UserList implements ApplicationContextAware, Iterable<User> {
    /**
     * The blob that stored information about users
     */
    private final BlobModel userInfoBlob;
    /**
     * The encryption service to use
     */
    private final EncryptionService encryptionService;
    /**
     * The internal list of users associated with this user list
     */
    @Getter
    private List<User> userList;

    /**
     * Creates a new {@link UserList}
     *
     * @param userInfoBlob      the blob that contains info about users
     * @param encryptionService the encryption service to use
     */
    @Autowired
    public UserList(@BeanAnnotations.UserInfoBlob BlobModel userInfoBlob,
                    EncryptionService encryptionService) {
        this.userInfoBlob = userInfoBlob;
        this.encryptionService = encryptionService;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        AutowireCapableBeanFactory beanFactory = applicationContext.getAutowireCapableBeanFactory();

        try {
            File f = userInfoBlob.getBlob();
            try (ObjectInputStream ois = encryptionService.getEncryptedObjectInputStream(f)) {
                userList = (List<User>) ois.readObject();
            } catch (EOFException e) {
                log.info("The user info file is poorly formatted or nonexistant");
                userList = new ArrayList<>();
                return;
            }

            for (User u : userList) {
                beanFactory.autowireBean(u);
                beanFactory.initializeBean(u, "bean");
            }
        } catch (IOException | ClassNotFoundException e) {
            log.error("An exception occurred while fetching user data", e);
            userList = new ArrayList<>();
        }
    }

    /**
     * Saves all user data to the cloud.
     *
     * @throws IOException if something goes wrong while manipulating files
     */
    public void save() throws IOException {
        File f = File.createTempFile("user-info", ".dat");
        try (ObjectOutputStream oos = encryptionService.getEncryptedObjectOutputStream(f)) {
            oos.writeObject(userList);
        }

        userInfoBlob.uploadFile(f);
    }

    /**
     * Attempts a login with the given information and returns the corresponding
     * {@link User} if the login is successful.
     *
     * @param username the username to attempt the login with
     * @param password the password to attempt the login with
     * @return the corresponding {@link User} if the credentials are correct, and {@code null} if they are not.
     */
    public User attemptLogin(String username, String password) {
        if (userList == null) { // list doesn't exist...
            return null; // ...so nobody can log in
        }

        for (User user : userList) {
            if (username.equals(user.getUsername()) &&
                    password.equals(new String(ArrayUtils.toPrimitive(user.getPassword())))) {
                return user;
            }
        }

        return null;
    }

    /**
     * Adds a {@link User} to the list of users and updates the cloud with the new info.
     *
     * @param user the {@link User} to add
     */
    public void addUser(User user) {
        userList.add(user);

        try {
            save();
        } catch (IOException e) {
            log.warn("Could not save user information due to IOException", e);
        }
    }

    /**
     * Freeing some resources.
     */
    @PreDestroy
    public void preDestroy() {
        userInfoBlob.close();
    }

    @Override
    public Iterator<User> iterator() {
        return userList.iterator();
    }
}
