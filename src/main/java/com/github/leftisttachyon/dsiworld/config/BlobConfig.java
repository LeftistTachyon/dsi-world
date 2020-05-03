package com.github.leftisttachyon.dsiworld.config;

import com.github.leftisttachyon.dsiworld.model.BlobModel;
import com.github.leftisttachyon.dsiworld.model.ContainerModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * A configuration class that handles blob beans.
 *
 * @author Jed Wang
 * @since 1.0.0
 */
@Configuration
@ComponentScan(basePackages = "com.github.leftisttachyon.dsiworld")
public final class BlobConfig {
    /**
     * No instantiation >:(
     */
    private BlobConfig() {
    }

    /**
     * Returns the singleton instance of the meta storage container
     *
     * @return the singleton instance of the meta storage container
     */
    @Bean
    @Scope("singleton")
    public ContainerModel getMetaContainer() {
        return null; // TODO
    }

    /**
     * Returns the singleton instance of the ID blob
     *
     * @return the singleton instance of the ID blob
     */
    @Bean
    @Scope("singleton")
    public BlobModel getIdBlob() {
        return null; // TODO
    }
}
