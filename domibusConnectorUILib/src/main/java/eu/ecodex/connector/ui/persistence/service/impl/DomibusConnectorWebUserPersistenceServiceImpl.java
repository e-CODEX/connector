/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.ui.persistence.service.impl;

import eu.ecodex.connector.persistence.dao.DomibusConnectorUserDao;
import eu.ecodex.connector.persistence.dao.DomibusConnectorUserPasswordDao;
import eu.ecodex.connector.persistence.model.PDomibusConnectorUser;
import eu.ecodex.connector.persistence.model.PDomibusConnectorUserPassword;
import eu.ecodex.connector.ui.dto.WebUser;
import eu.ecodex.connector.ui.enums.UserRole;
import eu.ecodex.connector.ui.exception.InitialPasswordException;
import eu.ecodex.connector.ui.exception.UserLoginException;
import eu.ecodex.connector.ui.persistence.service.DomibusConnectorWebUserPersistenceService;
import jakarta.xml.bind.DatatypeConverter;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.util.CollectionUtils;

/**
 * This class is the implementation of the {@link DomibusConnectorWebUserPersistenceService}
 * interface. It provides methods for managing web user persistence in the DomibusConnector
 * application.
 */
@org.springframework.stereotype.Service("webUserPersistenceService")
public class DomibusConnectorWebUserPersistenceServiceImpl
    implements DomibusConnectorWebUserPersistenceService {
    private DomibusConnectorUserDao userDao;
    private DomibusConnectorUserPasswordDao passwordDao;

    /*
     * DAO SETTER
     */
    @Autowired
    public void setUserDao(DomibusConnectorUserDao userDao) {
        this.userDao = userDao;
    }

    /*
     * DAO SETTER
     */
    @Autowired
    public void setPasswordDao(DomibusConnectorUserPasswordDao passwordDao) {
        this.passwordDao = passwordDao;
    }

    @Override
    public WebUser login(String username, String password)
        throws UserLoginException, InitialPasswordException {
        PDomibusConnectorUser user = getAndCheckGivenUser(username, password);
        return mapDbUserToWebUser(user);
    }

    private PDomibusConnectorUser getAndCheckGivenUser(String username, String password)
        throws UserLoginException, InitialPasswordException {
        PDomibusConnectorUser user = userDao.findOneByUsernameIgnoreCase(username);
        if (user != null) {
            if (user.isLocked()) {
                throw new UserLoginException(
                    "The user is locked! Please contact your administrator!");
            }

            PDomibusConnectorUserPassword currentPassword;
            try {
                currentPassword = this.passwordDao.findCurrentByUser(user);
            } catch (IncorrectResultSizeDataAccessException e) {
                throw new UserLoginException(
                    "The user has more than one current passwords! Please contact your "
                        + "administrator!"
                );
            }
            if (currentPassword == null) {
                throw new UserLoginException(
                    "The user has no current password! Please contact your administrator!");
            }

            String encrypted;
            try {
                encrypted = generatePasswordHashWithSaltOnlyPW(password, currentPassword.getSalt());
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                throw new UserLoginException(
                    "The user could not be logged in! Please try again later or contact your "
                        + "administrator!"
                );
            }

            if (!encrypted.equals(currentPassword.getPassword())) {
                var graceLoginsUsed = user.getGraceLoginsUsed();
                var numberOfGraceLogins = user.getNumberOfGraceLogins();

                graceLoginsUsed = graceLoginsUsed + 1;
                user.setGraceLoginsUsed(user.getGraceLoginsUsed() + 1);
                if (graceLoginsUsed >= numberOfGraceLogins) {
                    user.setLocked(true);
                    userDao.save(user);
                    throw new UserLoginException(
                        "The given password is not correct! The user is locked now!");
                } else {
                    userDao.save(user);
                    throw new UserLoginException(
                        "The given password is not correct! You have " + (numberOfGraceLogins
                            - graceLoginsUsed
                        ) + " grace logins left.");
                }
            }

            if (currentPassword.isInitialPassword()) {
                throw new InitialPasswordException("Initial password must be changed!");
            }

            if (user.getGraceLoginsUsed() > 0) {
                user.setGraceLoginsUsed(0L);
                userDao.save(user);
            }

            return user;
        }
        throw new UserLoginException("Cannot find given User!");
    }

    @Override
    public WebUser changePassword(String username, String oldPassword, String newPassword)
        throws UserLoginException {
        PDomibusConnectorUser user;
        try {
            user = getAndCheckGivenUser(username, oldPassword);
        } catch (InitialPasswordException e) {
            // This is expected here!
            user = userDao.findOneByUsernameIgnoreCase(username);
        }

        if (user != null) {
            var newDbPassword = new PDomibusConnectorUserPassword();
            newDbPassword.setUser(user);
            newDbPassword.setCurrentPassword(true);
            newDbPassword.setInitialPassword(false);

            String salt;
            String passwordDB;
            try {
                salt = getHexSalt();
                passwordDB = generatePasswordHashWithSaltOnlyPW(newPassword, salt);
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                throw new UserLoginException(
                    "The password could not be changed! Please try again later or contact your "
                        + "administrator!"
                );
            }

            newDbPassword.setSalt(salt);
            newDbPassword.setPassword(passwordDB);

            newDbPassword = passwordDao.save(newDbPassword);

            user.getPasswords().add(newDbPassword);

            var currentPassword = this.passwordDao.findCurrentByUser(user);
            currentPassword.setCurrentPassword(false);

            passwordDao.save(currentPassword);

            user = userDao.save(user);

            return mapDbUserToWebUser(user);
        }
        return null;
    }

    @Override
    public List<WebUser> listAllUsers() {
        Iterable<PDomibusConnectorUser> allUsers = this.userDao.findAll();
        return mapUsersToDto(allUsers);
    }

    @Override
    public WebUser resetUserPassword(WebUser user, String newInitialPassword)
        throws NoSuchAlgorithmException, InvalidKeySpecException {
        PDomibusConnectorUser dbUser = userDao.findOneByUsernameIgnoreCase(user.getUsername());

        createNewInitialPasswordAndInvalidateOthers(dbUser, newInitialPassword);

        return mapDbUserToWebUser(dbUser);
    }

    @Override
    public WebUser createNewUser(WebUser newUser)
        throws NoSuchAlgorithmException, InvalidKeySpecException {
        var dbUser = new PDomibusConnectorUser();

        dbUser.setCreated(new Date());
        dbUser.setRole(eu.ecodex.connector.persistence.model.enums.UserRole.valueOf(
            newUser.getRole().name()));
        dbUser.setUsername(newUser.getUsername());

        dbUser = userDao.save(dbUser);

        PDomibusConnectorUserPassword newPwd =
            createNewInitialPasswordAndInvalidateOthers(dbUser, newUser.getPassword());

        dbUser.getPasswords().add(newPwd);

        dbUser = userDao.save(dbUser);

        return mapDbUserToWebUser(dbUser);
    }

    @Override
    public WebUser updateUser(WebUser user) {
        PDomibusConnectorUser dbUser = userDao.findOneByUsernameIgnoreCase(user.getUsername());

        var changed = false;

        if (dbUser.getRole() != null && user.getRole() != null && !dbUser.getRole().name().equals(
            user.getRole().name())) {
            dbUser.setRole(eu.ecodex.connector.persistence.model.enums.UserRole.valueOf(
                user.getRole().name()));
            changed = true;
        }

        if (dbUser.isLocked() != user.isLocked()) {
            dbUser.setLocked(user.isLocked());
            changed = true;
        }

        if (changed) {
            dbUser = userDao.save(dbUser);
        }

        return mapDbUserToWebUser(dbUser);
    }

    private PDomibusConnectorUserPassword createNewInitialPasswordAndInvalidateOthers(
        PDomibusConnectorUser dbUser, String newInitialPassword)
        throws NoSuchAlgorithmException, InvalidKeySpecException {
        if (!CollectionUtils.isEmpty(dbUser.getPasswords())) {
            for (PDomibusConnectorUserPassword pwd : dbUser.getPasswords()) {
                if (pwd.isCurrentPassword()) {
                    pwd.setCurrentPassword(false);
                    passwordDao.save(pwd);
                }
            }
        }

        var userPassword = new PDomibusConnectorUserPassword();

        userPassword.setCreated(new Date());
        userPassword.setUser(dbUser);
        userPassword.setCurrentPassword(true);
        userPassword.setInitialPassword(true);

        String salt = getHexSalt();
        String passwordDB = generatePasswordHashWithSaltOnlyPW(newInitialPassword, salt);

        userPassword.setSalt(salt);
        userPassword.setPassword(passwordDB);

        return passwordDao.save(userPassword);
    }

    private List<WebUser> mapUsersToDto(Iterable<PDomibusConnectorUser> allUsers) {
        List<WebUser> users = new LinkedList<>();
        for (PDomibusConnectorUser connectorUser : allUsers) {
            var webUser = mapDbUserToWebUser(connectorUser);

            users.add(webUser);
        }

        return users;
    }

    private WebUser mapDbUserToWebUser(PDomibusConnectorUser connectorUser) {
        var webUser = new WebUser();

        webUser.setUsername(connectorUser.getUsername());
        webUser.setRole(UserRole.valueOf(connectorUser.getRole().name()));
        webUser.setLocked(connectorUser.isLocked());
        webUser.setCreated(connectorUser.getCreated());

        return webUser;
    }

    private PDomibusConnectorUser mapWebUserToDbUser(WebUser webUser) {
        var connectorUser = new PDomibusConnectorUser();
        connectorUser.setUsername(webUser.getUsername());

        return connectorUser;
    }

    private static String generatePasswordHashWithSaltOnlyPW(String password, String saltParam)
        throws NoSuchAlgorithmException, InvalidKeySpecException {
        var iterations = 1000;
        var chars = password.toCharArray();
        var salt = DatatypeConverter.parseHexBinary(saltParam);
        var spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);
        var skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        var hash = skf.generateSecret(spec).getEncoded();
        return toHex(hash);
    }

    private static String getHexSalt() throws NoSuchAlgorithmException {
        var secureRandom = SecureRandom.getInstance("SHA1PRNG");
        var salt = new byte[16];
        secureRandom.nextBytes(salt);
        return toHex(salt);
    }

    private static String toHex(byte[] array) {
        var bi = new BigInteger(1, array);
        var hex = bi.toString(16);
        int paddingLength = (array.length * 2) - hex.length();
        if (paddingLength > 0) {
            return String.format("%0" + paddingLength + "d", 0) + hex;
        } else {
            return hex;
        }
    }
}
