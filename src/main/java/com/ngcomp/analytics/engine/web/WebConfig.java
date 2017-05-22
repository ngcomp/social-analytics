package com.ngcomp.analytics.engine.web;

import com.mangofactory.swagger.configuration.DocumentationConfig;
import com.ngcomp.analytics.engine.config.PropertyPlaceholderConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * TumblerUser: Ram Parashar
 * Date: 7/20/13
 * Time: 11:38 AM
 */

@Configuration
@EnableWebMvc
@EnableAsync
@Import({PropertyPlaceholderConfig.class, DocumentationConfig.class})
public class WebConfig extends WebMvcConfigurerAdapter
{

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry)
    {
        registry.addResourceHandler("/**").addResourceLocations("classpath:/assets/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry)
    {
        registry.addViewController("/"    ).setViewName("redirect:index.html");
        registry.addViewController("/docs").setViewName("redirect:docs/index.html");
    }

}