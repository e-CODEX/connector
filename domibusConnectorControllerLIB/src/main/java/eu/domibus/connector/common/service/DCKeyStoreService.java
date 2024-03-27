package eu.domibus.connector.common.service;

import eu.domibus.connector.lib.spring.configuration.StoreConfigurationProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;


/**
 * Handles loading and storing of keystores
 * centrally
 * <p>
 * so any extensions for HSM support, ... should be put here
 */
@Service
public class DCKeyStoreService {
    private static final Logger LOGGER = LogManager.getLogger(DCKeyStoreService.class);

    private final ApplicationContext ctx;

    public DCKeyStoreService(ApplicationContext ctx) {
        this.ctx = ctx;
    }

    private InputStream loadKeyStoreAsInputStream(StoreConfigurationProperties storeConfigurationProperties) {
        try {
            return loadKeyStoreAsResource(storeConfigurationProperties).getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Resource loadKeyStoreAsResource(StoreConfigurationProperties storeConfigurationProperties) {
        String path = storeConfigurationProperties.getPath();
        String resolvedPath = ctx.getEnvironment().resolvePlaceholders(path);
        LOGGER.debug("loadKeyStoreAsResource# Resolved path [{}] to [{}]", path, resolvedPath);
        return ctx.getResource(resolvedPath);
    }

    public KeyStore loadKeyStore(StoreConfigurationProperties storeConfigurationProperties) {
        if (storeConfigurationProperties == null) {
            throw new IllegalArgumentException("storeConfigurationProperties are not allowed to be null!");
        }
        String password = storeConfigurationProperties.getPassword();
        // validatePathReadable();
        if (password == null) {
            password = "";
        }
        char[] pwdArray = password.toCharArray();
        try (InputStream inputStream = loadKeyStoreAsInputStream(storeConfigurationProperties)) {
            KeyStore keyStore = KeyStore.getInstance(storeConfigurationProperties.getType());
            keyStore.load(inputStream, pwdArray);
            return keyStore;
        } catch (IOException | KeyStoreException | CertificateException | NoSuchAlgorithmException e) {
            throw new CannotLoadKeyStoreException(String.format(
                    "Cannot load key store from location %s",
                    storeConfigurationProperties.getPath()
            ), e);
        }
    }

    public void saveKeyStore(StoreConfigurationProperties storeConfigurationProperties, KeyStore keyStore) {
        throw new UnsupportedOperationException(); // not yet supported
    }

    public void validateKeyExists(
            StoreConfigurationProperties storeConfigurationProperties,
            String alias,
            String password) {
        KeyStore keyStore;
        keyStore = loadKeyStore(storeConfigurationProperties);

        try {
            Key key = keyStore.getKey(alias, password.toCharArray());
            if (key == null) {
                throw new DCKeyStoreService.ValidationException(String.format("No key found for alias [%s]"));
            }
        } catch (KeyStoreException e) {
            throw new DCKeyStoreService.ValidationException(String.format(
                    "Key Store exception when retrieving key alias [%s]",
                    alias
            ), e);
        } catch (NoSuchAlgorithmException e) {
            throw new DCKeyStoreService.ValidationException(String.format(
                    "No such key exception when retrieving key alias [%s]",
                    alias
            ), e);
        } catch (UnrecoverableKeyException e) {
            throw new DCKeyStoreService.ValidationException(String.format(
                    "Validation exception when retrieving key alias [%s]",
                    alias
            ), e);
        }
    }

    public void validateCertExists(StoreConfigurationProperties storeConfigurationProperties, String alias) {
        KeyStore keyStore;
        keyStore = loadKeyStore(storeConfigurationProperties);
        try {
            Certificate certificate = keyStore.getCertificate(alias);
            if (certificate == null) {
                throw new DCKeyStoreService.ValidationException(String.format(
                        "No certificate found for alias [%s]",
                        alias
                ));
            }
        } catch (KeyStoreException e) {
            throw new DCKeyStoreService.ValidationException(String.format(
                    "Key store exception occured while loading certificate with alias [%s] from key store",
                    alias
            ), e);
        }
    }

    public static class CannotLoadKeyStoreException extends RuntimeException {
        public CannotLoadKeyStoreException() {
        }

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
                String message,
                Throwable cause,
                boolean enableSuppression,
                boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }

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
                String message,
                Throwable cause,
                boolean enableSuppression,
                boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }
}
