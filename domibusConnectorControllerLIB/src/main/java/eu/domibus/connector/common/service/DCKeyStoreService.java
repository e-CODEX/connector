/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.common.service;

import eu.domibus.connector.lib.spring.configuration.StoreConfigurationProperties;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

/**
 * Handles loading and storing of keystores centrally.
 *
 * <p>So any extensions for HSM support, ... should be put here
 */
@Service
public class DCKeyStoreService {
    private static final Logger LOGGER = LogManager.getLogger(DCKeyStoreService.class);
    private final ApplicationContext ctx;

    public DCKeyStoreService(ApplicationContext ctx) {
        this.ctx = ctx;
    }

    private InputStream loadKeyStoreAsInputStream(
        StoreConfigurationProperties storeConfigurationProperties) {
        try {
            return loadKeyStoreAsResource(storeConfigurationProperties).getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Loads a KeyStore as a Spring Resource based on the given store configuration properties.
     *
     * @param storeConfigurationProperties The store configuration properties.
     * @return The loaded KeyStore resource.
     */
    public Resource loadKeyStoreAsResource(
        StoreConfigurationProperties storeConfigurationProperties) {
        String path = storeConfigurationProperties.getPath();
        String resolvedPath = ctx.getEnvironment().resolvePlaceholders(path);
        LOGGER.debug("loadKeyStoreAsResource# Resolved path [{}] to [{}]", path, resolvedPath);
        return ctx.getResource(resolvedPath);
    }

    /**
     * Loads a KeyStore based on the given store configuration properties.
     *
     * @param storeConfigurationProperties The store configuration properties.
     * @return The loaded KeyStore.
     * @throws IllegalArgumentException    If storeConfigurationProperties is null.
     * @throws CannotLoadKeyStoreException If there is an error loading the KeyStore.
     */
    public KeyStore loadKeyStore(StoreConfigurationProperties storeConfigurationProperties) {
        if (storeConfigurationProperties == null) {
            throw new IllegalArgumentException(
                "storeConfigurationProperties are not allowed to be null!");
        }
        String password = storeConfigurationProperties.getPassword();
        if (password == null) {
            password = "";
        }
        char[] pwdArray = password.toCharArray();
        try (var inputStream = loadKeyStoreAsInputStream(storeConfigurationProperties)) {
            var keyStore = KeyStore.getInstance(storeConfigurationProperties.getType());
            keyStore.load(inputStream, pwdArray);
            return keyStore;
        } catch (IOException | KeyStoreException | CertificateException
                 | NoSuchAlgorithmException e) {
            throw new CannotLoadKeyStoreException(
                String.format(
                    "Cannot load key store from location %s",
                    storeConfigurationProperties.getPath()
                ), e);
        }
    }

    public void saveKeyStore(
        StoreConfigurationProperties storeConfigurationProperties, KeyStore keyStore) {
        throw new UnsupportedOperationException(); // not yet supported
    }

    /**
     * Validates the existence of a key in the specified key store.
     *
     * @param storeConfigurationProperties The properties of the key store.
     * @param alias                        The alias of the key.
     * @param password                     The password for the key.
     * @throws DCKeyStoreService.ValidationException If the key does not exist or if there is an
     *                                               error retrieving the key.
     */
    public void validateKeyExists(
        StoreConfigurationProperties storeConfigurationProperties, String alias, String password) {
        KeyStore keyStore;
        keyStore = loadKeyStore(storeConfigurationProperties);

        try {
            var key = keyStore.getKey(alias, password.toCharArray());
            if (key == null) {
                throw new DCKeyStoreService.ValidationException("No key found for alias [%s]");
            }
        } catch (KeyStoreException e) {
            throw new DCKeyStoreService.ValidationException(
                    "Key Store exception when retrieving key alias [%s]".formatted(alias), e);
        } catch (NoSuchAlgorithmException e) {
            throw new DCKeyStoreService.ValidationException(
                    "No such key exception when retrieving key alias [%s]".formatted(alias), e);
        } catch (UnrecoverableKeyException e) {
            throw new DCKeyStoreService.ValidationException(
                    "Validation exception when retrieving key alias [%s]".formatted(alias), e);
        }
    }

    /**
     * Validates the existence of a certificate with the given alias in the specified key store.
     *
     * @param storeConfigurationProperties the configuration properties for the key store
     * @param alias                        the alias of the certificate to validate
     * @throws DCKeyStoreService.ValidationException if the certificate with the alias is not found
     *                                               or if there are any issues with the key store
     */
    public void validateCertExists(
        StoreConfigurationProperties storeConfigurationProperties, String alias) {
        KeyStore keyStore;
        keyStore = loadKeyStore(storeConfigurationProperties);
        try {
            var certificate = keyStore.getCertificate(alias);
            if (certificate == null) {
                throw new DCKeyStoreService.ValidationException(
                        "No certificate found for alias [%s]".formatted(alias));
            }
        } catch (KeyStoreException e) {
            throw new DCKeyStoreService.ValidationException((
                    "Key store exception occurred while loading certificate with alias [%s] from "
                            + "key store").formatted(
                    alias
            ), e);
        }
    }

    /**
     * RuntimeException thrown when there is an error loading a KeyStore.
     */
    @NoArgsConstructor
    public static class CannotLoadKeyStoreException extends RuntimeException {
        public CannotLoadKeyStoreException(String message) {
            super(message);
        }

        public CannotLoadKeyStoreException(String message, Throwable cause) {
            super(message, cause);
        }

        public CannotLoadKeyStoreException(Throwable cause) {
            super(cause);
        }

        public CannotLoadKeyStoreException(
            String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }

    /**
     * Exception class for validation errors.
     */
    public static class ValidationException extends RuntimeException {
        public ValidationException() {
        }

        public ValidationException(String message) {
            super(message);
        }

        public ValidationException(String message, Throwable cause) {
            super(message, cause);
        }

        public ValidationException(Throwable cause) {
            super(cause);
        }

        public ValidationException(
            String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }
}
