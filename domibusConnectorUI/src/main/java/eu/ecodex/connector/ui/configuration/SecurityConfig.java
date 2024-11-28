/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.ui.configuration;

import com.vaadin.flow.spring.security.VaadinWebSecurity;
import eu.ecodex.connector.spring.WebUserAuthenticationProvider;
import eu.ecodex.connector.ui.login.LoginView;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configures Spring Security.
 *
 * @author spindlest
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final WebUserAuthenticationProvider authProvider;

    public SecurityConfig(WebUserAuthenticationProvider authProvider) {
        this.authProvider = authProvider;
    }

    /**
     * Creates an Authentication Provider including authProvider.
     */
    @Bean
    public AuthenticationManager authenticationManager() {
        List<AuthenticationProvider> authProviders = new ArrayList<>();
        authProviders.add(this.authProvider);
        return new ProviderManager(authProviders);
    }

    /**
     * This class is a configuration class for securing the Actuator endpoints in a web application
     * using Spring Security.
     */
    @Configuration
    @Order(1)
    public static class ActuatorWebSecurityConfiguration {
        private static final String ACTUATOR_BASE_PATH = "actuator";

        @Bean
        protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            if (StringUtils.isNotEmpty(ACTUATOR_BASE_PATH)) {
                http
                    .securityMatcher("/" + ACTUATOR_BASE_PATH + "/**")
                    .authorizeHttpRequests(auth -> auth
                        .anyRequest()
                        .hasAnyRole("ACTUATOR", "ADMIN")
                    )
                    .httpBasic(Customizer.withDefaults());
            }
            return http.build();
        }
    }

    /**
     * Configuration class for Vaadin web security.
     *
     * <p>This class provides configuration for securing Vaadin web applications using Spring
     * Security. It configures the security filter chain and allows access to static resources.
     */
    @Configuration
    @Order(500)
    public static class VaadinWebSecurityConfiguration extends VaadinWebSecurity {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            super.configure(http);
            setLoginView(http, LoginView.class);
        }

        @Override
        public void configure(WebSecurity web) throws Exception {
            web.ignoring().requestMatchers(
                // the standard favicon URI
                "/favicon.ico",
                // icons and images
                "/icons/**",
                "/images/**",
                // (production mode) static resources
                "/frontend-es5/**", "/frontend-es6/**",
                // (development mode) static resources
                "/frontend/**",
                // (development mode) H2 debugging console
                "/h2-console/**",
                //allow access to webservices
                "/services/**",
                "/documentation/**" // allow access to documentation
            );
            super.configure(web);
        }
    }
}
