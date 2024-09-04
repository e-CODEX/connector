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

import com.vaadin.flow.server.HandlerHelper;
import com.vaadin.flow.shared.ApplicationConstants;
import eu.ecodex.connector.ui.dto.WebUser;
import eu.ecodex.connector.ui.utils.RoleRequired;
import jakarta.servlet.http.HttpServletRequest;
import java.util.stream.Stream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * SecurityUtils takes care of all such static operations that have to do with security and querying
 * rights from different beans of the UI.
 */
public final class SecurityUtils {
    private static final Logger LOGGER = LogManager.getLogger(SecurityUtils.class);

    private SecurityUtils() {
        // Util methods only
    }

    /**
     * Tests if the request is an internal framework request. The test consists of checking if the
     * request parameter is present and if its value is consistent with any of the request types
     * know.
     *
     * @param request {@link HttpServletRequest}
     * @return true if is an internal framework request. False otherwise.
     */
    static boolean isFrameworkInternalRequest(HttpServletRequest request) {
        final String parameterValue =
            request.getParameter(ApplicationConstants.REQUEST_TYPE_PARAMETER);
        return parameterValue != null
            && Stream.of(HandlerHelper.RequestType.values())
                     .anyMatch(r -> r.getIdentifier().equals(parameterValue));
    }

    /**
     * Tests if some user is authenticated. As Spring Security always will create an
     * {@link AnonymousAuthenticationToken} we have to ignore those tokens explicitly.
     */
    public static boolean isUserLoggedIn() {
        var authentication = getAuthentication();
        return authentication != null
            && !(authentication instanceof AnonymousAuthenticationToken)
            && authentication.isAuthenticated();
    }

    /**
     * Checks if the user is allowed to view the specified view class.
     *
     * @param viewClass the view class to check
     * @return true if the user is allowed to view the class, false otherwise
     */
    public static boolean isUserAllowedToView(Class<?> viewClass) {
        RoleRequired annotation = AnnotationUtils.findAnnotation(viewClass, RoleRequired.class);
        if (annotation != null) {
            String role = annotation.role();
            LOGGER.debug(
                "#isUserAllowedToView: checking if user is in requiredRole [{}] of view [{}]", role,
                viewClass
            );
            return isUserInRole(role);
        }
        LOGGER.debug(
            "#isUserAllowedToView: View [{}] has no required role, returning true", viewClass);
        return true;
    }

    /**
     * Retrieves the username of the authenticated user.
     *
     * <p>This method first retrieves the authentication object using the
     * {@link SecurityContextHolder}. It then checks if the principal is null. If it is null, an
     * empty string is returned. If the principal is an instance of {@link WebUser}, the username is
     * obtained using the {@link WebUser#getUsername()} method. Otherwise, the toString() method is
     * called on the principal to obtain the username.
     *
     * @return the username of the authenticated user, or an empty string if the user is not
     *      authenticated
     */
    public static String getUsername() {
        var authentication = getAuthentication();
        var principal = authentication.getPrincipal();
        if (principal == null) {
            return "";
        }
        if (principal instanceof WebUser webUser) {
            return webUser.getUsername();
        } else {
            return principal.toString();
        }
    }

    /**
     * Checks if the user is in the specified role.
     *
     * @param role the role to check if the user has
     * @return true if the user has the specified role, false otherwise
     */
    public static boolean isUserInRole(String role) {
        var userHasRole = false;
        if (isUserLoggedIn()) {
            var authentication = getAuthentication();
            userHasRole = authentication
                .getAuthorities()
                .stream()
                .anyMatch(
                    grantedAuthority -> ("ROLE_" + role).equals(grantedAuthority.getAuthority())
                );

            LOGGER.trace(
                "User [{}] has roles [{}]", authentication.getPrincipal(),
                authentication.getAuthorities()
            );
        }

        LOGGER.debug(
            "Check if user is logged in and has role [{}] returned [{}]", role, userHasRole);
        return userHasRole;
    }

    private static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
