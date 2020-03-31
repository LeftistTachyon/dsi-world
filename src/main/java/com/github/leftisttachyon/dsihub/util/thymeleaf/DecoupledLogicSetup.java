package com.github.leftisttachyon.dsihub.util.thymeleaf;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;

import javax.annotation.PostConstruct;

/**
 * A class to enable decoupled logic in Thymeleaf.
 *
 * @author Tim Buchalka
 */
@Slf4j
@Component
public final class DecoupledLogicSetup {
    // == fields ==
    private final SpringResourceTemplateResolver templateResolver;

    // == constructor ==
    @Autowired
    public DecoupledLogicSetup(SpringResourceTemplateResolver templateResolver) {
        this.templateResolver = templateResolver;
    }

    // == init ==
    @PostConstruct
    public void init() {
        templateResolver.setUseDecoupledLogic(true);
        log.info("Decoupled template logic enabled");
    }
}
