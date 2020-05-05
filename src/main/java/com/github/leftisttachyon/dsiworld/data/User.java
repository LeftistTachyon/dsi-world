package com.github.leftisttachyon.dsiworld.data;

import com.github.leftisttachyon.dsiworld.model.ContainerModel;
import com.github.leftisttachyon.dsiworld.model.ContainerModelFactory;
import com.microsoft.azure.storage.blob.models.BlobItem;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A class that represents a user.
 *
 * @author Jed Wang
 * @since 1.0.0
 */
@Data
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class User implements Serializable, AutoCloseable {
    /**
     * The number to associate with this class
     */
    private static final long serialVersionUID = -698412456457L;

    /**
     * The username for this user.
     */
    private final String username;
    /**
     * A string that represents the id of this user
     */
    private final String id;
    /**
     * The password for this user
     */
    private Character[] password;
    /**
     * The email associated with this user
     */
    private String email;
    /**
     * A list of repositories that this user owns
     */
    private final transient List<Repository> repositories = new ArrayList<>();
    /**
     * The Azure storage container associated with this user
     */
    @Setter(AccessLevel.NONE)
    private transient ContainerModel userContainer;

    /**
     * Sets the {@link ContainerModel} associated with this user
     *
     * @param factory the factory that creates {@link ContainerModel}s
     */
    @Autowired
    public void setContainerModel(ContainerModelFactory factory) {
        userContainer = factory.createContainerModel(id);
    }

    /**
     * Loads all existing repositories into memory.
     */
    public void loadRepositories() {
        List<BlobItem> blobItems = userContainer.blobList();
        for (BlobItem item : blobItems) {
            if (item.name().startsWith("repos/")) {
                Repository repo = new Repository(userContainer.createBlob(item));
                repositories.add(repo);
            }
        }
    }

    @Override
    public void close() {
        for (Repository r : repositories) {
            r.close();
        }
    }
}
