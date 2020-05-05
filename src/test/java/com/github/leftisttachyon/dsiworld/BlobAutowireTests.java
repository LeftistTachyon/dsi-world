package com.github.leftisttachyon.dsiworld;

import com.github.leftisttachyon.dsiworld.model.BlobModel;
import com.github.leftisttachyon.dsiworld.model.ContainerModel;
import com.github.leftisttachyon.dsiworld.util.BeanAnnotations;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * A collection of tests that test whether blob autowiring works as intended.
 *
 * @author Jed Wang
 * @since 1.0.0
 */
@SpringBootTest
public class BlobAutowireTests {
    /**
     * The meta info container
     */
    @Autowired
    @BeanAnnotations.MetaContainer
    private ContainerModel metaContainer;
    /**
     * The id blob
     */
    @Autowired
    @BeanAnnotations.IdBlob
    private BlobModel idBlob;

    /**
     * Testing whether the container exists.
     */
    @Test
    public void containerWireTest() {
        Assertions.assertNotNull(metaContainer);
    }

    /**
     * Testing whether the blob exists.
     */
    @Test
    public void blobWireTest() {
        Assertions.assertNotNull(idBlob);
    }

    /**
     * Testing whether it is possible to upload an entire folder using the current framework.<br>
     * Results: it doesn't, so I'll have to think of another solution
     *
     * @throws IOException if something goes wrong while manipulating files
     */
    public void folderUploadTest() throws IOException {
        File dir = Files.createTempDirectory("").toFile();
        System.out.println("dir: " + dir.getCanonicalPath());
        if (!dir.exists() && !dir.mkdir()) {
            Assertions.fail("Unable to create parent directory");
        }

        for (int i = 1; i <= 5; i++) {
            File f = new File(dir, "file" + i);
            System.out.println("file #" + i + ": " + f.getCanonicalPath());
            if (!f.exists() && !f.createNewFile()) {
                Assertions.fail("Unable to create file #" + i);
            }
        }

        System.out.println(dir.getName());
        try (BlobModel blob = metaContainer.createBlob(dir.getName())) {
            blob.uploadFile(dir);
        }
    }

    @AfterAll
    public void cleanup() {
        idBlob.close();
    }
}
