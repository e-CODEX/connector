/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.controller.spring;

import eu.domibus.connector.common.annotations.BusinessDomainScoped;
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
