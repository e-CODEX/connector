/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.persistence.spring;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class that initializes the DatabaseResourceLoader. Implements the
 * ApplicationContextInitializer interface.
 */
@Configuration
public class DatabaseResourceLoaderConfiguration
    implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    //    private ResourceLoader resourceLoader;
    //    private ApplicationContext applicationContext;

    //    @Override
    //    public Object postProcessBeforeInitialization(Object bean, String beanName) {
    //        if (bean instanceof ResourceLoaderAware) {
    //            ((ResourceLoaderAware)bean).
    //                    setResourceLoader(this.resourceLoader);
    //        }
    //        return bean;
    //    }

    //    @Override
    //    public Object postProcessAfterInitialization(Object bean, String beanName) {
    //        return bean;
    //    }

    //    @Override
    //    public void postProcessBeanFactory(
    //            ConfigurableListableBeanFactory beanFactory) {
    //        this.resourceLoader =
    //                new DatabaseResourceLoader(this.applicationContext, this.resourceLoader);
    //        beanFactory.registerResolvableDependency(ResourceLoader.class, this.resourceLoader);
    //    }

    //    @Override
    //    public int getOrder() {
    //        return Ordered.HIGHEST_PRECEDENCE;
    //    }

    //    @Override
    //    public void setResourceLoader(
    //            ResourceLoader resourceLoader) {
    //        this.resourceLoader = resourceLoader;
    //    }

    //    @Override
    //    public void setApplicationContext(
    //            ApplicationContext applicationContext) {
    //        this.applicationContext = applicationContext;
    //    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {

        applicationContext.addProtocolResolver((location, resourceLoader) -> {
            if (location.startsWith(DatabaseResourceLoader.DB_URL_PREFIX)) {
                DatabaseResourceLoader r =
                    applicationContext.getBean(DatabaseResourceLoader.class);
                return r.getResource(location);
            }
            return null;
        });
    }
}
