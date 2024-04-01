package eu.domibus.connector.ui.configuration;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import eu.domibus.connector.ui.login.LoginView;
import eu.domibus.connector.ui.view.AccessDeniedView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


@Component
public class ConfigureUIServiceInitListener implements VaadinServiceInitListener {
    private static final Logger LOGGER = LogManager.getLogger(ConfigureUIServiceInitListener.class);

    private final ConnectorUiConfigurationProperties connectorUiConfigurationProperties;
    private final AuthenticationManager authenticationManager;

    public ConfigureUIServiceInitListener(
            ConnectorUiConfigurationProperties connectorUiConfigurationProperties,
            AuthenticationManager authenticationManager) {
        this.connectorUiConfigurationProperties = connectorUiConfigurationProperties;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public void serviceInit(ServiceInitEvent event) {
        event.getSource().addUIInitListener(uiEvent -> {
            final UI ui = uiEvent.getUI();
            ui.addBeforeEnterListener(this::beforeEnter);
        });
    }

    /**
     * Reroutes the user if (s)he is not authorized to access the view.
     *
     * @param event before navigation event with event details
     */
    private void beforeEnter(BeforeEnterEvent event) {
        if (connectorUiConfigurationProperties.isAutoLoginEnabled()) {
            LOGGER.warn("Auto Login is enabled! Only use this in development!");
            Authentication authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            connectorUiConfigurationProperties.getAutoLoginUser(),
                            connectorUiConfigurationProperties.getAutoLoginPassword()
                    ));
            SecurityContextHolder.getContext().setAuthentication(authenticate);
        }

        if (!LoginView.class.equals(event.getNavigationTarget())
                && !SecurityUtils.isUserLoggedIn()) {
            String path = event.getLocation().getPath();
            // should work after vaadin lib update
            // after Login forward to previous page
            // RouteParameters p = new RouteParameters(LoginView.PREVIOUS_ROUTE_PARAMETER, path);
            // event.forwardTo(LoginView.class, p);
            event.forwardTo(LoginView.class);
        }
        Class<?> navigationTarget = event.getNavigationTarget();
        if (!SecurityUtils.isUserAllowedToView(navigationTarget)) {
            event.forwardTo(AccessDeniedView.class);
        }
        // forward to default page, used for development
        if (connectorUiConfigurationProperties.isAutoLoginEnabled() &&
                LoginView.class.equals(event.getNavigationTarget()) &&
                StringUtils.hasText(connectorUiConfigurationProperties.getDefaultRoute())) {
            event.forwardTo(connectorUiConfigurationProperties.getDefaultRoute());
        }
    }
}
