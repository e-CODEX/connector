/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.lib.spring.configuration.validation;

import eu.domibus.connector.common.service.DCKeyStoreService;
import eu.domibus.connector.lib.spring.configuration.KeyConfigurationProperties;
import eu.domibus.connector.lib.spring.configuration.StoreConfigurationProperties;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import jakarta.validation.ConstraintValidatorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * HelperMethods is a utility class that provides methods for validating and checking keys in a
 * keystore.
 */
@Component
public class HelperMethods {
    private static final Logger LOGGER = LoggerFactory.getLogger(HelperMethods.class);
    private final DCKeyStoreService dcKeyStoreService;

    public HelperMethods(DCKeyStoreService dcKeyStoreService) {
        this.dcKeyStoreService = dcKeyStoreService;
    }

    /**
     * Checks if the provided key is loadable from the given store configuration properties.
     *
     * @param context     The constraint validator context.
     * @param storeConfig The store configuration properties.
     * @param keyConfig   The key configuration properties.
     * @return true if the key is loadable, false otherwise.
     */
    @SuppressWarnings("squid:S1135")
    public boolean checkKeyIsLoadable(
        ConstraintValidatorContext context, StoreConfigurationProperties storeConfig,
        KeyConfigurationProperties keyConfig) {
        if (keyConfig == null || keyConfig.getAlias() == null
            || storeConfig
            == null) { // DO NOT CHECK IF keyConfig or keyAlias or storeConfig is null
            LOGGER.trace(
                "checkKeyIsLoadable skipped because either keyConfig, keyConfig.alias or "
                    + "storeConfig is null!");
            return true;
        }
        LOGGER.trace(
            "checkKeyIsLoadable with context [{}], storeConfig [{}], keyConfig [{}]", context,
            storeConfig, keyConfig
        );
        var alias = keyConfig.getAlias();
        var password = keyConfig.getPassword();
        var logPassword = "** enableDebugToSee **";
        if (LOGGER.isDebugEnabled()) {
            logPassword = password;
        }

        char[] passwordArray = password.toCharArray();

        KeyStore keyStore;
        try {
            keyStore = dcKeyStoreService.loadKeyStore(storeConfig);
        } catch (Exception e) {
            context.buildConstraintViolationWithTemplate(e.getMessage())
                   .addConstraintViolation(); // TODO: add PropertyNode
            return false;
        }

        try {
            if (!keyStore.containsAlias(alias)) {
                var error = String.format("key alias [%s] does not exist in key store!", alias);

                context.buildConstraintViolationWithTemplate(error)
                       .addPropertyNode("privateKey")
                       .addPropertyNode("alias")
                       .addConstraintViolation();
                return false;
            }

            var key = keyStore.getKey(alias, passwordArray);
            if (key != null) {
                return true;
            } else {
                var error =
                    String.format("Cannot retrieve key with alias [%s] and password [%s]! ", alias,
                                  logPassword
                    );
                context.buildConstraintViolationWithTemplate(error).addConstraintViolation();
            }
        } catch (KeyStoreException e) {

            var error = String.format(
                "key with alias [%s] and pw [%s] could not recovered! KeyStoreException!", alias,
                logPassword
            );
            LOGGER.warn(error, e);
            context.buildConstraintViolationWithTemplate(error).addConstraintViolation();
        } catch (NoSuchAlgorithmException e) {
            var error = String.format(
                "key with alias [%s] and pw [%s] could not recovered! No such algorithm exception",
                alias, logPassword
            );
            LOGGER.warn(error, e);
            context.buildConstraintViolationWithTemplate(error).addConstraintViolation();
        } catch (UnrecoverableKeyException e) {
            var error = String.format(
                "key with alias [%s] could not recovered! Check if the password [%s] is correct",
                alias, logPassword
            );
            context.buildConstraintViolationWithTemplate(error).addConstraintViolation();
        }
        return false;
    }
}
