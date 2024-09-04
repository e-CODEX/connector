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

import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.SpringVaadinSession;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.context.SecurityContextImpl;

/**
 * A custom {@link SecurityContextHolderStrategy} that stores the {@link SecurityContext} in the
 * Vaadin Session.
 */
public class VaadinSessionSecurityContextHolderStrategy implements SecurityContextHolderStrategy {
    @Override
    public void clearContext() {
        getSession().setAttribute(SecurityContext.class, null);
    }

    @Override
    public SecurityContext getContext() {
        VaadinSession session = getSession();
        SecurityContext context = session.getAttribute(SecurityContext.class);
        if (context == null) {
            context = createEmptyContext();
            session.setAttribute(SecurityContext.class, context);
        }
        return context;
    }

    @Override
    public void setContext(SecurityContext context) {
        getSession().setAttribute(SecurityContext.class, context);
    }

    @Override
    public SecurityContext createEmptyContext() {
        return new SecurityContextImpl();
    }

    private static VaadinSession getSession() {
        var session = SpringVaadinSession.getCurrent();
        if (session == null) {
            throw new IllegalStateException("No VaadinSession bound to current thread");
        }
        return session;
    }
}
