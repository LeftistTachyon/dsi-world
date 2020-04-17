package com.github.leftisttachyon.dsiworld.config;

import com.github.leftisttachyon.dsiworld.util.SiteMappings;
import com.github.leftisttachyon.dsiworld.util.ViewNames;
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
        registry.addViewController(SiteMappings.HOME).setViewName(ViewNames.HOME);
        registry.addViewController(SiteMappings.TEST).setViewName(ViewNames.TEST);
        registry.addViewController(SiteMappings.STYLE_TEST).setViewName(ViewNames.STYLE_TEST);
        registry.addViewController(SiteMappings.INPUT_DETECTION).setViewName(ViewNames.INPUT_DETECTION);
        registry.addViewController(SiteMappings.JQUERY_UI_TEST).setViewName(ViewNames.JQUERY_UI);
        registry.addViewController(SiteMappings.JS_PLAYGROUND).setViewName(ViewNames.JS_PLAYGROUND);
        registry.addViewController(SiteMappings.FRAMERATE).setViewName(ViewNames.FRAMERATE);
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
