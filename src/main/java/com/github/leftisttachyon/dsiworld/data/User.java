package com.github.leftisttachyon.dsiworld.data;

import com.github.leftisttachyon.dsiworld.model.ContainerModel;
import com.github.leftisttachyon.dsiworld.model.ContainerModelFactory;
import com.microsoft.azure.storage.blob.models.BlobItem;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
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
     * Whether this user has been verified
     */
    private boolean verified;
    /**
     * A list of repositories that this user owns
     */
    private transient List<Repository> repositories;
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
        repositories = new ArrayList<>();

        List<BlobItem> blobItems = userContainer.blobList();
        for (BlobItem item : blobItems) {
            if (item.name().startsWith("repos/")) {
                String name = item.name();
                int before = name.indexOf('/') + 1,
                        after = name.contains(".") ? name.indexOf('.') : name.length();
                Repository repo = new Repository(userContainer.createBlob(item),
                        name.substring(before, after));
                repositories.add(repo);
            }
        }
    }

    /**
     * Adds a repository to the list of repositories that this user owns
     *
     * @param r the {@link Repository} to add
     */
    public void addRepository(Repository r) {
        if (repositories == null) {
            repositories = new ArrayList<>();
        }

        repositories.add(r);
    }

    /**
     * Saves all open repositories that are associated with this user.
     *
     * @throws IOException if something goes wrong while manipulating files
     */
    public void saveRepositories() throws IOException {
        if (repositories != null) {
            for (Repository r : repositories) {
                if (r.isOpened()) r.save();
            }
        }
    }

    @Override
    public void close() {
        for (Repository r : repositories) {
            r.close();
        }

        repositories = null;
    }
}
