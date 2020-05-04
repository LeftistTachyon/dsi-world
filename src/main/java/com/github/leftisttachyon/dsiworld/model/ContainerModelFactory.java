package com.github.leftisttachyon.dsiworld.model;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/**
 * A class that easily generates {@link ContainerModel container models}.<br>
 * "To first make a horse, you must first make a horse factory."
 *
 * @author Jed Wang
 * @since 1.0.0
 */
@Component
@Slf4j
public class ContainerModelFactory {
    /**
     * The name of the storage account to connect to
     */
    private final String accountName;
    /**
     * The secret key needed to connect to the storage account
     */
    private final String accountKey;

    /**
     * Creates a new ContainerModelFactory
     *
     * @param accountName the name of the storage account to connect to
     * @param accountKey  the secret key needed to connect to the storage account
     */
    private ContainerModelFactory(@Value("${azure.blob.name}") String accountName,
                                  @Value("${azure.blob.key}") String accountKey) {
        this.accountName = accountName;
        this.accountKey = accountKey;
    }

    /**
     * Creates a ContainerModel and connects it to the cloud.
     *
     * @param containerName the name of the container to create. This String
     *                      must abide by Azure's rules, or a {@link java.net.MalformedURLException} will be
     *                      thrown.
     */
    public ContainerModel createContainerModel(String containerName) {
        return createContainerModel(containerName, true);
    }

    /**
     * Creates a ContainerModel and connects it to the cloud.
     *
     * @param containerName    the name of the container to create. This String
     *                         must abide by Azure's rules, or a {@link java.net.MalformedURLException} will be
     *                         thrown.
     * @param checkForCreation whether this constructor should create the
     *                         container if it doesn't exist
     */
    public ContainerModel createContainerModel(String containerName, boolean checkForCreation) {
        log.debug("Creating container {}, checkingForCreation={}", containerName, checkForCreation);
        return new ContainerModel(accountName, accountKey, containerName, checkForCreation);
    }
}
