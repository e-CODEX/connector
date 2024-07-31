/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
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
