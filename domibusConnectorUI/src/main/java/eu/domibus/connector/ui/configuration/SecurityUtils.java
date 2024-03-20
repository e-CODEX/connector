package eu.domibus.connector.ui.configuration;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.server.ServletHelper.RequestType;
import com.vaadin.flow.shared.ApplicationConstants;
import eu.domibus.connector.tools.logging.LoggingMarker;
import eu.domibus.connector.ui.dto.WebUser;
import eu.domibus.connector.ui.utils.RoleRequired;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.util.stream.Stream;

/**
 * SecurityUtils takes care of all such static operations that have to do with
 * security and querying rights from different beans of the UI.
 *
 */
public final class SecurityUtils {

    private static final Logger LOGGER = LogManager.getLogger(SecurityUtils.class);

    private SecurityUtils() {
        // Util methods only
    }

    /**
     * Tests if the request is an internal framework request. The test consists of
     * checking if the request parameter is present and if its value is consistent
     * with any of the request types know.
     *
     * @param request
     *            {@link HttpServletRequest}
     * @return true if is an internal framework request. False otherwise.
     */
    static boolean isFrameworkInternalRequest(HttpServletRequest request) {
        final String parameterValue = request.getParameter(ApplicationConstants.REQUEST_TYPE_PARAMETER);
        return parameterValue != null
                && Stream.of(RequestType.values()).anyMatch(r -> r.getIdentifier().equals(parameterValue));
    }

    /**
     * Tests if some user is authenticated. As Spring Security always will create an {@link AnonymousAuthenticationToken}
     * we have to ignore those tokens explicitly.
     */
    public static boolean isUserLoggedIn() {
        Authentication authentication = getAuthentication();
        return authentication != null
                && !(authentication instanceof AnonymousAuthenticationToken)
                && authentication.isAuthenticated();
    }

    public static boolean isUserAllowedToView(Class<?> viewClass) {

        RoleRequired annotation = AnnotationUtils.findAnnotation(viewClass, RoleRequired.class);
        if (annotation != null) {
            String role = annotation.role();
            LOGGER.debug("#isUserAllowedToView: checking if user is in requiredRole [{}] of view [{}]", role, viewClass);
            return isUserInRole(role);
        }
        LOGGER.debug("#isUserAllowedToView: View [{}] has no required role, returning true", viewClass);
        return true;
    }

    public static String getUsername() {
        Authentication authentication = getAuthentication();
        Object principal = authentication.getPrincipal();
        if (principal == null) {
            return "";
        }
        if (principal instanceof WebUser) {
            return ((WebUser) principal).getUsername();
        } else {
            return principal.toString();
        }
    }

    public static boolean isUserInRole(String role) {
        boolean userHasRole = false;
        if (isUserLoggedIn()) {
            Authentication authentication = getAuthentication();

            userHasRole = authentication.getAuthorities()
                    .stream()
                    .anyMatch(grantedAuthority -> ("ROLE_" + role).equals(((GrantedAuthority) grantedAuthority).getAuthority()));

            LOGGER.trace("User [{}] has roles [{}]", authentication.getPrincipal(), authentication.getAuthorities());
        }

        LOGGER.debug("Check if user is logged in and has role [{}] returned [{}]", role, userHasRole);
        return userHasRole;
    }

    private static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }


}
