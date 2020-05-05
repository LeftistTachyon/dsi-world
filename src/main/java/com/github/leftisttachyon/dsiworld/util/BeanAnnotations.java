package com.github.leftisttachyon.dsiworld.util;

import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A final class that contains annotations to be used as bean markers.
 *
 * @author Jed Wang
 * @since 1.0.0
 */
public final class BeanAnnotations {
    /**
     * Yo gabba gabba
     */
    private BeanAnnotations() {
    }

    @Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Qualifier
    public @interface MetaContainer {
    }

    @Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Qualifier
    public @interface IdBlob {
    }

    @Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Qualifier
    public @interface UserInfoBlob {
    }
}
