package eu.domibus.connector.ui.persistence.service.impl;

import eu.domibus.connector.persistence.dao.DomibusConnectorUserDao;
import eu.domibus.connector.persistence.dao.DomibusConnectorUserPasswordDao;
import eu.domibus.connector.persistence.model.PDomibusConnectorUser;
import eu.domibus.connector.persistence.model.PDomibusConnectorUserPassword;
import eu.domibus.connector.ui.dto.WebUser;
import eu.domibus.connector.ui.enums.UserRole;
import eu.domibus.connector.ui.exception.InitialPasswordException;
import eu.domibus.connector.ui.exception.UserLoginException;
import eu.domibus.connector.ui.persistence.service.DomibusConnectorWebUserPersistenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.util.CollectionUtils;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


@org.springframework.stereotype.Service("webUserPersistenceService")
public class DomibusConnectorWebUserPersistenceServiceImpl implements DomibusConnectorWebUserPersistenceService {
    private DomibusConnectorUserDao userDao;
    private DomibusConnectorUserPasswordDao passwordDao;

    private static String generatePasswordHashWithSaltOnlyPW(String password, String saltParam)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        int iterations = 1000;
        char[] chars = password.toCharArray();
        byte[] salt = DatatypeConverter.parseHexBinary(saltParam);
        PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = skf.generateSecret(spec).getEncoded();
        return toHex(hash);
    }

    private static String getHexSalt() throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return toHex(salt);
    }

    private static String toHex(byte[] array) throws NoSuchAlgorithmException {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);
        int paddingLength = (array.length * 2) - hex.length();
        if (paddingLength > 0) {
            return String.format("%0" + paddingLength + "d", 0) + hex;
        } else {
            return hex;
        }
    }

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
    public WebUser login(String username, String password) throws UserLoginException, InitialPasswordException {
        PDomibusConnectorUser user = getAndCheckGivenUser(username, password);
        return mapDbUserToWebUser(user);
    }

    @Override
    public List<WebUser> listAllUsers() {
        Iterable<PDomibusConnectorUser> allUsers = this.userDao.findAll();
        return mapUsersToDto(allUsers);
    }

    @Override
    public WebUser resetUserPassword(
            WebUser user,
            String newInitialPassword) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PDomibusConnectorUser dbUser = userDao.findOneByUsernameIgnoreCase(user.getUsername());

        createNewInitialPasswordAndInvalidateOthers(dbUser, newInitialPassword);

        return mapDbUserToWebUser(dbUser);
    }

    @Override
    public WebUser createNewUser(WebUser newUser) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PDomibusConnectorUser dbUser = new PDomibusConnectorUser();

        dbUser.setCreated(new Date());
        dbUser.setRole(eu.domibus.connector.persistence.model.enums.UserRole.valueOf(newUser.getRole().name()));
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

        boolean changed = false;

        if (dbUser.getRole() != null && user.getRole() != null &&
                !dbUser.getRole().name().equals(user.getRole().name())) {
            dbUser.setRole(eu.domibus.connector.persistence.model.enums.UserRole.valueOf(user.getRole().name()));
            changed = true;
        }

        if (dbUser.isLocked() != user.isLocked()) {
            dbUser.setLocked(user.isLocked());
            changed = true;
        }

        if (changed)
            dbUser = userDao.save(dbUser);

        return mapDbUserToWebUser(dbUser);
    }

    @Override
    public WebUser changePassword(String username, String oldPassword, String newPassword) throws UserLoginException {
        PDomibusConnectorUser user = null;
        try {
            user = getAndCheckGivenUser(username, oldPassword);
        } catch (InitialPasswordException e) {
            // This is expected here!
            user = userDao.findOneByUsernameIgnoreCase(username);
        }

        if (user != null) {
            PDomibusConnectorUserPassword currentPassword = this.passwordDao.findCurrentByUser(user);

            PDomibusConnectorUserPassword newDbPassword = new PDomibusConnectorUserPassword();
            newDbPassword.setUser(user);
            newDbPassword.setCurrentPassword(true);
            newDbPassword.setInitialPassword(false);

            String salt = null;
            String passwordDB = null;
            try {
                salt = getHexSalt();
                passwordDB = generatePasswordHashWithSaltOnlyPW(newPassword, salt);
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                throw new UserLoginException(
                        "The password could not be changed! Please try again later or contact your administrator!");
            }

            newDbPassword.setSalt(salt);
            newDbPassword.setPassword(passwordDB);

            newDbPassword = passwordDao.save(newDbPassword);

            user.getPasswords().add(newDbPassword);

            currentPassword.setCurrentPassword(false);

            passwordDao.save(currentPassword);

            user = userDao.save(user);

            return mapDbUserToWebUser(user);
        }
        return null;
    }

    private PDomibusConnectorUser getAndCheckGivenUser(String username, String password)
            throws UserLoginException, InitialPasswordException {
        PDomibusConnectorUser user = userDao.findOneByUsernameIgnoreCase(username);
        if (user != null) {
            if (user.isLocked())
                throw new UserLoginException("The user is locked! Please contact your administrator!");

            PDomibusConnectorUserPassword currentPassword = null;
            try {
                currentPassword = this.passwordDao.findCurrentByUser(user);
            } catch (IncorrectResultSizeDataAccessException e) {
                throw new UserLoginException(
                        "The user has more than one current passwords! Please contact your administrator!");
            }
            if (currentPassword == null) {
                throw new UserLoginException("The user has no current password! Please contact your administrator!");
            }

            String encrypted = null;
            try {
                encrypted = generatePasswordHashWithSaltOnlyPW(password, currentPassword.getSalt());
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                throw new UserLoginException(
                        "The user could not be logged in! Please try again later or contact your administrator!");
            }

            if (!encrypted.equals(currentPassword.getPassword())) {
                long graceLoginsUsed = user.getGraceLoginsUsed().longValue();
                long numberOfGraceLogins = user.getNumberOfGraceLogins().longValue();

                graceLoginsUsed = graceLoginsUsed + 1;
                user.setGraceLoginsUsed(user.getGraceLoginsUsed().longValue() + 1);
                if (graceLoginsUsed >= numberOfGraceLogins) {
                    user.setLocked(true);
                    userDao.save(user);
                    throw new UserLoginException("The given password is not correct! The user is locked now!");
                } else {
                    userDao.save(user);
                    throw new UserLoginException("The given password is not correct! You have " + (numberOfGraceLogins - graceLoginsUsed) + " grace logins left.");
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

    private PDomibusConnectorUserPassword createNewInitialPasswordAndInvalidateOthers(
            PDomibusConnectorUser dbUser,
            String newInitialPassword) throws NoSuchAlgorithmException, InvalidKeySpecException {
        if (!CollectionUtils.isEmpty(dbUser.getPasswords())) {
            for (PDomibusConnectorUserPassword pwd : dbUser.getPasswords()) {
                if (pwd.isCurrentPassword()) {
                    pwd.setCurrentPassword(false);
                    passwordDao.save(pwd);
                }
            }
        }

        PDomibusConnectorUserPassword newPwd = new PDomibusConnectorUserPassword();

        newPwd.setCreated(new Date());
        newPwd.setUser(dbUser);
        newPwd.setCurrentPassword(true);
        newPwd.setInitialPassword(true);

        String salt = getHexSalt();
        String passwordDB = generatePasswordHashWithSaltOnlyPW(newInitialPassword, salt);

        newPwd.setSalt(salt);
        newPwd.setPassword(passwordDB);

        return passwordDao.save(newPwd);
    }

    private List<WebUser> mapUsersToDto(Iterable<PDomibusConnectorUser> allUsers) {
        List<WebUser> users = new LinkedList<WebUser>();
        Iterator<PDomibusConnectorUser> usrIt = allUsers.iterator();
        while (usrIt.hasNext()) {
            PDomibusConnectorUser pUser = usrIt.next();

            WebUser user = mapDbUserToWebUser(pUser);

            users.add(user);
        }

        return users;
    }

    private WebUser mapDbUserToWebUser(PDomibusConnectorUser pUser) {
        WebUser user = new WebUser();

        user.setUsername(pUser.getUsername());
        user.setRole(UserRole.valueOf(pUser.getRole().name()));
        user.setLocked(pUser.isLocked());
        user.setCreated(pUser.getCreated());

        return user;
    }

    private PDomibusConnectorUser mapWebUserToDbUser(WebUser webUser) {
        PDomibusConnectorUser dbUser = new PDomibusConnectorUser();
        dbUser.setUsername(webUser.getUsername());

        return dbUser;
    }
}
