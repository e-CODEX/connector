/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.spring;

import eu.domibus.connector.ui.dto.WebUser;
import eu.domibus.connector.ui.persistence.service.DomibusConnectorWebUserPersistenceService;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

/**
 * Uses the IDomibusWebAdminUserDao to get the users from DB.
 *
 * @author spindlest
 */
@Service
public class WebUserAuthenticationProvider implements AuthenticationProvider {
    private static final Logger LOG = LoggerFactory.getLogger(WebUserAuthenticationProvider.class);
    private DomibusConnectorWebUserPersistenceService webUserPersistenceService;

    @Autowired
    public void setWebUserPersistenceService(
        DomibusConnectorWebUserPersistenceService webUserPersistenceService) {
        this.webUserPersistenceService = webUserPersistenceService;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Authentication authenticate(Authentication authentication)
        throws AuthenticationException {
        if (authentication == null) {
            throw new IllegalArgumentException("authentication is not allowed to be null!");
        }
        LOG.trace("authenticate: called");

        UsernamePasswordAuthenticationToken pwAuth =
            (UsernamePasswordAuthenticationToken) authentication;

        if (pwAuth.getCredentials() == null) {
            LOG.info("Authentication has no credentials!");
            return authentication;
        }

        String username = "" + pwAuth.getPrincipal();
        String password = "" + pwAuth.getCredentials();

        LOG.trace("authenticate: username is [{}], password is [{}]", username, password);

        WebUser user;
        user = webUserPersistenceService.login(username, password);

        if (user == null) {
            throw new BadCredentialsException("username or password incorrect!");
        }
        LOG.debug("authenticated user [{}]  successfully]", username);

        var grantedAuthorities = Stream.of(user.getRole())
                                       .map(Object::toString)
                                       .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                                       .toList();

        return new UsernamePasswordAuthenticationToken(
            username, password, grantedAuthorities
        );
    }

    /**
     * {@inheritDoc}
     *
     * <p>Only username + password authentication is supported
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return (authentication == UsernamePasswordAuthenticationToken.class);
    }
}
