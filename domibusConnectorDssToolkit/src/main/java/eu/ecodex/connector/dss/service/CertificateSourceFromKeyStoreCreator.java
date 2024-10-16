/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.dss.service;

import eu.ecodex.connector.common.service.DCKeyStoreService;
import eu.ecodex.connector.lib.spring.configuration.KeyAndKeyStoreConfigurationProperties;
import eu.ecodex.connector.lib.spring.configuration.StoreConfigurationProperties;
import eu.ecodex.connector.tools.logging.LoggingUtils;
import eu.europa.esig.dss.model.DSSException;
import eu.europa.esig.dss.spi.x509.CertificateSource;
import eu.europa.esig.dss.spi.x509.KeyStoreCertificateSource;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.token.KeyStoreSignatureTokenConnection;
import eu.europa.esig.dss.token.SignatureTokenConnection;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.Objects;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

/**
 * This class is responsible for creating a certificate source and signature connection from a
 * keystore.
 */
@Service
public class CertificateSourceFromKeyStoreCreator {
    private static final Logger LOGGER =
        LogManager.getLogger(CertificateSourceFromKeyStoreCreator.class);
    private final DCKeyStoreService dcKeyStoreService;

    public CertificateSourceFromKeyStoreCreator(DCKeyStoreService dcKeyStoreService) {
        this.dcKeyStoreService = dcKeyStoreService;
    }

    /**
     * Creates a CertificateSource from a store based on the given StoreConfigurationProperties.
     *
     * @param storeConfigurationProperties The store configuration properties (path, password,
     *                                     type).
     * @return The CertificateSource created from the store.
     * @throws RuntimeException If failed to load the keystore.
     */
    public CertificateSource createCertificateSourceFromStore(
        StoreConfigurationProperties storeConfigurationProperties) {
        Objects.requireNonNull(
            storeConfigurationProperties, "store configuration is not allowed to be null!");
        LOGGER.debug(
            "Using truststore location [{}], password [{}], type [{}]",
            storeConfigurationProperties.getPath(),
            LoggingUtils.logPassword(LOGGER, storeConfigurationProperties.getPassword()),
            storeConfigurationProperties.getType()
        );
        KeyStoreCertificateSource keyStoreCertificateSource;
        InputStream res;
        try {
            res = dcKeyStoreService.loadKeyStoreAsResource(storeConfigurationProperties)
                                   .getInputStream();
        } catch (IOException ioException) {
            var error = String.format(
                "Failed to load keystore: location [%s], type [%s], password [%s] ",
                storeConfigurationProperties.getPath(),
                storeConfigurationProperties.getType(),
                LoggingUtils.logPassword(LOGGER, storeConfigurationProperties.getPassword())
            );
            throw new RuntimeException(error, ioException);
        }
        try {
            keyStoreCertificateSource =
                new KeyStoreCertificateSource(res, storeConfigurationProperties.getType(),
                                              storeConfigurationProperties.getPassword()
                );
        } catch (DSSException dssException) {
            var error = String.format(
                "Failed to load keystore: location [%s], type [%s], password [%s] ",
                storeConfigurationProperties.getPath(),
                storeConfigurationProperties.getType(),
                LoggingUtils.logPassword(LOGGER, storeConfigurationProperties.getPassword())
            );
            throw new RuntimeException(error, dssException);
        }
        return keyStoreCertificateSource;
    }

    /**
     * Creates a SignatureConnectionAndPrivateKeyEntry from the given
     * KeyAndKeyStoreConfigurationProperties.
     *
     * @param keyAndKeyStoreConfigurationProperties The configuration properties for the key and
     *                                              keystore.
     * @return The created SignatureConnectionAndPrivateKeyEntry.
     * @throws RuntimeException If unable to load the keystore.
     */
    public SignatureConnectionAndPrivateKeyEntry createSignatureConnectionFromStore(
        KeyAndKeyStoreConfigurationProperties keyAndKeyStoreConfigurationProperties) {
        var storeConfigurationProperties = keyAndKeyStoreConfigurationProperties.getKeyStore();
        LOGGER.debug(
            "Using keystore location [{}], password [{}], type [{}]",
            storeConfigurationProperties.getPath(),
            LoggingUtils.logPassword(LOGGER, storeConfigurationProperties.getPassword()),
            storeConfigurationProperties.getType()
        );
        InputStream res;
        try {
            res = dcKeyStoreService.loadKeyStoreAsResource(storeConfigurationProperties)
                                   .getInputStream();
            var keyStoreSignatureTokenConnection = new KeyStoreSignatureTokenConnection(
                res, storeConfigurationProperties.getType(),
                new KeyStore.PasswordProtection(
                    storeConfigurationProperties.getPassword().toCharArray()
                )
            );
            DSSPrivateKeyEntry privateKeyEntry = keyStoreSignatureTokenConnection.getKey(
                keyAndKeyStoreConfigurationProperties.getPrivateKey().getAlias(),
                new KeyStore.PasswordProtection(
                    keyAndKeyStoreConfigurationProperties
                        .getPrivateKey()
                        .getPassword()
                        .toCharArray()
                )
            );

            return new SignatureConnectionAndPrivateKeyEntry(
                keyStoreSignatureTokenConnection, privateKeyEntry);
        } catch (IOException e) {
            throw new RuntimeException("Unable to load trust store", e);
        }
    }

    /**
     * SignatureConnectionAndPrivateKeyEntry represents a connection to a signature token and the
     * corresponding private key entry.
     */
    @Getter
    public static class SignatureConnectionAndPrivateKeyEntry {
        final SignatureTokenConnection signatureTokenConnection;
        final DSSPrivateKeyEntry dssPrivateKeyEntry;

        private SignatureConnectionAndPrivateKeyEntry() {
            throw new RuntimeException("Not Supported");
        }

        public SignatureConnectionAndPrivateKeyEntry(
            SignatureTokenConnection signatureTokenConnection,
            DSSPrivateKeyEntry dssPrivateKeyEntry) {
            this.signatureTokenConnection = signatureTokenConnection;
            this.dssPrivateKeyEntry = dssPrivateKeyEntry;
        }
    }
}
