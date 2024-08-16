/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.security.container.service;

import eu.domibus.connector.domain.enums.AdvancedElectronicSystemType;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.dss.configuration.SignatureConfigurationProperties;
import eu.domibus.connector.dss.configuration.SignatureValidationConfigurationProperties;
import eu.domibus.connector.dss.service.CertificateSourceFromKeyStoreCreator;
import eu.domibus.connector.dss.service.CommonCertificateVerifierFactory;
import eu.domibus.connector.dss.service.DSSTrustedListsManager;
import eu.domibus.connector.lib.spring.configuration.StoreConfigurationProperties;
import eu.domibus.connector.security.aes.DCAuthenticationBasedTechnicalValidationServiceFactory;
import eu.domibus.connector.security.configuration.DCBusinessDocumentValidationConfigurationProperties;
import eu.domibus.connector.security.configuration.DCEcodexContainerProperties;
import eu.ecodex.dss.model.ECodexContainer;
import eu.ecodex.dss.model.SignatureCheckers;
import eu.ecodex.dss.model.SignatureParameters;
import eu.ecodex.dss.model.token.AdvancedSystemType;
import eu.ecodex.dss.model.token.TokenIssuer;
import eu.ecodex.dss.service.ECodexContainerService;
import eu.ecodex.dss.service.ECodexLegalValidationService;
import eu.ecodex.dss.service.ECodexTechnicalValidationService;
import eu.ecodex.dss.service.impl.dss.DSSECodexContainerService;
import eu.ecodex.dss.service.impl.dss.DSSECodexLegalValidationService;
import eu.ecodex.dss.service.impl.dss.DSSECodexTechnicalValidationService;
import eu.ecodex.dss.service.impl.dss.DSSSignatureChecker;
import eu.europa.esig.dss.policy.EtsiValidationPolicy;
import eu.europa.esig.dss.policy.ValidationPolicyFacade;
import eu.europa.esig.dss.spi.tsl.TrustedListsCertificateSource;
import eu.europa.esig.dss.spi.x509.CertificateSource;
import eu.europa.esig.dss.validation.CertificateVerifier;
import eu.europa.esig.dss.validation.executor.signature.DefaultSignatureProcessExecutor;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

/**
 * This class is a factory service for creating instances of ECodexContainerService. It provides
 * methods for creating different types of ECodexContainerService based on the configuration
 * properties and input parameters.
 *
 * <p>The constructor of ECodexContainerFactoryService accepts several dependencies that are
 * required for creating instances of ECodexContainerService. These dependencies include
 * configuration properties, trusted list manager, application context, and factories for creating
 * certificate sources and certificate verifiers.
 *
 * <p>The ECodexContainerService instance is created using the createECodexContainerService method.
 * This method takes a DomibusConnectorMessage parameter and returns an instance of
 * ECodexContainerService.
 *
 * <p>The factory service also includes private methods for creating the required components of the
 * ECodexContainerService. These methods include creating the technical business document validation
 * service, the legal validation service, the token issuer, and the signature checkers.
 *
 * <p>This class also includes helper methods for loading EtsiValidationPolicy from a validation
 * constraints XML file and for creating signature parameters for signature operations.
 */
@Service
@SuppressWarnings("checkstyle:LineLength")
public class ECodexContainerFactoryService {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(ECodexContainerFactoryService.class);
    public static final String TOKEN_PDF_SIGNATURE_CHECKER = "TokenPdfSignatureChecker";
    private final DCBusinessDocumentValidationConfigurationProperties dcBusinessDocConfig;
    private final DCEcodexContainerProperties dcEcodexContainerProperties;
    private final DSSTrustedListsManager dssTrustedListsManager;
    private final ApplicationContext applicationContext;
    private final CertificateSourceFromKeyStoreCreator certificateSourceFromKeyStoreCreator;
    private final CommonCertificateVerifierFactory commonCertificateVerifierFactory;

    /**
     * The ECodexContainerFactoryService class is responsible for creating instances of various
     * services related to ECodex container.
     */
    public ECodexContainerFactoryService(
        DCBusinessDocumentValidationConfigurationProperties dcBusinessDocConfig,
        DCEcodexContainerProperties dcEcodexContainerProperties,
        DSSTrustedListsManager dssTrustedListsManager,
        ApplicationContext applicationContext,
        CertificateSourceFromKeyStoreCreator certificateSourceFromKeyStoreCreator,
        CommonCertificateVerifierFactory commonCertificateVerifierFactory) {
        this.dcBusinessDocConfig = dcBusinessDocConfig;
        this.dcEcodexContainerProperties = dcEcodexContainerProperties;
        this.dssTrustedListsManager = dssTrustedListsManager;
        this.applicationContext = applicationContext;
        this.certificateSourceFromKeyStoreCreator = certificateSourceFromKeyStoreCreator;
        this.commonCertificateVerifierFactory = commonCertificateVerifierFactory;
    }

    /**
     * Creates an instance of ECodexContainerService.
     *
     * @param message the DomibusConnectorMessage object
     * @return an instance of ECodexContainerService
     */
    public ECodexContainerService createECodexContainerService(DomibusConnectorMessage message) {
        return new DSSECodexContainerService(
            createTechnicalBusinessDocumentValidationService(message),
            createLegalValidationService(),
            createSignatureParameters(),
            tokenIssuer(message),
            createSignatureCheckers()
        );
    }

    /**
     * Creates an instance of ECodexLegalValidationService.
     *
     * @return an instance of ECodexLegalValidationService
     */
    private ECodexLegalValidationService createLegalValidationService() {
        return new DSSECodexLegalValidationService();
    }

    private TokenIssuer tokenIssuer(DomibusConnectorMessage message) {
        String country = dcBusinessDocConfig.getCountry();
        var tokenIssuer = new TokenIssuer();
        tokenIssuer.setCountry(country);
        tokenIssuer.setServiceProvider(dcBusinessDocConfig.getServiceProvider());
        tokenIssuer.setAdvancedElectronicSystem(getAdvancedSystemType(message));

        LOGGER.debug("Using TokenIssuer [{}]", tokenIssuer);
        return tokenIssuer;
    }

    /**
     * Creates a technical business document validation service based on the type of the advanced
     * electronic system.
     *
     * @param message the DomibusConnectorMessage object representing the business document
     * @return an instance of ECodexTechnicalValidationService based on the advanced electronic
     *      system type
     * @throws IllegalArgumentException if the advanced electronic system type is illegal
     */
    public ECodexTechnicalValidationService createTechnicalBusinessDocumentValidationService(
        DomibusConnectorMessage message) {
        var advancedElectronicSystemType = getAdvancedSystemType(message);
        return switch (advancedElectronicSystemType) {
            case SIGNATURE_BASED -> createDSSECodexTechnicalValidationService(
                dcBusinessDocConfig.getSignatureValidation());
            case AUTHENTICATION_BASED -> createDSSAuthenticationBasedValidationService(
                message, dcBusinessDocConfig.getAuthenticationValidation());
        };
    }

    /**
     * Retrieves the advanced system type for a given DomibusConnectorMessage.
     *
     * @param message the DomibusConnectorMessage object representing the business document
     * @return the AdvancedSystemType of the given DomibusConnectorMessage
     * @throws NullPointerException     if the message or messageProcess settings are null
     * @throws IllegalArgumentException if the AdvancedSystemType is not part of the configured
     *                                  allowed ones
     */
    private AdvancedSystemType getAdvancedSystemType(DomibusConnectorMessage message) {
        Objects.requireNonNull(message, "message is not allowed to be null");
        Objects.requireNonNull(
            message.getDcMessageProcessSettings(),
            "messageProcess settings are not allowed to be null!"
        );
        AdvancedElectronicSystemType validationServiceName =
            message.getDcMessageProcessSettings().getValidationServiceName();

        if (validationServiceName != null) {
            LOGGER.debug("Using AdvancedSystemType [{}] from message", validationServiceName);
        } else {
            validationServiceName = dcBusinessDocConfig.getDefaultAdvancedSystemType();
            LOGGER.debug("Using AdvancedSystemType [{}] from configuration", validationServiceName);
            Objects.requireNonNull(
                validationServiceName, "AdvancedSystemType is null in configuration!");
        }
        if (!dcBusinessDocConfig.getAllowedAdvancedSystemTypes().contains(validationServiceName)) {
            var error = String.format(
                "The used AdvancedSystemType [%s] is not part of the configured allowed ones [%s]",
                validationServiceName,
                dcBusinessDocConfig.getAllowedAdvancedSystemTypes().stream().map(Object::toString)
                                   .collect(Collectors.joining(","))
            );
            throw new IllegalArgumentException(error);
        }

        return AdvancedSystemType.valueOf(validationServiceName.name());
    }

    /**
     * Creates a DSS authentication-based validation service for ECodex. This method creates an
     * instance of {@link ECodexTechnicalValidationService} that performs the technical validation
     * of the main business document based on authentication.
     *
     * @param message the {@link DomibusConnectorMessage} object representing the business document
     * @param config  the
     *                {@link
     *                DCBusinessDocumentValidationConfigurationProperties.AuthenticationValidationConfigurationProperties}
     *                configuration for authentication-based validation
     * @return an instance of {@link ECodexTechnicalValidationService} based on authentication
     * @throws NullPointerException if the config parameter is null
     */
    private ECodexTechnicalValidationService createDSSAuthenticationBasedValidationService(
        DomibusConnectorMessage message,
        DCBusinessDocumentValidationConfigurationProperties
            .AuthenticationValidationConfigurationProperties config) {
        Objects.requireNonNull(
            config, "AuthenticationValidationConfigurationProperties is not allowed to be null!");
        Class<? extends DCAuthenticationBasedTechnicalValidationServiceFactory>
            authenticatorServiceFactoryClass = config.getAuthenticatorServiceFactoryClass();
        DCAuthenticationBasedTechnicalValidationServiceFactory bean =
            applicationContext.getBean(authenticatorServiceFactoryClass);
        return bean.createTechnicalValidationService(message, config);
    }

    /**
     * Creates a DSSECodexTechnicalValidationService instance with the given
     * SignatureValidationConfigurationProperties.
     *
     * @param signatureValidationConfigurationProperties The configuration properties for signature
     *                                                   validation.
     * @return An instance of DSSECodexTechnicalValidationService.
     */
    private ECodexTechnicalValidationService createDSSECodexTechnicalValidationService(
        SignatureValidationConfigurationProperties signatureValidationConfigurationProperties) {
        String trustedListSourceName =
            signatureValidationConfigurationProperties.getTrustedListSource();
        Optional<TrustedListsCertificateSource> certificateSource =
            dssTrustedListsManager.getCertificateSource(trustedListSourceName);

        StoreConfigurationProperties ignoreStore =
            signatureValidationConfigurationProperties.getIgnoreStore();
        CertificateSource ignoreCertificates = null;
        if (ignoreStore != null) {
            ignoreCertificates =
                certificateSourceFromKeyStoreCreator.createCertificateSourceFromStore(ignoreStore);
        }

        CertificateVerifier businessDocumentCertificateVerifier =
            commonCertificateVerifierFactory.createCommonCertificateVerifier(
                signatureValidationConfigurationProperties);

        var etsiValidationPolicy =
            loadEtsiValidationPolicy(signatureValidationConfigurationProperties);

        return new DSSECodexTechnicalValidationService(
            etsiValidationPolicy,
            businessDocumentCertificateVerifier,
            new DefaultSignatureProcessExecutor(),
            certificateSource,
            Optional.ofNullable(ignoreCertificates)
        );
    }

    /**
     * Loads the EtsiValidationPolicy based on the provided
     * SignatureValidationConfigurationProperties.
     *
     * @param signatureValidationConfigurationProperties The configuration properties for signature
     *                                                   validation. It contains properties for the
     *                                                   validation constraints XML file path.
     * @return The loaded EtsiValidationPolicy instance.
     * @throws RuntimeException If there is an error loading the resource or parsing the
     *                          EtsiValidationPolicy.
     */
    private EtsiValidationPolicy loadEtsiValidationPolicy(
        SignatureValidationConfigurationProperties signatureValidationConfigurationProperties) {
        try {
            var resource = applicationContext.getResource(
                signatureValidationConfigurationProperties.getValidationConstraintsXml());
            var policyDataStream = resource.getInputStream();
            EtsiValidationPolicy validationPolicy;
            validationPolicy = (EtsiValidationPolicy) ValidationPolicyFacade.newFacade()
                                                                            .getValidationPolicy(
                                                                                policyDataStream);
            return validationPolicy;
        } catch (IOException ioe) {
            throw new RuntimeException("Error while loading resource", ioe);
        } catch (XMLStreamException | JAXBException | SAXException e) {
            throw new RuntimeException("Error while parsing EtsiValidationPolicy", e);
        }
    }

    private SignatureCheckers createSignatureCheckers() {
        return new SignatureCheckers(
            asicsSignatureChecker(),
            xmlTokenSignatureChecker(),
            pdfTokenSignatureChecker()
        );
    }

    private DSSSignatureChecker<ECodexContainer.TokenPdfTypeECodex> pdfTokenSignatureChecker() {
        return DSSSignatureChecker.builder()
                                  .withSignatureCheckerName(TOKEN_PDF_SIGNATURE_CHECKER)
                                  .withCertificateVerifier(eCodexContainerCertificateVerifier())
                                  .withValidationConstraints(signatureValidationConstraintsXml())
                                  .withProcessExecutor(new DefaultSignatureProcessExecutor())
                                  .build(new ECodexContainer.TokenPdfTypeECodex())
            ;
    }

    private DSSSignatureChecker<ECodexContainer.TokenXmlTypesECodex> xmlTokenSignatureChecker() {
        return DSSSignatureChecker.builder()
                                  .withSignatureCheckerName(TOKEN_PDF_SIGNATURE_CHECKER)
                                  .withCertificateVerifier(eCodexContainerCertificateVerifier())
                                  .withValidationConstraints(signatureValidationConstraintsXml())
                                  .withProcessExecutor(new DefaultSignatureProcessExecutor())
                                  .build(new ECodexContainer.TokenXmlTypesECodex())
            ;
    }

    private DSSSignatureChecker<ECodexContainer.AsicDocumentTypeECodex> asicsSignatureChecker() {
        return DSSSignatureChecker.builder()
                                  .withCertificateVerifier(eCodexContainerCertificateVerifier())
                                  .withSignatureCheckerName(TOKEN_PDF_SIGNATURE_CHECKER)
                                  .withValidationConstraints(signatureValidationConstraintsXml())
                                  .withProcessExecutor(new DefaultSignatureProcessExecutor())
                                  .withConnectorCertificateSource(connectorCertificateSource())
                                  .build(new ECodexContainer.AsicDocumentTypeECodex())
            ;
    }

    @SuppressWarnings("checkstyle:MethodName")
    private CertificateVerifier eCodexContainerCertificateVerifier() {
        return commonCertificateVerifierFactory.createCommonCertificateVerifier(
            dcEcodexContainerProperties.getSignatureValidation());
    }

    private CertificateSource connectorCertificateSource() {
        StoreConfigurationProperties trustStore =
            dcEcodexContainerProperties.getSignatureValidation().getTrustStore();
        return certificateSourceFromKeyStoreCreator.createCertificateSourceFromStore(trustStore);
    }

    private Resource signatureValidationConstraintsXml() {
        return applicationContext.getResource(dcEcodexContainerProperties
                                                  .getSignatureValidation()
                                                  .getValidationConstraintsXml());
    }

    private SignatureParameters createSignatureParameters() {
        try {
            LOGGER.debug("creatingSignatureParameters");

            SignatureConfigurationProperties signatureConfig =
                dcEcodexContainerProperties.getSignature();
            CertificateSourceFromKeyStoreCreator.SignatureConnectionAndPrivateKeyEntry
                signatureConnectionFromStore =
                certificateSourceFromKeyStoreCreator.createSignatureConnectionFromStore(
                    signatureConfig);

            var signatureParameters = new SignatureParameters();
            signatureParameters.setSignatureTokenConnection(
                signatureConnectionFromStore.getSignatureTokenConnection());
            signatureParameters.setPrivateKey(signatureConnectionFromStore.getDssPrivateKeyEntry());
            signatureParameters.setDigestAlgorithm(signatureConfig.getDigestAlgorithm());
            signatureParameters.setEncryptionAlgorithm(signatureConfig.getEncryptionAlgorithm());

            return signatureParameters;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
