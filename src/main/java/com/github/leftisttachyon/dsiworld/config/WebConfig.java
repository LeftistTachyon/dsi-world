package com.github.leftisttachyon.dsiworld.config;

import com.github.leftisttachyon.dsiworld.interceptor.MemberInterceptor;
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
//        registry.addViewController("/").setViewName("index");

        // testing pages
        registry.addViewController("/test").setViewName("test/test");
        registry.addViewController("/test/style-test").setViewName("test/style-test");
        registry.addViewController("/test/input-detection").setViewName("test/input-detection");
        registry.addViewController("/test/jquery-ui-test").setViewName("test/jquery-ui");
        registry.addViewController("/test/js-playground").setViewName("test/js-playground");
        registry.addViewController("/test/framerate").setViewName("test/framerate");
        registry.addViewController("/test/input2").setViewName("test/input2");
        registry.addViewController("/test/input3").setViewName("test/input3");

        // site pages
//        registry.addViewController("/login").setViewName("login");

        // member pages
//        registry.addViewController("/member").setViewName("member/index");
    }

    /**
     * Adds handler interceptors to this application
     *
     * @param registry the {@link InterceptorRegistry} to add handler interceptors to
     * @see InterceptorRegistry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new MemberInterceptor());
    }
}
