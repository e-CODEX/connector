package eu.domibus.connector.common.spring;

import eu.domibus.connector.common.annotations.BusinessDomainScoped;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class BusinessDomainScopeConfiguration {
    @Bean
    public BeanFactoryPostProcessor businessDomainScopeConfigurationBeanFactoryPostProcessor() {
        return new BusinessDomainScopeConfigurationBeanFactoryPostProcessor();
    }

    public static class BusinessDomainScopeConfigurationBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
            BusinessDomainScope businessDomainScope = new BusinessDomainScope();
            beanFactory.registerScope(BusinessDomainScoped.DC_BUSINESS_DOMAIN_SCOPE_NAME, businessDomainScope);
            beanFactory.registerSingleton("businessDomainScopeMsg", businessDomainScope);
        }
    }
}
