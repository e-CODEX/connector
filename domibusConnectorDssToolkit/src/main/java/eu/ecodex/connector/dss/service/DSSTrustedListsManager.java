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

import eu.ecodex.connector.common.service.DCKeyStoreService;
import eu.ecodex.connector.dss.configuration.BasicDssConfigurationProperties;
import eu.ecodex.connector.dss.configuration.TrustListSourceConfigurationProperties;
import eu.ecodex.connector.lib.spring.configuration.StoreConfigurationProperties;
import eu.ecodex.connector.tools.logging.LoggingMarker;
import eu.europa.esig.dss.service.http.commons.FileCacheDataLoader;
import eu.europa.esig.dss.spi.client.http.DSSFileLoader;
import eu.europa.esig.dss.spi.client.http.DataLoader;
import eu.europa.esig.dss.spi.client.http.IgnoreDataLoader;
import eu.europa.esig.dss.spi.tsl.TrustedListsCertificateSource;
import eu.europa.esig.dss.spi.x509.CommonTrustedCertificateSource;
import eu.europa.esig.dss.spi.x509.KeyStoreCertificateSource;
import eu.europa.esig.dss.tsl.function.OfficialJournalSchemeInformationURI;
import eu.europa.esig.dss.tsl.job.TLValidationJob;
import eu.europa.esig.dss.tsl.source.LOTLSource;
import eu.europa.esig.dss.tsl.source.TLSource;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Runs the DSS TSL (Trusted Lists, Trusted Lists of Lists) validation on Startup.
 *
 * <p>Registers also a timer job for that purpose
 */
@SuppressWarnings("squid:S1135")
@Component
public class DSSTrustedListsManager {
    private static final Logger LOGGER = LogManager.getLogger(DSSTrustedListsManager.class);
    private final BasicDssConfigurationProperties basicDssConfigurationProperties;
    private final DCKeyStoreService dcKeyStoreService;
    private final DataLoader dataLoader;
    private final Map<String, TrustedListsCertificateSource> trustedListsCertificateSourceMap =
        new HashMap<>();

    /**
     * Initializes an instance of DSSTrustedListsManager with the given parameters.
     *
     * @param basicDssConfigurationProperties The basic DSS configuration properties.
     * @param dcKeyStoreService               The DCKeyStoreService for loading and storing
     *                                        keystores.
     * @param dataLoader                      The DataLoader for loading data.
     */
    public DSSTrustedListsManager(
        BasicDssConfigurationProperties basicDssConfigurationProperties,
        DCKeyStoreService dcKeyStoreService,
        @Qualifier(DEFAULT_DATALOADER_BEAN_NAME) DataLoader dataLoader) {
        this.basicDssConfigurationProperties = basicDssConfigurationProperties;
        this.dcKeyStoreService = dcKeyStoreService;
        this.dataLoader = dataLoader;
    }

    /**
     * Initializes the DSSTrustedListsManager by iterating over the trust list sources and calling
     * the initTrustSource method for each source. This method is annotated with @PostConstruct to
     * ensure that it is automatically called after the DSSTrustedListsManager bean is constructed.
     */
    @PostConstruct
    public void init() {
        Map<String, TrustListSourceConfigurationProperties> trustSource =
            basicDssConfigurationProperties.getTrustListSources();
        trustSource.forEach(this::initTrustSource);
    }

    private void initTrustSource(
        String s, TrustListSourceConfigurationProperties trustListSourceConfigurationProperties) {
        var trustedListsCertificateSource = new TrustedListsCertificateSource();
        var tlValidationJob = new TLValidationJob();

        tlValidationJob.setTrustedListSources(
            trustListSourceConfigurationProperties
                .getTlSources()
                .stream()
                .map(this::mapTLConfig)
                .toArray(TLSource[]::new)
        );

        tlValidationJob.setListOfTrustedListSources(
            trustListSourceConfigurationProperties
                .getLotlSources()
                .stream()
                .map(this::mapLotlConfig)
                .toArray(LOTLSource[]::new)
        );

        tlValidationJob.setDebug(LOGGER.getLevel().isMoreSpecificThan(Level.INFO));
        tlValidationJob.setOfflineDataLoader(offlineDataLoader());
        tlValidationJob.setOnlineDataLoader(onlineDataLoader());
        tlValidationJob.setTrustedListCertificateSource(trustedListsCertificateSource);

        tlValidationJob.offlineRefresh();

        tlValidationJob.onlineRefresh();

        LOGGER.info(
            LoggingMarker.Log4jMarker.CONFIG,
            "Configured TrustedListsCertificateSource with name [{}]", s
        );
        trustedListsCertificateSourceMap.put(s, trustedListsCertificateSource);
        // TODO: register quartz job!
        // TODO: update on config changes...
    }

    public Optional<TrustedListsCertificateSource> getCertificateSource(String name) {
        return Optional.ofNullable(this.trustedListsCertificateSourceMap.get(name));
    }

    private DSSFileLoader offlineDataLoader() {
        var offlineFileLoader = new FileCacheDataLoader();
        offlineFileLoader.setCacheExpirationTime(
            basicDssConfigurationProperties.getTlCacheExpiration().toMillis());
        offlineFileLoader.setDataLoader(new IgnoreDataLoader()); // do not download from Internet
        offlineFileLoader.setFileCacheDirectory(
            basicDssConfigurationProperties.getTlCacheLocation().toFile());
        return offlineFileLoader;
    }

    private DSSFileLoader onlineDataLoader() {
        var onlineFileLoader = new FileCacheDataLoader();
        onlineFileLoader.setCacheExpirationTime(0);
        onlineFileLoader.setDataLoader(dataLoader);
        onlineFileLoader.setFileCacheDirectory(
            basicDssConfigurationProperties.getTlCacheLocation().toFile());
        return onlineFileLoader;
    }

    private TLSource mapTLConfig(TrustListSourceConfigurationProperties.TlSourceConfig tlConfig) {
        try {
            var tlSource = new TLSource();
            tlSource.setUrl(tlConfig.getTlUrl());

            if (tlConfig.getSigningCerts() != null) {
                var trustedCertSource =
                    getCommonTrustedCertificateSource(tlConfig.getSigningCerts());
                tlSource.setCertificateSource(trustedCertSource);
            }

            LOGGER.info(LoggingMarker.Log4jMarker.CONFIG, "Configured TL source [{}]", tlConfig);
            return tlSource;
        } catch (IOException ioe) {
            var error =
                String.format("Unable to open TrustStore from [%s]", tlConfig.getSigningCerts());
            throw new RuntimeException(error, ioe);
        }
    }

    private LOTLSource mapLotlConfig(
        TrustListSourceConfigurationProperties.LotlSourceConfig lotlConfig) {
        try {
            var lotlSource = new LOTLSource();
            lotlSource.setPivotSupport(lotlConfig.isPivotSupport());
            lotlSource.setUrl(lotlConfig.getLotlUrl());

            if (StringUtils.hasText(lotlConfig.getSigningCertificatesAnnouncementUri())) {
                var officialJournalSchemeInformationURI =
                    new OfficialJournalSchemeInformationURI(
                        lotlConfig.getSigningCertificatesAnnouncementUri());
                lotlSource.setSigningCertificatesAnnouncementPredicate(
                    officialJournalSchemeInformationURI);
            }

            if (lotlConfig.getSigningCerts() != null) {
                var trustedCertSource =
                    getCommonTrustedCertificateSource(lotlConfig.getSigningCerts());
                lotlSource.setCertificateSource(trustedCertSource);
            }

            LOGGER.info(
                LoggingMarker.Log4jMarker.CONFIG, "Configured LOTL source [{}]", lotlConfig);

            return lotlSource;
        } catch (IOException ioe) {
            var error =
                String.format("Unable to open TrustStore from [%s]", lotlConfig.getSigningCerts());
            throw new RuntimeException(error, ioe);
        }
    }

    private CommonTrustedCertificateSource getCommonTrustedCertificateSource(
        StoreConfigurationProperties signingCerts) throws IOException {
        var resource = dcKeyStoreService.loadKeyStoreAsResource(signingCerts);
        var inputStream = resource.getInputStream();
        var keyStoreCertificateSource = new KeyStoreCertificateSource(
            inputStream, signingCerts.getType(),
            signingCerts.getPassword()
        );

        var trustedCertSource = new CommonTrustedCertificateSource();
        trustedCertSource.importAsTrusted(keyStoreCertificateSource);
        return trustedCertSource;
    }

    public Collection<String> getAllSourceNames() {
        return this.trustedListsCertificateSourceMap.keySet();
    }
}
