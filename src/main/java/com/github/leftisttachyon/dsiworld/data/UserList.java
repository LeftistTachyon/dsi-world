package com.github.leftisttachyon.dsiworld.data;

import com.github.leftisttachyon.dsiworld.model.BlobModel;
import com.github.leftisttachyon.dsiworld.service.EncryptionService;
import com.github.leftisttachyon.dsiworld.util.BeanAnnotations;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * A class that deals with batch operations with users.
 *
 * @author Jed Wang
 * @since 1.0.0
 */
@Scope("singleton")
@Component
@Slf4j
public class UserList implements ApplicationContextAware {
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
    private List<User> list;

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
                list = (List<User>) ois.readObject();
            }

            for (User u : list) {
                beanFactory.autowireBean(u);
                beanFactory.initializeBean(u, "bean");
            }
        } catch (IOException | ClassNotFoundException e) {
            log.error("An exception occurred while fetching user data", e);
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
            oos.writeObject(list);
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
        for (User user : list) {
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
        list.add(user);

        try {
            save();
        } catch (IOException e) {
            log.warn("Could not save user information due to IOException", e);
        }
    }
}
