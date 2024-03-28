package eu.domibus.connector.evidences.spring;

import eu.domibus.connector.common.annotations.BusinessDomainScoped;
import eu.domibus.connector.common.service.DCKeyStoreService;
import eu.domibus.connector.evidences.DomibusConnectorEvidencesToolkit;
import eu.domibus.connector.evidences.HashValueBuilder;
import eu.ecodex.evidences.ECodexEvidenceBuilder;
import eu.ecodex.evidences.EvidenceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;


@Configuration
@ComponentScan(basePackageClasses = {DomibusConnectorEvidencesToolkit.class})
@EnableConfigurationProperties
@BusinessDomainScoped
public class DomibusConnectorEvidencesToolkitContext {
    private static final Logger LOGGER = LoggerFactory.getLogger(DomibusConnectorEvidencesToolkitContext.class);

    private final EvidencesToolkitConfigurationProperties evidencesToolkitConfigurationProperties;
    private final DCKeyStoreService keyStoreService;

    public DomibusConnectorEvidencesToolkitContext(
            EvidencesToolkitConfigurationProperties evidencesToolkitConfigurationProperties,
            DCKeyStoreService keyStoreService) {
        this.evidencesToolkitConfigurationProperties = evidencesToolkitConfigurationProperties;
        this.keyStoreService = keyStoreService;
    }

    @Bean
    @BusinessDomainScoped
    public EvidenceBuilder domibusConnectorEvidenceBuilder() {
        Resource javaKeyStorePath =
                keyStoreService.loadKeyStoreAsResource(evidencesToolkitConfigurationProperties.getSignature()
                                                                                              .getKeyStore());
        String javaKeyStorePassword =
                evidencesToolkitConfigurationProperties.getSignature().getKeyStore().getPassword();
        String javaKeyStoreType = evidencesToolkitConfigurationProperties.getSignature().getKeyStore().getType();
        String keyAlias = evidencesToolkitConfigurationProperties.getSignature().getPrivateKey().getAlias();
        String keyPassword = evidencesToolkitConfigurationProperties.getSignature().getPrivateKey().getPassword();
        LOGGER.debug(
                "Creating ECodexEvidenceBuilder with keyStorePath [{}], keyStoreType [{}], keyStorePassword " +
                        "[{}], " + "keyAlias [{}], keyPassword [password={}]",
                javaKeyStorePath,
                javaKeyStoreType,
                javaKeyStorePassword,
                keyAlias,
                keyPassword
        );
        return new ECodexEvidenceBuilder(
                javaKeyStorePath,
                javaKeyStoreType,
                javaKeyStorePassword,
                keyAlias,
                keyPassword
        );
    }

    @Bean
    @BusinessDomainScoped
    public HashValueBuilder hashValueBuilder() {
        return new HashValueBuilder(evidencesToolkitConfigurationProperties.getSignature().getDigestAlgorithm());
    }
}
