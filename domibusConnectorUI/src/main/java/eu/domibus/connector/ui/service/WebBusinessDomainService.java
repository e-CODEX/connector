/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.ui.service;

import com.vaadin.flow.spring.annotation.UIScope;
import eu.domibus.connector.domain.model.DomibusConnectorBusinessDomain;
import org.springframework.stereotype.Component;

/**
 * The WebBusinessDomainService class is responsible for retrieving the current business domain
 * of the Domibus Connector.
 */
@UIScope
@Component
@SuppressWarnings("squid:S1135")
public class WebBusinessDomainService {
    /**
     * Retrieves the current business domain of the Domibus Connector.
     *
     * @return the current business domain as a BusinessDomainId object
     */
    public DomibusConnectorBusinessDomain.BusinessDomainId getCurrentBusinessDomain() {
        // TODO: for IMPL Business Domain Configuration within UI,
        // extend this to retrieve current business Domain
        return DomibusConnectorBusinessDomain.getDefaultMessageLaneId();
    }
}
