/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
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
