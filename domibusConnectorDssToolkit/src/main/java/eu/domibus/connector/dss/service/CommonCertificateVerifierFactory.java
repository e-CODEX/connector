package eu.domibus.connector.dss.service;

import eu.domibus.connector.dss.configuration.CertificateVerifierConfigurationProperties;
import eu.europa.esig.dss.service.crl.OnlineCRLSource;
import eu.europa.esig.dss.service.ocsp.OnlineOCSPSource;
import eu.europa.esig.dss.spi.client.http.DataLoader;
import eu.europa.esig.dss.spi.tsl.TrustedListsCertificateSource;
import eu.europa.esig.dss.spi.x509.CertificateSource;
import eu.europa.esig.dss.spi.x509.CommonTrustedCertificateSource;
import eu.europa.esig.dss.spi.x509.ListCertificateSource;
import eu.europa.esig.dss.spi.x509.aia.AIASource;
import eu.europa.esig.dss.spi.x509.aia.DefaultAIASource;
import eu.europa.esig.dss.spi.x509.revocation.crl.CRLSource;
import eu.europa.esig.dss.spi.x509.revocation.ocsp.OCSPSource;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static eu.domibus.connector.dss.configuration.BasicDssConfiguration.DEFAULT_DATALOADER_BEAN_NAME;


@Service
public class CommonCertificateVerifierFactory {
    static Logger LOGGER = LogManager.getLogger(CommonCertificateVerifierFactory.class);

    private final OnlineCRLSource onlineCRLSource;
    private final OnlineOCSPSource onlineOCSPSource;
    private final CertificateSourceFromKeyStoreCreator certificateSourceFromKeyStoreCreator;
    private final DSSTrustedListsManager trustedListsManager;
    private final DataLoader dataLoader;

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

    public CommonCertificateVerifier createCommonCertificateVerifier(
            CertificateVerifierConfigurationProperties certificateVerifierConfig) {
        CommonCertificateVerifier commonCertificateVerifier = new CommonCertificateVerifier(true);
        ListCertificateSource trustedCertificateSourcesList = new ListCertificateSource();

        List<CertificateSource> trustedCertSources = new ArrayList<>();
        CRLSource crlSource = null;
        OCSPSource ocspSource = null;
        DataLoader aiaDataLoader = null;

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

        String trustedListSourceName = certificateVerifierConfig.getTrustedListSource();
        if (StringUtils.hasText(trustedListSourceName)) {
            Optional<TrustedListsCertificateSource> certificateSource = trustedListsManager
                    .getCertificateSource(trustedListSourceName);
            if (certificateSource.isPresent()) {
                TrustedListsCertificateSource tlSource = certificateSource.get();
                trustedCertificateSourcesList.add(tlSource);
            } else {
                LOGGER.warn(
                        "There is no TrustedListsCertificateSource with key [{}] configured. Available are [{}]",
                        trustedListSourceName, trustedListsManager.getAllSourceNames()
                );
            }
        }

        if (certificateVerifierConfig.isTrustStoreEnabled()) {
            if (certificateVerifierConfig.getTrustStore() == null) {
                throw new IllegalArgumentException("Trust store is set to enabled, but it is not configured!");
            }
            CertificateSource certificateSourceFromStore = certificateSourceFromKeyStoreCreator
                    .createCertificateSourceFromStore(certificateVerifierConfig.getTrustStore());
            CommonTrustedCertificateSource trustedCertSource = new CommonTrustedCertificateSource();
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
                    trustedCertificateSourcesList
                            .getSources()
                            .stream()
                            .map(s -> "[" + s.getCertificateSourceType() + " entries " + s
                                    .getCertificates().size() + "]")
                            .collect(Collectors.joining(","))
            );
        }

        if (certificateVerifierConfig.getIgnoreStore() != null) {
            CertificateSource ignoreCertificateSourceFromStore = certificateSourceFromKeyStoreCreator
                    .createCertificateSourceFromStore(certificateVerifierConfig.getIgnoreStore());
            commonCertificateVerifier.setAdjunctCertSources(ignoreCertificateSourceFromStore);
            LOGGER.debug("Setting untrusted certificate source: [{}]", ignoreCertificateSourceFromStore);
        }

        return commonCertificateVerifier;
    }
}
