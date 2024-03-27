package eu.domibus.connector.dss.service;

import eu.domibus.connector.common.service.DCKeyStoreService;
import eu.domibus.connector.lib.spring.configuration.KeyAndKeyStoreConfigurationProperties;
import eu.domibus.connector.lib.spring.configuration.StoreConfigurationProperties;
import eu.domibus.connector.tools.logging.LoggingUtils;
import eu.europa.esig.dss.model.DSSException;
import eu.europa.esig.dss.spi.x509.CertificateSource;
import eu.europa.esig.dss.spi.x509.KeyStoreCertificateSource;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.token.KeyStoreSignatureTokenConnection;
import eu.europa.esig.dss.token.SignatureTokenConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.Objects;


@Service
public class CertificateSourceFromKeyStoreCreator {
    private static final Logger LOGGER = LogManager.getLogger(CertificateSourceFromKeyStoreCreator.class);

    private final DCKeyStoreService dcKeyStoreService;

    public CertificateSourceFromKeyStoreCreator(DCKeyStoreService dcKeyStoreService) {
        this.dcKeyStoreService = dcKeyStoreService;
    }

    public CertificateSource createCertificateSourceFromStore(StoreConfigurationProperties storeConfigurationProperties) {
        Objects.requireNonNull(storeConfigurationProperties, "store configuration is not allowed to be null!");
        LOGGER.debug(
                "Using truststore location [{}], password [{}], type [{}]", storeConfigurationProperties.getPath(),
                LoggingUtils.logPassword(LOGGER, storeConfigurationProperties.getPassword()),
                storeConfigurationProperties.getType()
        );
        KeyStoreCertificateSource keyStoreCertificateSource = null;
        InputStream res = null;
        try {
            res = dcKeyStoreService.loadKeyStoreAsResource(storeConfigurationProperties).getInputStream();
        } catch (IOException ioException) {
            String error = String.format(
                    "Failed to load keystore: location [%s], type [%s], password [%s] ",
                    storeConfigurationProperties.getPath(),
                    storeConfigurationProperties.getType(),
                    LoggingUtils.logPassword(LOGGER, storeConfigurationProperties.getPassword())
            );
            throw new RuntimeException(error, ioException);
        }
        try {
            keyStoreCertificateSource = new KeyStoreCertificateSource(
                    res,
                    storeConfigurationProperties.getType(),
                    storeConfigurationProperties.getPassword()
            );
        } catch (DSSException dssException) {
            String error = String.format(
                    "Failed to load keystore: location [%s], type [%s], password [%s] ",
                    storeConfigurationProperties.getPath(),
                    storeConfigurationProperties.getType(),
                    LoggingUtils.logPassword(LOGGER, storeConfigurationProperties.getPassword())
            );
            throw new RuntimeException(error, dssException);
        }
        return keyStoreCertificateSource;
    }

    public SignatureConnectionAndPrivateKeyEntry createSignatureConnectionFromStore(
            KeyAndKeyStoreConfigurationProperties keyAndKeyStoreConfigurationProperties) {
        StoreConfigurationProperties storeConfigurationProperties = keyAndKeyStoreConfigurationProperties.getKeyStore();
        LOGGER.debug(
                "Using keystore location [{}], password [{}], type [{}]", storeConfigurationProperties.getPath(),
                LoggingUtils.logPassword(LOGGER, storeConfigurationProperties.getPassword()),
                storeConfigurationProperties.getType()
        );
        InputStream res = null;
        try {
            res = dcKeyStoreService.loadKeyStoreAsResource(storeConfigurationProperties).getInputStream();
            KeyStoreSignatureTokenConnection keyStoreSignatureTokenConnection =
                    new KeyStoreSignatureTokenConnection(
                            res,
                            storeConfigurationProperties.getType(),
                            new KeyStore.PasswordProtection(
                                    storeConfigurationProperties
                                            .getPassword()
                                            .toCharArray()
                            )
                    );
            DSSPrivateKeyEntry privatKeyEntry = keyStoreSignatureTokenConnection.getKey(
                    keyAndKeyStoreConfigurationProperties.getPrivateKey().getAlias(),
                    new KeyStore.PasswordProtection(
                            keyAndKeyStoreConfigurationProperties
                                    .getPrivateKey()
                                    .getPassword()
                                    .toCharArray()
                    )
            );

            return new SignatureConnectionAndPrivateKeyEntry(keyStoreSignatureTokenConnection, privatKeyEntry);
        } catch (IOException e) {
            throw new RuntimeException("Unable to load trust store", e);
        }
    }

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

        public SignatureTokenConnection getSignatureTokenConnection() {
            return signatureTokenConnection;
        }

        public DSSPrivateKeyEntry getDssPrivateKeyEntry() {
            return dssPrivateKeyEntry;
        }
    }
}
