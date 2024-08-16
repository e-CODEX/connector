/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.security.aes;

import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.security.configuration.DCBusinessDocumentValidationConfigurationProperties;
import eu.ecodex.dss.service.ECodexTechnicalValidationService;

/**
 * The DCAuthenticationBasedTechnicalValidationServiceFactory interface is responsible for creating
 * instances of the ECodexTechnicalValidationService interface for performing authentication-based
 * technical validation. Implementing classes must provide an implementation for the
 * createTechnicalValidationService method, which takes in a DomibusConnectorMessage object and a
 * DCBusinessDocumentValidation
 * ConfigurationProperties.AuthenticationValidationConfigurationProperties object.
 *
 * <p>The ECodexTechnicalValidationService interface represents the contract for creating the
 * technical validation (result and report) on the main business document. Implementing classes of
 * ECodexTechnicalValidationService must guarantee that the object instances provided in parameters
 * do not become tampered and must have thread-safe execution.
 *
 * <p>Disclaimer: Project owner e-CODEX
 */
public interface DCAuthenticationBasedTechnicalValidationServiceFactory {
    ECodexTechnicalValidationService createTechnicalValidationService(
        DomibusConnectorMessage message,
        DCBusinessDocumentValidationConfigurationProperties
            .AuthenticationValidationConfigurationProperties config);
}
