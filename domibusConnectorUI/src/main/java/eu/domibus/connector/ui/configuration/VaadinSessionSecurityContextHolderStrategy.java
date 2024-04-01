package eu.domibus.connector.ui.configuration;

import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.SpringVaadinSession;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.context.SecurityContextImpl;


/**
 * A custom {@link SecurityContextHolderStrategy} that stores the {@link SecurityContext} in the Vaadin Session.
 */
public class VaadinSessionSecurityContextHolderStrategy implements SecurityContextHolderStrategy {
    private static VaadinSession getSession() {
        VaadinSession session = SpringVaadinSession.getCurrent();
        if (session == null) {
            throw new IllegalStateException("No VaadinSession bound to current thread");
        }
        return session;
    }

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
}
