/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.ui.service;

import eu.domibus.connector.ui.dto.WebUser;
import eu.domibus.connector.ui.exception.InitialPasswordException;
import eu.domibus.connector.ui.exception.UserLoginException;
import eu.domibus.connector.ui.persistence.service.DomibusConnectorWebUserPersistenceService;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The WebUserService class is responsible for managing web users in a web application. It provides
 * methods for performing operations related to user management, such as getting all users,
 * resetting user passwords, creating new users, and saving user data.
 *
 * @see DomibusConnectorWebUserPersistenceService
 */
@Service("webUserService")
@NoArgsConstructor
public class WebUserService {
    protected static final Logger LOGGER = LoggerFactory.getLogger(WebUserService.class);
    private DomibusConnectorWebUserPersistenceService persistenceService;

    @Autowired
    public void setPersistenceService(
        DomibusConnectorWebUserPersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    public List<WebUser> getAllUsers() {
        return persistenceService.listAllUsers();
    }

    /**
     * Resets the password of a user to a new initial password.
     *
     * @param user The user whose password needs to be reset.
     * @param newInitialPassword The new initial password to be set for the user.
     * @return {@code true} if the password reset is successful, otherwise {@code false}.
     * @throws RuntimeException if there is an error during the password reset process.
     */
    @Transactional(value = "transactionManager")
    public boolean resetUserPassword(WebUser user, String newInitialPassword) {
        LOGGER.debug(
            "resetUserPassword called for user [{}] with new initial Password [{}]",
            user.getUsername(), newInitialPassword
        );

        WebUser resetUser;
        try {
            resetUser = persistenceService.resetUserPassword(user, newInitialPassword);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
        return resetUser != null;
    }

    /**
     * Creates a new user in the system.
     *
     * @param newUser The user to be created.
     * @return {@code true} if the user is successfully created, {@code false} otherwise.
     */
    @Transactional(value = "transactionManager")
    public boolean createNewUser(WebUser newUser) {
        WebUser user;
        try {
            user = persistenceService.createNewUser(newUser);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
        return user != null;
    }

    /**
     * Saves a user in the system.
     *
     * @param user The user to be saved.
     * @return {@code true} if the user is successfully saved, {@code false} otherwise.
     */
    @Transactional(value = "transactionManager")
    public boolean saveUser(WebUser user) {
        WebUser updated = persistenceService.updateUser(user);
        return updated != null;
    }

    /**
     * The login method is used to perform user login in the system. It takes a username and
     * password as input parameters. If the user authentication is successful, it sets the
     * authenticated user in the security context. Otherwise, it throws a UserLoginException or
     * InitialPasswordException.
     *
     * @param username the username of the user
     * @param password the password of the user
     * @throws UserLoginException       if there is an error during the user login process
     * @throws InitialPasswordException if the user attempts to log in with an initial password that
     *                                  has not been changed
     */
    public void login(String username, String password)
        throws UserLoginException, InitialPasswordException {
        WebUser user = persistenceService.login(username, password);
        if (user != null) {
            var context = SecurityContextHolder.getContext();
            var authentication = new UsernamePasswordAuthenticationToken(user, null);
            context.setAuthentication(authentication);
        }
    }

    /**
     * Changes the password and performs login for a specific user.
     *
     * @param username    the username of the user
     * @param oldPassword the old password of the user
     * @param newPassword the new password to be set for the user
     * @throws UserLoginException if there is an error during the user login process
     */
    public void changePasswordLogin(String username, String oldPassword, String newPassword)
        throws UserLoginException {
        WebUser user = persistenceService.changePassword(username, oldPassword, newPassword);
        if (user != null) {
            var context = SecurityContextHolder.getContext();
            var authentication = new UsernamePasswordAuthenticationToken(user, null);
            context.setAuthentication(authentication);
        }
    }
}
