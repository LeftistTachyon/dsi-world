package com.github.leftisttachyon.dsiworld.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import javax.annotation.PostConstruct;

/**
 * A configuration class to set various things up for the application.
 *
 * @author Tim Bulchaka, Jed Wang
 * @since 1.0.0
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private RequestMappingHandlerAdapter requestMappingHandlerAdapter;

    /**
     * Sets <code>setIgnoreDefaultModelOnRedirect</code> to true
     */
    @PostConstruct
    public void init() {
        requestMappingHandlerAdapter.setIgnoreDefaultModelOnRedirect(true);
    }

    /**
     * Sets the base url as the home page
     *
     * @param registry the {@link ViewControllerRegistry} to use to configure
     *                 views
     * @see ViewControllerRegistry
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("index");
        registry.addViewController("/test").setViewName("test");
        registry.addViewController("/style-test").setViewName("style-test");
        registry.addViewController("/input-detection").setViewName("input-detection");
        registry.addViewController("/jquery-ui-test").setViewName("jquery-ui");
        registry.addViewController("/js-playground").setViewName("js-playground");
        registry.addViewController("/framerate").setViewName("framerate");
        registry.addViewController("/input2").setViewName("input2");
        registry.addViewController("/input3").setViewName("input3");
    }

    /**
     * Adds handler interceptors to this application
     *
     * @param registry the {@link InterceptorRegistry} to add handler
     *                 interceptors to
     * @see InterceptorRegistry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(new MemberInterceptor());
    }
}
