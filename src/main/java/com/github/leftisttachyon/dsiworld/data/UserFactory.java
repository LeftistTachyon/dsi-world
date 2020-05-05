package com.github.leftisttachyon.dsiworld.data;

import com.github.leftisttachyon.dsiworld.service.IdGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.ApplicationScope;

import javax.annotation.PreDestroy;

/**
 * A class that makes creating users very simple.
 *
 * @author Jed Wang
 * @since 1.0.0
 */
@ApplicationScope
@Component
public class UserFactory implements ApplicationContextAware {
    /**
     * The service that generates ID's for the users
     */
    private final IdGeneratorService idService;
    /**
     * The {@link AutowireCapableBeanFactory} associated with this application
     */
    private AutowireCapableBeanFactory beanFactory;

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
        User user = new User(username, newId);

        beanFactory.autowireBean(user);
        beanFactory.initializeBean(user, "bean");

        return user;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        beanFactory = applicationContext.getAutowireCapableBeanFactory();
    }

    /**
     * Freeing up resources.
     */
    @PreDestroy
    public void preDestroy() {
        idService.close();
    }
}
