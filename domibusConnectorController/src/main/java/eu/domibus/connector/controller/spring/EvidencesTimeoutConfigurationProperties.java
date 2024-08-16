/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.controller.spring;

import eu.domibus.connector.lib.spring.DomibusConnectorDuration;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * The EvidencesTimeoutConfigurationProperties class represents the configuration properties
 * for evidences timeouts.
 * It is marked as a component with the bean name "evidencesTimeoutConfigurationProperties".
 * The configuration properties are read from the prefix "connector.controller.evidence"
 * in the default-connector.properties file.
 * It uses the DomibusConnectorDuration class to define the duration of each timeout.
 * The properties include a flag to indicate if timeouts for messages should be checked,
 * and the durations for various types of evidences timeouts.
 */
@Component(EvidencesTimeoutConfigurationProperties.BEAN_NAME)
@ConfigurationProperties(prefix = "connector.controller.evidence")
@Validated
@PropertySource("classpath:/eu/domibus/connector/controller/spring/default-connector.properties")
@Data
public class EvidencesTimeoutConfigurationProperties {
    public static final String BEAN_NAME = "evidencesTimeoutConfigurationProperties";
    /**
     * This property configures if timeouts for messages should be checked.
     */
    private boolean timeoutActive = true;
    /**
     * This property defines how often the timeouts for the evidences should be checked.
     * The default value is 1 minute (60000ms)
     */
    @SuppressWarnings("squid:S1135")
    // TODO put this into a global configuration, since the timer job configuration
    //  should be globally handled!
    @NotNull
    private DomibusConnectorDuration checkTimeout;
    /**
     * This property defines the timeout how long the connector should
     * wait for an relayREMMD evidence message after a message has been
     * successfully submitted to the gateway.
     * The default value is 24 hours
     * 0 disables the timeoutProcessor
     * If the timeout exceeds the according timeoutProcessor is started
     */
    @NotNull
    private DomibusConnectorDuration relayREMMDTimeout;
    /**
     * This property defines the timeout how long the connector should
     * wait for an relayREMMD evidence message after a message has been
     * successfully submitted to the gateway.
     * The default value is 12 hours
     * 0 disables the timeoutProcessor
     * If the timeout exceeds the according warnTimeoutProcessor is started
     */
    @NotNull
    private DomibusConnectorDuration relayREMMDWarnTimeout;
    /**
     * this property defines the timeout how long the connector should
     * wait for an deliveryTimeout evidence message after a message has been
     * successfully submitted to the gateway.
     * The default value is 24 hours
     * 0 disables the timeoutProcessor
     * If the timeout exceeds the according timeoutProcessor is started
     */
    @NotNull
    private DomibusConnectorDuration deliveryTimeout;
    /**
     * this property defines the timeout how long the connector should
     * wait for an deliveryTimeout evidence message after a message has been
     * successfully submitted to the gateway.
     * The default value is 24 hours
     * 0 disables the timeoutProcessor
     * If the timeout exceeds the according warnTimeoutProcessor is started
     */
    @NotNull
    private DomibusConnectorDuration deliveryWarnTimeout;
    /**
     * this property defines the timeout how long the connector should
     * wait for an retrievalTimeout evidence message after a message has been
     * successfully submitted to the gateway.
     * The default value is 24 hours
     * 0 disables the timeoutProcessor
     * If the timeout exceeds the according timeoutProcessor is started
     */
    @NotNull
    private DomibusConnectorDuration retrievalTimeout;
    /**
     * this property defines the timeout how long the connector should
     * wait for an retrievalTimeout evidence message after a message has been
     * successfully submitted to the gateway.
     * The default value is 24 hours
     * 0 disables the timeoutProcessor
     * If the timeout exceeds the according warnTimeoutProcessor is started
     */
    @NotNull
    private DomibusConnectorDuration retrievalWarnTimeout;
}
