package com.github.leftisttachyon.dsiworld.data;

import com.github.leftisttachyon.dsiworld.model.BlobModel;
import com.github.leftisttachyon.dsiworld.service.ZipService;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * A class that represents a repository.
 *
 * @author Jed Wang
 * @since 1.0.0
 */
@Data
@Setter(AccessLevel.NONE)
@Slf4j
public class Repository {
    /**
     * The blob that stores the repo on the cloud.
     */
    @Getter(AccessLevel.NONE)
    private final BlobModel blob;
    /**
     * The file where the repository is stored locally
     */
    private File file;
    /**
     * The {@link Git} object that represents the repository.
     */
    private Git git;

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
     * Saves this repository onto the cloud.<br>
     * Note: this operation overwrites any preexisting data related to this repository with this information.
     *
     * @throws IOException if something goes wrong while manipulating files
     */
    public void save() throws IOException {
        File zip = File.createTempFile("repo", ".zip");
        if (!ZipService.getInstance().zipFolder(file, zip)) {
            log.warn("Repository could not be zipped");
            return;
        }

        blob.uploadFile(zip);
    }
}
