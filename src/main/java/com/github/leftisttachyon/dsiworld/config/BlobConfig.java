package com.github.leftisttachyon.dsiworld.config;

import com.github.leftisttachyon.dsiworld.model.BlobModel;
import com.github.leftisttachyon.dsiworld.model.ContainerModel;
import com.github.leftisttachyon.dsiworld.model.ContainerModelFactory;
import com.github.leftisttachyon.dsiworld.util.BeanAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.ApplicationScope;

/**
 * A configuration class that handles blob beans.
 *
 * @author Jed Wang
 * @since 1.0.0
 */
@Configuration
@ComponentScan(basePackages = "com.github.leftisttachyon.dsiworld")
public class BlobConfig {
    /**
     * The {@link ContainerModelFactory} to use to create {@link ContainerModel}s
     */
    private final ContainerModelFactory factory;

    /**
     * Creates a new BlobConfig object
     *
     * @param factory the {@link ContainerModelFactory} to use to manufacture {@link ContainerModel}s
     */
    @Autowired
    public BlobConfig(ContainerModelFactory factory) {
        this.factory = factory;
    }

    /**
     * Returns the singleton instance of the meta storage container
     *
     * @return the singleton instance of the meta storage container
     */
    @Bean
    @ApplicationScope
    @BeanAnnotations.MetaContainer
    public ContainerModel getMetaContainer() {
        return factory.createContainerModel("meta-info");
    }

    /**
     * Returns the singleton instance of the ID blob
     *
     * @param metaContainer the {@link ContainerModel} to create the blob from
     * @return the singleton instance of the ID blob
     */
    @Bean
    @Autowired
    @ApplicationScope
    @BeanAnnotations.IdBlob
    public BlobModel getIdBlob(@BeanAnnotations.MetaContainer ContainerModel metaContainer) {
        return metaContainer.createBlob("used-ids.dat");
    }

    /**
     * Returns the single instance of the user info blob
     *
     * @param metaContainer the {@link ContainerModel} to create the blob from
     * @return the single instance of the user info blob
     */
    @Bean
    @Autowired
    @ApplicationScope
    @BeanAnnotations.UserInfoBlob
    public BlobModel getUserInfoBlob(@BeanAnnotations.MetaContainer ContainerModel metaContainer) {
        return metaContainer.createBlob("user-info.dat");
    }
}
