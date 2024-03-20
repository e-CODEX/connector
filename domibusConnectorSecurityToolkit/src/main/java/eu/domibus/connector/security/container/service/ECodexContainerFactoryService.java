package eu.domibus.connector.security.container.service;

import eu.domibus.connector.domain.enums.AdvancedElectronicSystemType;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.dss.configuration.SignatureValidationConfigurationProperties;
import eu.domibus.connector.dss.service.CertificateSourceFromKeyStoreCreator;
import eu.domibus.connector.dss.service.CommonCertificateVerifierFactory;
import eu.domibus.connector.dss.service.DSSTrustedListsManager;
import eu.domibus.connector.lib.spring.configuration.StoreConfigurationProperties;
import eu.domibus.connector.security.aes.DCAuthenticationBasedTechnicalValidationServiceFactory;
import eu.domibus.connector.security.configuration.DCBusinessDocumentValidationConfigurationProperties;
import eu.domibus.connector.security.configuration.DCEcodexContainerProperties;
import eu.domibus.connector.dss.configuration.SignatureConfigurationProperties;
import eu.ecodex.dss.model.ECodexContainer;
import eu.ecodex.dss.model.SignatureCheckers;
import eu.ecodex.dss.model.SignatureParameters;
import eu.ecodex.dss.model.token.AdvancedSystemType;
import eu.ecodex.dss.model.token.TokenIssuer;
import eu.ecodex.dss.service.ECodexContainerService;
import eu.ecodex.dss.service.ECodexLegalValidationService;
import eu.ecodex.dss.service.ECodexTechnicalValidationService;
import eu.ecodex.dss.service.impl.dss.*;
import eu.europa.esig.dss.policy.EtsiValidationPolicy;
import eu.europa.esig.dss.policy.ValidationPolicyFacade;
import eu.europa.esig.dss.spi.tsl.TrustedListsCertificateSource;
import eu.europa.esig.dss.spi.x509.CertificateSource;
import eu.europa.esig.dss.validation.CertificateVerifier;
import eu.europa.esig.dss.validation.executor.signature.DefaultSignatureProcessExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ECodexContainerFactoryService {

    private final static Logger LOGGER = LoggerFactory.getLogger(ECodexContainerFactoryService.class);

    private final DCBusinessDocumentValidationConfigurationProperties dcBusinessDocConfig;
    private final DCEcodexContainerProperties dcEcodexContainerProperties;
    private final DSSTrustedListsManager dssTrustedListsManager;
    private final ApplicationContext applicationContext;
    private final CertificateSourceFromKeyStoreCreator certificateSourceFromKeyStoreCreator;
    private final CommonCertificateVerifierFactory commonCertificateVerifierFactory;

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

    public ECodexContainerService createECodexContainerService(DomibusConnectorMessage message) {

        DSSECodexContainerService containerService = new DSSECodexContainerService(
                createTechnicalBusinessDocumentValidationService(message),
                createLegalValidationService(),
                createSignatureParameters(),
                tokenIssuer(message),
                createSignatureCheckers()
        );

        return containerService;
    }

    private ECodexLegalValidationService createLegalValidationService() {
        return new DSSECodexLegalValidationService();
    }

    private TokenIssuer tokenIssuer(DomibusConnectorMessage message) {
        String country = dcBusinessDocConfig.getCountry();
        TokenIssuer tokenIssuer = new TokenIssuer();
        tokenIssuer.setCountry(country);
        tokenIssuer.setServiceProvider(dcBusinessDocConfig.getServiceProvider());
        tokenIssuer.setAdvancedElectronicSystem(getAdvancedSystemType(message));

        LOGGER.debug("Using TokenIssuer [{}]", tokenIssuer);
        return tokenIssuer;
    }

    public ECodexTechnicalValidationService createTechnicalBusinessDocumentValidationService(DomibusConnectorMessage message) {
        AdvancedSystemType advancedElectronicSystemType = getAdvancedSystemType(message);
        switch(advancedElectronicSystemType) {
            case SIGNATURE_BASED: return createDSSECodexTechnicalValidationService(dcBusinessDocConfig.getSignatureValidation());
            case AUTHENTICATION_BASED: return createDSSAuthenticationBasedValidationService(message, dcBusinessDocConfig.getAuthenticationValidation());
            default: throw new IllegalArgumentException("Illegal AdvancedSystemType");
        }
    }

    private AdvancedSystemType getAdvancedSystemType(DomibusConnectorMessage message) {
        Objects.requireNonNull(message, "message is not allowed to be null");
        Objects.requireNonNull(message.getDcMessageProcessSettings(), "messageProcess settings are not allowed to be null!");
        AdvancedElectronicSystemType validationServiceName = message.getDcMessageProcessSettings().getValidationServiceName();


        if (validationServiceName != null) {
            LOGGER.debug("Using AdvancedSystemType [{}] from message", validationServiceName);
        } else {
            validationServiceName = dcBusinessDocConfig.getDefaultAdvancedSystemType();
            LOGGER.debug("Using AdvancedSystemType [{}] from configuration", validationServiceName);
            Objects.requireNonNull(validationServiceName, "AdvancedSystemType is null in configuration!");
        }
        if (!dcBusinessDocConfig.getAllowedAdvancedSystemTypes().contains(validationServiceName)) {
            String error = String.format("The used AdvancedSystemType [%s] is not part of the configured allowed ones [%s]",
                    validationServiceName, dcBusinessDocConfig.getAllowedAdvancedSystemTypes().stream().map(Object::toString).collect(Collectors.joining(",")));
            throw new IllegalArgumentException(error);
        }

        AdvancedSystemType advancedElectronicSystemType = AdvancedSystemType.valueOf(validationServiceName.name());;
        return advancedElectronicSystemType;
    }

    private ECodexTechnicalValidationService createDSSAuthenticationBasedValidationService(DomibusConnectorMessage message, DCBusinessDocumentValidationConfigurationProperties.AuthenticationValidationConfigurationProperties config) {
        Objects.requireNonNull(config, "AuthenticationValidationConfigurationProperties is not allowed to be null!");
        Class<? extends DCAuthenticationBasedTechnicalValidationServiceFactory> authenticatorServiceFactoryClass = config.getAuthenticatorServiceFactoryClass();
        DCAuthenticationBasedTechnicalValidationServiceFactory bean = applicationContext.getBean(authenticatorServiceFactoryClass);
        return bean.createTechnicalValidationService(message, config);
    }

    private ECodexTechnicalValidationService createDSSECodexTechnicalValidationService(SignatureValidationConfigurationProperties signatureValidationConfigurationProperties) {
        String trustedListSourceName = signatureValidationConfigurationProperties.getTrustedListSource();
        Optional<TrustedListsCertificateSource> certificateSource = dssTrustedListsManager.getCertificateSource(trustedListSourceName);

        StoreConfigurationProperties ignoreStore = signatureValidationConfigurationProperties.getIgnoreStore();
        CertificateSource ignoreCertificates = null;
        if (ignoreStore != null) {
             ignoreCertificates = certificateSourceFromKeyStoreCreator.createCertificateSourceFromStore(ignoreStore);
        }

        CertificateVerifier businessDocumentCertificateVerifier = commonCertificateVerifierFactory.createCommonCertificateVerifier(signatureValidationConfigurationProperties);

        EtsiValidationPolicy etsiValidationPolicy = loadEtsiValidationPolicy(signatureValidationConfigurationProperties);

        return new DSSECodexTechnicalValidationService(
                etsiValidationPolicy,
                businessDocumentCertificateVerifier,
                new DefaultSignatureProcessExecutor(),
                certificateSource,
                Optional.ofNullable(ignoreCertificates)
                );

    }

    private EtsiValidationPolicy loadEtsiValidationPolicy(SignatureValidationConfigurationProperties signatureValidationConfigurationProperties) {
        try {
            Resource resource = applicationContext.getResource(signatureValidationConfigurationProperties.getValidationConstraintsXml());
            InputStream policyDataStream = resource.getInputStream();
            EtsiValidationPolicy validationPolicy = null;
            validationPolicy = (EtsiValidationPolicy) ValidationPolicyFacade.newFacade().getValidationPolicy(policyDataStream);
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
                pdfTokenSignatureChecker());

    }

    private DSSSignatureChecker<ECodexContainer.TokenPdfTypeECodex> pdfTokenSignatureChecker() {
        return DSSSignatureChecker.builder()
                .withSignatureCheckerName("TokenPdfSignatureChecker")
                .withCertificateVerifier(eCodexContainerCertificateVerifier())
                .withValidationConstraints(signatureValidationConstraintsXml())
                .withProcessExecutor(new DefaultSignatureProcessExecutor())
                .build(new ECodexContainer.TokenPdfTypeECodex())
                ;
    }

    private DSSSignatureChecker<ECodexContainer.TokenXmlTypesECodex> xmlTokenSignatureChecker() {
        return DSSSignatureChecker.builder()
                .withSignatureCheckerName("TokenPdfSignatureChecker")
                .withCertificateVerifier(eCodexContainerCertificateVerifier())
                .withValidationConstraints(signatureValidationConstraintsXml())
                .withProcessExecutor(new DefaultSignatureProcessExecutor())
                .build(new ECodexContainer.TokenXmlTypesECodex())
                ;
    }

    private DSSSignatureChecker<ECodexContainer.AsicDocumentTypeECodex> asicsSignatureChecker() {
        return DSSSignatureChecker.builder()
                .withCertificateVerifier(eCodexContainerCertificateVerifier())
                .withSignatureCheckerName("TokenPdfSignatureChecker")
                .withValidationConstraints(signatureValidationConstraintsXml())
                .withProcessExecutor(new DefaultSignatureProcessExecutor())
                .withConnectorCertificateSource(connectorCertificateSource())
                .build(new ECodexContainer.AsicDocumentTypeECodex())
                ;
    }

    private CertificateVerifier eCodexContainerCertificateVerifier() {
        return commonCertificateVerifierFactory.createCommonCertificateVerifier(dcEcodexContainerProperties.getSignatureValidation());
    }

    private CertificateSource connectorCertificateSource() {
        StoreConfigurationProperties trustStore = dcEcodexContainerProperties.getSignatureValidation().getTrustStore();
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

            SignatureConfigurationProperties signatureConfig = dcEcodexContainerProperties.getSignature();
            CertificateSourceFromKeyStoreCreator.SignatureConnectionAndPrivateKeyEntry signatureConnectionFromStore = certificateSourceFromKeyStoreCreator.createSignatureConnectionFromStore(signatureConfig);

            SignatureParameters signatureParameters = new SignatureParameters();
            signatureParameters.setSignatureTokenConnection(signatureConnectionFromStore.getSignatureTokenConnection());
            signatureParameters.setPrivateKey(signatureConnectionFromStore.getDssPrivateKeyEntry());
            signatureParameters.setDigestAlgorithm(signatureConfig.getDigestAlgorithm());
            signatureParameters.setEncryptionAlgorithm(signatureConfig.getEncryptionAlgorithm());

            return signatureParameters;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


}
