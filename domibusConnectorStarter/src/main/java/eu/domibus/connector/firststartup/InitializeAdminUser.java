/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.firststartup;

import eu.domibus.connector.persistence.dao.DomibusConnectorUserDao;
import eu.domibus.connector.persistence.dao.DomibusConnectorUserPasswordDao;
import eu.domibus.connector.persistence.model.PDomibusConnectorUser;
import eu.domibus.connector.persistence.model.PDomibusConnectorUserPassword;
import eu.domibus.connector.persistence.model.enums.UserRole;
import eu.domibus.connector.tools.logging.LoggingMarker;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import javax.annotation.PostConstruct;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.transaction.Transactional;
import javax.xml.bind.DatatypeConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * This bean will be run on first startup (for details see FirstStartupCondition).
 *
 * <p>It requires an already initialized database It creates an initial admin user with a random
 * password. Password will be logged to console
 */
@Configuration
@ConditionalOnProperty(
    prefix = InitializeAdminUserProperties.PREFIX, name = "enabled", havingValue = "true",
    matchIfMissing = true
)
@EnableConfigurationProperties(InitializeAdminUserProperties.class)
public class InitializeAdminUser {
    private static final Logger LOGGER = LogManager.getLogger(InitializeAdminUser.class);
    private final InitializeAdminUserProperties initializeAdminUserProperties;
    // instead of the DAOs the service (WebUserPersistenceService) should be used here.
    // But this requires some refactoring, because the service is deeply integrated in
    // the webLib Module
    private final DomibusConnectorUserDao userDao;
    private final DomibusConnectorUserPasswordDao userPasswordDao;

    /**
     * Initializes the admin user.
     *
     * @param initializeAdminUserProperties The properties required to initialize the admin user.
     * @param userDao                       The DAO object for interacting with the database for the
     *                                      DomibusConnectorUser entity.
     * @param userPasswordDao               The DAO object for interacting with the database for the
     *                                      DomibusConnectorUserPassword entity.
     */
    public InitializeAdminUser(
        InitializeAdminUserProperties initializeAdminUserProperties,
        DomibusConnectorUserDao userDao,
        DomibusConnectorUserPasswordDao userPasswordDao) {
        this.initializeAdminUserProperties = initializeAdminUserProperties;
        this.userDao = userDao;
        this.userPasswordDao = userPasswordDao;
    }

    /**
     * Check if the admin user exists in DB if not - create it.
     */
    @PostConstruct
    @Transactional
    public void checkAdminUser() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String adminUserName = initializeAdminUserProperties.getInitialUserName();

        PDomibusConnectorUser adminUser = userDao.findOneByUsernameIgnoreCase(adminUserName);
        if (adminUser != null) {
            LOGGER.info(
                LoggingMarker.Log4jMarker.CONFIG, "Admin user [{}] already exists in DB",
                adminUser.getUsername()
            );
            return;
        }
        String newUserPassword = initializeAdminUserProperties.getInitialUserPassword();

        var newAdminUser = new PDomibusConnectorUser();
        newAdminUser.setUsername(adminUserName);
        newAdminUser.setLocked(false);
        newAdminUser.setRole(UserRole.ADMIN);

        String salt = getHexSalt();

        String dbPassword = generatePasswordHashWithSaltOnlyPW(newUserPassword, salt);

        var userPassword = new PDomibusConnectorUserPassword();
        userPassword.setInitialPassword(initializeAdminUserProperties.isInitialChangeRequired());
        userPassword.setCurrentPassword(true);
        userPassword.setPassword(dbPassword);
        userPassword.setSalt(salt);
        userPassword.setUser(newAdminUser);

        newAdminUser.getPasswords().add(userPassword);

        if (initializeAdminUserProperties.isLogInitialToConsole()) {
            LOGGER.info(
                LoggingMarker.Log4jMarker.CONFIG,
                """
                    ###############################
                    Successfully created initial admin user [{}] with pw [{}]"
                    ###############################
                    """,
                adminUserName,
                newUserPassword
            );
        }

        userDao.save(newAdminUser);
        userPasswordDao.save(userPassword);
    }

    // should be done by user service!
    private static String generatePasswordHashWithSaltOnlyPW(String password, String saltParam)
        throws NoSuchAlgorithmException, InvalidKeySpecException {
        var iterations = 1000;
        var chars = password.toCharArray();
        var salt = DatatypeConverter.parseHexBinary(saltParam);
        var spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);
        var secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        var hash = secretKeyFactory.generateSecret(spec).getEncoded();
        return toHex(hash);
    }

    // should be done by user service!
    private static String getHexSalt() throws NoSuchAlgorithmException {
        var secureRandom = SecureRandom.getInstance("SHA1PRNG");
        var salt = new byte[16];
        secureRandom.nextBytes(salt);
        return toHex(salt);
    }

    // should be done by user service!
    private static String toHex(byte[] array) {
        var bigInteger = new BigInteger(1, array);
        var hex = bigInteger.toString(16);
        var paddingLength = (array.length * 2) - hex.length();
        if (paddingLength > 0) {
            return String.format("%0" + paddingLength + "d%s", 0) + hex;
        } else {
            return hex;
        }
    }
}
