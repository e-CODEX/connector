/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.controller.spring;

import eu.ecodex.connector.common.annotations.BusinessDomainScoped;
import eu.ecodex.utils.configuration.api.annotation.ConfigurationLabel;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * This class represents the properties for message processing by the connector.
 */
@Data
@Component
@BusinessDomainScoped
@ConfigurationProperties(prefix = ConnectorMessageProcessingProperties.PREFIX)
public class ConnectorMessageProcessingProperties {
    public static final String PREFIX = "processing";
    /**
     * should the by the connector created evidences
     * transported back to the backend
     * this would affect all kind of evidences which
     * are originally created by the connector.
     * <ul>
     *     <li>The automatically created SubmissionAcceptance</li>
     *     <li>The by a evidence trigger message created evidence message</li>
     * </ul>
     *
     * <p>The default is true
     */
    @ConfigurationLabel("Transport generated evidence back")
    private boolean sendGeneratedEvidencesToBackend = true; //
    /**
     * Should the connector create the EBMS id.
     */
    @ConfigurationLabel("EBMS generation enabled")
    private boolean ebmsIdGeneratorEnabled = true;
    private String ebmsIdSuffix = "ecodex.eu";
    private PModeVerificationMode outgoingPModeVerificationMode = PModeVerificationMode.RELAXED;
    private PModeVerificationMode incomingPModeVerificationMode = PModeVerificationMode.STRICT;

    /**
     * Enumeration representing the verification mode for P-Mode.
     * P-Mode verification mode determines how the P-Mode properties are verified during
     * message processing.
     */
    public enum PModeVerificationMode {
        CREATE, RELAXED, STRICT;
    }
}
