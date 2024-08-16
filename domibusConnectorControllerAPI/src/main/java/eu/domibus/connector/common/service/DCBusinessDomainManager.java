/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.common.service;

import eu.domibus.connector.domain.model.DomibusConnectorBusinessDomain;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Represents a manager for handling business domains in the DC application.
 */
public interface DCBusinessDomainManager {
    String BUSINESS_DOMAIN_PROPERTY_PREFIX = "connector.businessDomain";

    List<DomibusConnectorBusinessDomain.BusinessDomainId> getActiveBusinessDomainIds();

    Optional<DomibusConnectorBusinessDomain> getBusinessDomain(
        DomibusConnectorBusinessDomain.BusinessDomainId id);

    void updateConfig(DomibusConnectorBusinessDomain.BusinessDomainId id,
                      Map<String, String> properties);

    void createBusinessDomain(DomibusConnectorBusinessDomain businessDomain);
}
