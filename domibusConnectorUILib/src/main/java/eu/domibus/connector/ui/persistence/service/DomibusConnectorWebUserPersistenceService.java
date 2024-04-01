package eu.domibus.connector.ui.persistence.service;

import eu.domibus.connector.ui.dto.WebUser;
import eu.domibus.connector.ui.exception.InitialPasswordException;
import eu.domibus.connector.ui.exception.UserLoginException;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;


public interface DomibusConnectorWebUserPersistenceService {
    WebUser login(String username, String password) throws UserLoginException, InitialPasswordException;

    List<WebUser> listAllUsers();

    WebUser resetUserPassword(
            WebUser user,
            String newInitialPassword) throws NoSuchAlgorithmException, InvalidKeySpecException;

    WebUser createNewUser(WebUser newUser) throws NoSuchAlgorithmException, InvalidKeySpecException;

    WebUser updateUser(WebUser user);

    WebUser changePassword(String username, String oldPassword, String newPassword) throws UserLoginException;
}
