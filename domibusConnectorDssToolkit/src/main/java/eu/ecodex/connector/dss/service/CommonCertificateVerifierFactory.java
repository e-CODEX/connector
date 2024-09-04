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

import static eu.ecodex.connector.dss.configuration.BasicDssConfiguration.DEFAULT_DATALOADER_BEAN_NAME;

import eu.ecodex.connector.dss.configuration.CertificateVerifierConfigurationProperties;
import eu.europa.esig.dss.service.crl.OnlineCRLSource;
import eu.europa.esig.dss.service.ocsp.OnlineOCSPSource;
import eu.europa.esig.dss.spi.client.http.DataLoader;
import eu.europa.esig.dss.spi.tsl.TrustedListsCertificateSource;
import eu.europa.esig.dss.spi.x509.CommonTrustedCertificateSource;
import eu.europa.esig.dss.spi.x509.ListCertificateSource;
import eu.europa.esig.dss.spi.x509.aia.AIASource;
import eu.europa.esig.dss.spi.x509.aia.DefaultAIASource;
import eu.europa.esig.dss.spi.x509.revocation.crl.CRLSource;
import eu.europa.esig.dss.spi.x509.revocation.ocsp.OCSPSource;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * The CommonCertificateVerifierFactory class is responsible for creating instances of the
 * CommonCertificateVerifier class.
 */
@Service
public class CommonCertificateVerifierFactory {
    static Logger LOGGER = LogManager.getLogger(CommonCertificateVerifierFactory.class);
    private final OnlineCRLSource onlineCRLSource;
    private final OnlineOCSPSource onlineOCSPSource;
    private final CertificateSourceFromKeyStoreCreator certificateSourceFromKeyStoreCreator;
    private final DSSTrustedListsManager trustedListsManager;
    private final DataLoader dataLoader;

    /**
     * CommonCertificateVerifierFactory class is responsible for creating a
     * CommonCertificateVerifier based on the given parameters.
     */
    public CommonCertificateVerifierFactory(
        @Qualifier(DEFAULT_DATALOADER_BEAN_NAME) DataLoader defaultDataLoader,
        OnlineCRLSource onlineCRLSource,
        OnlineOCSPSource onlineOCSPSource,
        CertificateSourceFromKeyStoreCreator certificateSourceFromKeyStoreCreator,
        DSSTrustedListsManager trustedListsManager
    ) {
        this.dataLoader = defaultDataLoader;
        this.onlineCRLSource = onlineCRLSource;
        this.onlineOCSPSource = onlineOCSPSource;
        this.certificateSourceFromKeyStoreCreator = certificateSourceFromKeyStoreCreator;
        this.trustedListsManager = trustedListsManager;
    }

    /**
     * Creates a CommonCertificateVerifier based on the given
     * CertificateVerifierConfigurationProperties.
     *
     * @param certificateVerifierConfig The configuration properties for the Certificate Verifier.
     * @return The created CommonCertificateVerifier.
     * @throws IllegalArgumentException If the trust store is enabled but not configured.
     */
    public CommonCertificateVerifier createCommonCertificateVerifier(
        CertificateVerifierConfigurationProperties certificateVerifierConfig) {
        var commonCertificateVerifier = new CommonCertificateVerifier(true);

        CRLSource crlSource;
        OCSPSource ocspSource;

        if (certificateVerifierConfig.isAiaEnabled()) {
            LOGGER.debug("AIA loading is enabled");
            AIASource aiaSource = new DefaultAIASource(dataLoader);
            commonCertificateVerifier.setAIASource(aiaSource);
        } else {
            LOGGER.debug("AIA loading is NOT enabled");
        }
        if (certificateVerifierConfig.isOcspEnabled()) {
            ocspSource = onlineOCSPSource;
            commonCertificateVerifier.setOcspSource(ocspSource);
            LOGGER.debug("OCSP checking is enabled");
        } else {
            LOGGER.debug("OCSP checking is NOT enabled");
        }
        if (certificateVerifierConfig.isCrlEnabled()) {
            LOGGER.debug("CRL checking is enabled");
            crlSource = onlineCRLSource;
            commonCertificateVerifier.setCrlSource(crlSource);
        } else {
            LOGGER.debug("CRL checking is NOT enabled");
        }

        var trustedCertificateSourcesList = new ListCertificateSource();

        String trustedListSourceName = certificateVerifierConfig.getTrustedListSource();
        if (StringUtils.hasText(trustedListSourceName)) {
            Optional<TrustedListsCertificateSource> certificateSource =
                trustedListsManager.getCertificateSource(trustedListSourceName);
            if (certificateSource.isPresent()) {
                TrustedListsCertificateSource tlSource = certificateSource.get();
                trustedCertificateSourcesList.add(tlSource);
            } else {
                LOGGER.warn(
                    "There is no TrustedListsCertificateSource with key [{}] configured. "
                        + "Available are [{}]",
                    trustedListSourceName, trustedListsManager.getAllSourceNames()
                );
            }
        }

        if (certificateVerifierConfig.isTrustStoreEnabled()) {
            if (certificateVerifierConfig.getTrustStore() == null) {
                throw new IllegalArgumentException(
                    "Trust store is set to enabled, but it is not configured!");
            }
            var certificateSourceFromStore =
                certificateSourceFromKeyStoreCreator.createCertificateSourceFromStore(
                    certificateVerifierConfig.getTrustStore()
                );
            var trustedCertSource = new CommonTrustedCertificateSource();
            trustedCertSource.importAsTrusted(certificateSourceFromStore);
            trustedCertificateSourcesList.add(trustedCertSource);
            LOGGER.debug("Setting source [{}] as trusted", certificateSourceFromStore);
        } else {
            LOGGER.debug("TrustStore is not enabled");
        }

        if (trustedCertificateSourcesList.isEmpty()) {
            LOGGER.warn("No trusted certificate source has been configured");
        } else {
            commonCertificateVerifier.setTrustedCertSources(trustedCertificateSourcesList);
            LOGGER.debug(
                "Setting trusted certificate sources: [{}]",
                trustedCertificateSourcesList.getSources()
                                             .stream()
                                             .map(s -> "[" + s.getCertificateSourceType()
                                                 + " entries " + s.getCertificates().size() + "]")
                                             .collect(Collectors.joining(","))
            );
        }

        if (certificateVerifierConfig.getIgnoreStore() != null) {
            var ignoreCertificateSourceFromStore =
                certificateSourceFromKeyStoreCreator.createCertificateSourceFromStore(
                    certificateVerifierConfig.getIgnoreStore()
                );
            commonCertificateVerifier.setAdjunctCertSources(ignoreCertificateSourceFromStore);
            LOGGER.debug(
                "Setting untrusted certificate source: [{}]", ignoreCertificateSourceFromStore);
        }

        return commonCertificateVerifier;
    }
}
