package eu.domibus.connector.dss.service;

import eu.domibus.connector.common.service.DCKeyStoreService;
import eu.domibus.connector.dss.configuration.BasicDssConfigurationProperties;
import eu.domibus.connector.dss.configuration.TrustListSourceConfigurationProperties;
import eu.domibus.connector.lib.spring.configuration.StoreConfigurationProperties;
import eu.domibus.connector.tools.logging.LoggingMarker;
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
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static eu.domibus.connector.dss.configuration.BasicDssConfiguration.DEFAULT_DATALOADER_BEAN_NAME;

/**
 * Runs the DSS TSL (Trusted Lists, Trusted Lists of Lists)
 * validation on Startup
 *
 * Registers also a timer job for that purpose
 *
 */
@Component
public class DSSTrustedListsManager {

    private static final Logger LOGGER = LogManager.getLogger(DSSTrustedListsManager.class);

    private final BasicDssConfigurationProperties basicDssConfigurationProperties;
    private final DCKeyStoreService dcKeyStoreService;
    private final DataLoader dataLoader;

    private Map<String, TrustedListsCertificateSource> trustedListsCertificateSourceMap = new HashMap<>();

    public DSSTrustedListsManager(BasicDssConfigurationProperties basicDssConfigurationProperties,
                                  DCKeyStoreService dcKeyStoreService,
                                  @Qualifier(DEFAULT_DATALOADER_BEAN_NAME) DataLoader dataLoader) {
        this.basicDssConfigurationProperties = basicDssConfigurationProperties;
        this.dcKeyStoreService = dcKeyStoreService;
        this.dataLoader = dataLoader;
    }

    @PostConstruct
    public void init() {

        Map<String, TrustListSourceConfigurationProperties> trustSource = basicDssConfigurationProperties.getTrustListSources();
        trustSource.forEach(this::initTrustSource);



    }

    private void initTrustSource(String s, TrustListSourceConfigurationProperties trustListSourceConfigurationProperties) {

        TrustedListsCertificateSource trustedListsCertificateSource = new TrustedListsCertificateSource();

        TLValidationJob tlValidationJob = new TLValidationJob();

        tlValidationJob.setTrustedListSources(trustListSourceConfigurationProperties.getTlSources()
                .stream()
                .map(this::mapTLConfig).toArray(TLSource[]::new));

        tlValidationJob.setListOfTrustedListSources(trustListSourceConfigurationProperties.getLotlSources()
            .stream()
            .map(this::mapLotlConfig).toArray(LOTLSource[]::new));

        tlValidationJob.setDebug(LOGGER.getLevel().isMoreSpecificThan(Level.INFO));
        tlValidationJob.setOfflineDataLoader(offlineDataLoader());
        tlValidationJob.setOnlineDataLoader(onlineDataLoader());
        tlValidationJob.setTrustedListCertificateSource(trustedListsCertificateSource);

        tlValidationJob.offlineRefresh();

        tlValidationJob.onlineRefresh();

        LOGGER.info(LoggingMarker.Log4jMarker.CONFIG, "Configured TrustedListsCertificateSource with name [{}]", s);
        trustedListsCertificateSourceMap.put(s, trustedListsCertificateSource);

        //TODO: register quartz job!
        //TODO: update on config changes...
    }

    public Optional<TrustedListsCertificateSource> getCertificateSource(String name) {
        return Optional.ofNullable(this.trustedListsCertificateSourceMap.get(name));
    }

    private DSSFileLoader offlineDataLoader() {
        FileCacheDataLoader offlineFileLoader = new FileCacheDataLoader();
        offlineFileLoader.setCacheExpirationTime(basicDssConfigurationProperties.getTlCacheExpiration().toMillis());
        offlineFileLoader.setDataLoader(new IgnoreDataLoader()); // do not download from Internet
        offlineFileLoader.setFileCacheDirectory(basicDssConfigurationProperties.getTlCacheLocation().toFile());
        return offlineFileLoader;
    }

    private DSSFileLoader onlineDataLoader() {
        FileCacheDataLoader onlineFileLoader = new FileCacheDataLoader();
        onlineFileLoader.setCacheExpirationTime(0);
        onlineFileLoader.setDataLoader(dataLoader);
        onlineFileLoader.setFileCacheDirectory(basicDssConfigurationProperties.getTlCacheLocation().toFile());
        return onlineFileLoader;
    }

    private TLSource mapTLConfig(TrustListSourceConfigurationProperties.TlSourceConfig tlConfig) {
        try {
            TLSource tlSource = new TLSource();
            tlSource.setUrl(tlConfig.getTlUrl());

            if (tlConfig.getSigningCerts() != null) {
                CommonTrustedCertificateSource trustedCertSource = getCommonTrustedCertificateSource(tlConfig.getSigningCerts());
                tlSource.setCertificateSource(trustedCertSource);
            }

            LOGGER.info(LoggingMarker.Log4jMarker.CONFIG, "Configured TL source [{}]", tlConfig);
            return tlSource;

        } catch (IOException ioe) {
            String error = String.format("Unable to open TrustStore from [%s]", tlConfig.getSigningCerts());
            throw new RuntimeException(error, ioe);
        }
    }


    private LOTLSource mapLotlConfig(TrustListSourceConfigurationProperties.LotlSourceConfig lotlConfig) {
        try {
            LOTLSource lotlSource = new LOTLSource();
            lotlSource.setPivotSupport(lotlConfig.isPivotSupport());
            lotlSource.setUrl(lotlConfig.getLotlUrl());

            if (StringUtils.hasText(lotlConfig.getSigningCertificatesAnnouncementUri())) {
                OfficialJournalSchemeInformationURI officialJournalSchemeInformationURI = new OfficialJournalSchemeInformationURI(lotlConfig.getSigningCertificatesAnnouncementUri());
                lotlSource.setSigningCertificatesAnnouncementPredicate(officialJournalSchemeInformationURI);
            }

            if (lotlConfig.getSigningCerts() != null) {
                CommonTrustedCertificateSource trustedCertSource = getCommonTrustedCertificateSource(lotlConfig.getSigningCerts());
                lotlSource.setCertificateSource(trustedCertSource);
            }

            LOGGER.info(LoggingMarker.Log4jMarker.CONFIG, "Configured LOTL source [{}]", lotlConfig);

            return lotlSource;
        } catch (IOException ioe) {
            String error = String.format("Unable to open TrustStore from [%s]", lotlConfig.getSigningCerts());
            throw new RuntimeException(error, ioe);
        }
    }

    private CommonTrustedCertificateSource getCommonTrustedCertificateSource(StoreConfigurationProperties signingCerts) throws IOException {
        Resource resource = dcKeyStoreService.loadKeyStoreAsResource(signingCerts);
        InputStream inputStream = resource.getInputStream();
        KeyStoreCertificateSource keyStoreCertificateSource = new KeyStoreCertificateSource(inputStream, signingCerts.getType(), signingCerts.getPassword());

        CommonTrustedCertificateSource trustedCertSource = new CommonTrustedCertificateSource();
        trustedCertSource.importAsTrusted(keyStoreCertificateSource);
        return trustedCertSource;
    }

    public Collection<String> getAllSourceNames() {
        return this.trustedListsCertificateSourceMap.keySet();
    }


}
