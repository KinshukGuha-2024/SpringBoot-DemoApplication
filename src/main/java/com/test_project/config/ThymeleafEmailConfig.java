package com.test_project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

@Configuration
public class ThymeleafEmailConfig {
    @Bean
    public ClassLoaderTemplateResolver emailTemplateResolver() {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML");
        resolver.setCharacterEncoding("UTF-8");
        resolver.setCacheable(true);
        resolver.setOrder(1);
        return resolver;
    }

    @Bean
    public TemplateEngine emailTemplateEngine(ClassLoaderTemplateResolver emailTemplateResolver) {
        TemplateEngine engine = new TemplateEngine();
        engine.setTemplateResolver(emailTemplateResolver);
        return engine;
    }
}
