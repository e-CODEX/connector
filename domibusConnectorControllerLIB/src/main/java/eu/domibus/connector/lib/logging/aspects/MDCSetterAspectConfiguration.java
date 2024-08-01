/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.lib.logging.aspects;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.annotation.Order;

/**
 * Configuration class that declares and configures the MDCSetterAspect bean.
 */
@Configuration
@EnableAspectJAutoProxy
public class MDCSetterAspectConfiguration {
    @Bean
    @Order(1)
    MDCSetterAspect mdcSetterAspect() {
        return new MDCSetterAspect();
    }
}
