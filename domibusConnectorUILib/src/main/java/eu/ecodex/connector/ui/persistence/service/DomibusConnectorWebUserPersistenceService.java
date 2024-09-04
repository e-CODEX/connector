/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.ui.persistence.service;

import eu.ecodex.connector.ui.dto.WebUser;
import eu.ecodex.connector.ui.exception.InitialPasswordException;
import eu.ecodex.connector.ui.exception.UserLoginException;
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
