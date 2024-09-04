/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.common.service;

import eu.ecodex.connector.domain.model.DomibusConnectorBusinessDomain;
import java.util.Map;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Represents a change in the configuration of a business domain.
 */
public class BusinessDomainConfigurationChange extends ApplicationEvent {
    /**
     * The business domain where the configuration has changed.
     */
    @Getter
    private final DomibusConnectorBusinessDomain.BusinessDomainId businessDomainId;
    @Getter
    private final Map<String, String> changedConfiguration;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source               the object on which the event initially occurred or with
     *                             which the event is associated (never {@code null})
     * @param businessDomainId     the business domain, which has been changed
     * @param changedConfiguration the configuration changes itself
     */
    public BusinessDomainConfigurationChange(
        Object source,
        DomibusConnectorBusinessDomain.BusinessDomainId businessDomainId,
        Map<String, String> changedConfiguration) {
        super(source);
        this.businessDomainId = businessDomainId;
        this.changedConfiguration = changedConfiguration;
    }
}
