package eu.domibus.connector.ui.configuration;

import com.vaadin.flow.spring.security.VaadinWebSecurity;
import eu.domibus.connector.spring.WebUserAuthenticationProvider;
import eu.domibus.connector.ui.login.LoginView;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * Configures Spring Security
 *
 * @author spindlest
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends VaadinWebSecurity {

    private final static Logger LOG = LoggerFactory.getLogger(SecurityConfig.class);

    final
    WebUserAuthenticationProvider authProvider;

    @Autowired
    public SecurityConfig(WebUserAuthenticationProvider authProvider) {
        this.authProvider = authProvider;
    }


    /**
     * creates a Authentication Provider
     * including authProvider
     */
    @Bean
    public AuthenticationManager authenticationManager() {
        List<AuthenticationProvider> authProviders = new ArrayList<>();
        authProviders.add(authProvider);
        return new ProviderManager(authProviders);
    }

    private String actuatorBasePath = "actuator";

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Delegating the responsibility of general configurations
        // of http security to the super class. It's configuring
        // the followings: Vaadin's CSRF protection by ignoring
        // framework's internal requests, default request cache,
        // ignoring public views annotated with @AnonymousAllowed,
        // restricting access to other views/endpoints, and enabling
        // NavigationAccessControl authorization.
        // You can add any possible extra configurations of your own
        // here (the following is just an example):

        // http.rememberMe().alwaysRemember(false);

        // Configure your static resources with public access before calling
        // super.configure(HttpSecurity) as it adds final anyRequest matcher
        http.authorizeHttpRequests(auth -> {
            auth.requestMatchers(new AntPathRequestMatcher("/public/**"))
                    .permitAll();
        });
        http.securityMatcher("/" + actuatorBasePath + "/**")
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/" + actuatorBasePath + "/**")
                        .hasAnyRole("ACTUATOR", "ADMIN")
                ).httpBasic(withDefaults());

        super.configure(http);



        // This is important to register your login view to the
        // navigation access control mechanism:
        setLoginView(http, LoginView.class);

    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        // Customize your WebSecurity configuration.
        super.configure(web);
    }

}

