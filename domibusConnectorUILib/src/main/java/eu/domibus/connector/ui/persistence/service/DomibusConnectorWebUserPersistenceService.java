/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.ui.persistence.service;

import eu.domibus.connector.ui.dto.WebUser;
import eu.domibus.connector.ui.exception.InitialPasswordException;
import eu.domibus.connector.ui.exception.UserLoginException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

/**
 * The DomibusConnectorWebUserPersistenceService interface provides methods for managing web users
 * in the Domibus Connector application.
 */
public interface DomibusConnectorWebUserPersistenceService {
    WebUser login(String username, String password)
        throws UserLoginException, InitialPasswordException;

    List<WebUser> listAllUsers();

    WebUser resetUserPassword(WebUser user, String newInitialPassword)
        throws NoSuchAlgorithmException, InvalidKeySpecException;

    WebUser createNewUser(WebUser newUser) throws NoSuchAlgorithmException, InvalidKeySpecException;

    WebUser updateUser(WebUser user);

    WebUser changePassword(String username, String oldPassword, String newPassword)
        throws UserLoginException;
}
