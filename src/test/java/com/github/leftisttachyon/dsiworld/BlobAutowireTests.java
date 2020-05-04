package com.github.leftisttachyon.dsiworld;

import com.github.leftisttachyon.dsiworld.model.BlobModel;
import com.github.leftisttachyon.dsiworld.model.ContainerModel;
import com.github.leftisttachyon.dsiworld.util.BeanAnnotations;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
     * Testing whether the container exists.
     */
    @Test
    public void containerWireTest() {
        Assertions.assertNotNull(metaContainer);
    }

    /**
     * The id blob
     */
    @Autowired
    @BeanAnnotations.IdBlob
    private BlobModel idBlob;

    /**
     * Testing whether the blob exists.
     */
    @Test
    public void blobWireTest() {
        Assertions.assertNotNull(idBlob);
    }
}
