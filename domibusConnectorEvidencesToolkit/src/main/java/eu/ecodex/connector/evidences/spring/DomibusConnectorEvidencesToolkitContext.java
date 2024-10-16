/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.evidences.spring;

import eu.ecodex.connector.common.annotations.BusinessDomainScoped;
import eu.ecodex.connector.common.service.DCKeyStoreService;
import eu.ecodex.connector.evidences.DomibusConnectorEvidencesToolkit;
import eu.ecodex.connector.evidences.HashValueBuilder;
import eu.ecodex.evidences.ECodexEvidenceBuilder;
import eu.ecodex.evidences.EvidenceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * The DomibusConnectorEvidencesToolkitContext class is responsible for configuring and providing
 * beans for the creation of eCodex Evidence Messages.
 *
 * <p>It is annotated with @Configuration to indicate that it is a configuration class. It is also
 * annotated with @ComponentScan to specify the base package classes that need to be scanned for
 * component scanning, specifically the DomibusConnectorEvidencesToolkit class. It is enabled for
 *
 * <p>@ConfigurationProperties and scoped with @BusinessDomainScoped to create multiple instances
 * with separate state based on the business domain.
 *
 * <p>Note: This class is used in conjunction with the DomibusConnectorEvidencesToolkit interface.
 */
@Configuration
@ComponentScan(basePackageClasses = {DomibusConnectorEvidencesToolkit.class})
@EnableConfigurationProperties
@BusinessDomainScoped
public class DomibusConnectorEvidencesToolkitContext {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(DomibusConnectorEvidencesToolkitContext.class);
    private final EvidencesToolkitConfigurationProperties evidencesToolkitConfigurationProperties;
    private final DCKeyStoreService keyStoreService;

    public DomibusConnectorEvidencesToolkitContext(
        EvidencesToolkitConfigurationProperties evidencesToolkitConfigurationProperties,
        DCKeyStoreService keyStoreService) {
        this.evidencesToolkitConfigurationProperties = evidencesToolkitConfigurationProperties;
        this.keyStoreService = keyStoreService;
    }

    /**
     * This method creates a new instance of the EvidenceBuilder interface. It retrieves the
     * necessary configuration properties from the EvidencesToolkitConfigurationProperties class and
     * the DCKeyStoreService class. It then uses these properties to initialize an instance of the
     * ECodexEvidenceBuilder class.
     *
     * @return an instance of the EvidenceBuilder interface
     */
    @Bean
    @BusinessDomainScoped
    public EvidenceBuilder domibusConnectorEvidenceBuilder() {
        var javaKeyStorePath = keyStoreService.loadKeyStoreAsResource(
            evidencesToolkitConfigurationProperties.getSignature().getKeyStore());
        String javaKeyStorePassword =
            evidencesToolkitConfigurationProperties.getSignature().getKeyStore().getPassword();
        String javaKeyStoreType =
            evidencesToolkitConfigurationProperties.getSignature().getKeyStore().getType();
        String keyAlias =
            evidencesToolkitConfigurationProperties.getSignature().getPrivateKey().getAlias();
        String keyPassword =
            evidencesToolkitConfigurationProperties.getSignature().getPrivateKey().getPassword();
        LOGGER.debug(
            "Creating ECodexEvidenceBuilder with keyStorePath [{}], keyStoreType [{}], "
                + "keyStorePassword [{}], keyAlias [{}], keyPassword [password={}]",
            javaKeyStorePath, javaKeyStoreType, javaKeyStorePassword, keyAlias, keyPassword
        );
        return new ECodexEvidenceBuilder(
            javaKeyStorePath, javaKeyStoreType, javaKeyStorePassword, keyAlias, keyPassword);
    }

    @Bean
    @BusinessDomainScoped
    public HashValueBuilder hashValueBuilder() {
        return new HashValueBuilder(
            evidencesToolkitConfigurationProperties.getSignature().getDigestAlgorithm());
    }
}
