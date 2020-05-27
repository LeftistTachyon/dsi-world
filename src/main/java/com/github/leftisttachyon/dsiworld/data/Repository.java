package com.github.leftisttachyon.dsiworld.data;

import com.github.leftisttachyon.dsiworld.model.BlobModel;
import com.github.leftisttachyon.dsiworld.service.ZipService;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A class that represents a repository.
 *
 * @author Jed Wang
 * @since 1.0.0
 */
@Data
@Setter(AccessLevel.NONE)
@Slf4j
public class Repository implements AutoCloseable {
    /**
     * The blob that stores the repo on the cloud.
     */
    @Getter(AccessLevel.NONE)
    private final BlobModel blob;
    /**
     * The name of this repository.
     */
    private final String name;
    /**
     * The file where the repository is stored locally
     */
    private File file;
    /**
     * The {@link Git} object that represents the repository.
     */
    private Git git;
    /**
     * The {@link CredentialsProvider} associated with this repo
     */
    private CredentialsProvider creds;

    /**
     * Downloads and unzips the repository from the cloud.<br>
     * If this operation has already been done, all files will be replaced with files from the cloud.
     *
     * @throws IOException if something goes wrong while manipulating files
     */
    public void fetch() throws IOException {
        File zipped = blob.getBlob(),
                parent = Files.createTempDirectory("").toFile();
        if (!ZipService.getInstance().unzipFile(zipped, parent)) {
            log.warn("Repository could not be fetched");
            return;
        }

        git = Git.open(file = parent);
    }

    /**
     * Clones the repository stored at the given url and stores it in memory.
     *
     * @param url the URL to clone the repo from
     * @throws IOException if something goes wrong while manipulating files
     */
    public void clone(String url) throws IOException {
        File parent = Files.createTempDirectory("").toFile();
        try {
            git = Git.cloneRepository()
                    .setDirectory(parent)
                    .setURI(url)
                    .call();
        } catch (GitAPIException e) {
            log.warn("An error occurred while cloning the repo", e);
            return;
        }

        file = parent;

        save();
    }

    /**
     * Clones the repository stored at the given url with the given credentials and stores it in memory.
     *
     * @param url      the URL to clone the repo from
     * @param username the username to use as credentials
     * @param password the password to use as credentials
     * @throws IOException if something goes wrong while manipulating files
     */
    public void clone(String url, String username, String password) throws IOException {
        File parent = Files.createTempDirectory("").toFile();
        try {
            git = Git.cloneRepository()
                    .setDirectory(parent)
                    .setURI(url)
                    .setCredentialsProvider(creds = new UsernamePasswordCredentialsProvider(username, password))
                    .call();
        } catch (GitAPIException e) {
            log.warn("An error occurred while cloning the repo", e);
            return;
        }

        file = parent;

        save();
    }

    /**
     * Saves this repository onto the cloud.<br>
     * Note: this operation overwrites any preexisting data related to this repository with this information.
     *
     * @throws IOException if something goes wrong while manipulating files
     */
    public void save() throws IOException {
        if (file == null)
            return;

        File zip = File.createTempFile("repo", ".zip");
        if (!ZipService.getInstance().zipFolder(file, zip)) {
            log.warn("Repository could not be zipped");
            return;
        }

        blob.uploadFile(zip);
    }

    /**
     * Finds and returns the file found at the given relative path, if it exists.
     *
     * @param relPath the relative path to search the file for
     * @return the file, if it exists. If it doesn't, then {@code null} is returned.
     */
    public File getFile(String relPath) {
        log.debug("relPath: '{}'", relPath);
        Path p = file.toPath().resolve(relPath);
        log.debug("path: '{}'", p);
        File file1 = p.toFile();
        return file1.exists() ? file1 : null;
    }

    /**
     * Creates a blank file at the given relative path.
     *
     * @param relPath the relative path at which to create the file
     * @return whether the operation was successful, or if the file already exists, {@code true}.
     * @throws IOException if something goes wrong while manipulating files
     */
    public boolean createFile(String relPath) throws IOException {
        File toCreate = new File(file.toString() + relPath);
        log.debug("toCreate: '{}'", toCreate);
        if (toCreate.exists()) return true;

        File parentFile = toCreate.getParentFile();
        log.debug("parentFile: '{}'", parentFile);
        if (parentFile == null || !(parentFile.exists() || parentFile.mkdirs())) return false;

        return toCreate.createNewFile();
    }

    /**
     * Returns whether this {@link Repository} has been fetched and not closed.
     *
     * @return whether this {@link Repository} has been fetched and not closed.
     */
    public boolean isOpened() {
        return file != null;
    }

    @Override
    public void close() {
        blob.close();

        file.delete();
        file = null;
        git.close();
        git = null;
    }
}
