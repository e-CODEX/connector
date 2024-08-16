/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.common.spring;

import eu.domibus.connector.common.annotations.BusinessDomainScoped;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The BusinessDomainScopeConfiguration class is a configuration class for defining the
 * BusinessDomainScope bean in the application context. It also registers a BeanFactoryPostProcessor
 * for setting up the BusinessDomainScope.
 */
@Configuration
public class BusinessDomainScopeConfiguration {
    /**
     * The BusinessDomainScopeConfigurationBeanFactoryPostProcessor class is a
     * BeanFactoryPostProcessor implementation that sets up and registers the BusinessDomainScope in
     * the application context.
     */
    public static class BusinessDomainScopeConfigurationBeanFactoryPostProcessor
        implements BeanFactoryPostProcessor {
        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
            throws BeansException {
            var businessDomainScope = new BusinessDomainScope();
            beanFactory.registerScope(
                BusinessDomainScoped.DC_BUSINESS_DOMAIN_SCOPE_NAME, businessDomainScope);
            beanFactory.registerSingleton("businessDomainScopeMsg", businessDomainScope);
        }
    }

    @Bean
    public BeanFactoryPostProcessor businessDomainScopeConfigurationBeanFactoryPostProcessor() {
        return new BusinessDomainScopeConfigurationBeanFactoryPostProcessor();
    }
}
