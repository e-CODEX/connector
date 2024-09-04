/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.c2ctests.config;

import eu.ecodex.connector.domain.configuration.EvidenceActionServiceConfigurationProperties;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

/**
 * This configuration properties are for defining the connector to connector test action and
 * service.
 *
 * <p>If the connector receives a message with the configured service and action the message will
 * not be delivered to the gateway
 */
@ConfigurationProperties(prefix = ConnectorTestConfigurationProperties.PREFIX)
@Component
public class ConnectorTestConfigurationProperties {
    public static final String PREFIX = "c2ctests";
    private boolean enabled = true;
    /**
     * Should a delivery evidence sent.
     */
    private boolean respondWithDeliveryEvidence = true;
    @NotNull
    private Resource defaultBusinessXml =
        new ClassPathResource("/eu/ecodex/connector/config/c2ctests/testbusinessxml.xml");
    @NotNull
    private Resource defaultBusinessPdf =
        new ClassPathResource("/eu/ecodex/connector/config/c2ctests/testbusinesspdf.pdf");
    @Valid
    private EvidenceActionServiceConfigurationProperties.AS4Service service =
        new EvidenceActionServiceConfigurationProperties.AS4Service(
            "Connector-TEST",
            "urn:e-codex:services:"
        );
    @Valid
    @NotNull
    private EvidenceActionServiceConfigurationProperties.AS4Action action =
        new EvidenceActionServiceConfigurationProperties.AS4Action("ConTest_Form");

    public EvidenceActionServiceConfigurationProperties.AS4Service getService() {
        return service;
    }

    public void setService(EvidenceActionServiceConfigurationProperties.AS4Service service) {
        this.service = service;
    }

    public EvidenceActionServiceConfigurationProperties.AS4Action getAction() {
        return action;
    }

    public void setAction(EvidenceActionServiceConfigurationProperties.AS4Action action) {
        this.action = action;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isRespondWithDeliveryEvidence() {
        return respondWithDeliveryEvidence;
    }

    public void setRespondWithDeliveryEvidence(boolean respondWithDeliveryEvidence) {
        this.respondWithDeliveryEvidence = respondWithDeliveryEvidence;
    }

    public Resource getDefaultBusinessXml() {
        return defaultBusinessXml;
    }

    public void setDefaultBusinessXml(Resource defaultBusinessXml) {
        this.defaultBusinessXml = defaultBusinessXml;
    }

    public Resource getDefaultBusinessPdf() {
        return defaultBusinessPdf;
    }

    public void setDefaultBusinessPdf(Resource defaultBusinessPdf) {
        this.defaultBusinessPdf = defaultBusinessPdf;
    }
}
