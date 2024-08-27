/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
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
