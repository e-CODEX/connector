/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.common.service;

import eu.domibus.connector.domain.model.DomibusConnectorBusinessDomain;
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
