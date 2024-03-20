package eu.domibus.connector.common.service;

import eu.domibus.connector.domain.model.DomibusConnectorBusinessDomain;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface DCBusinessDomainManager {

    public static final String BUSINESS_DOMAIN_PROPERTY_PREFIX = "connector.businessDomain";

    public List<DomibusConnectorBusinessDomain.BusinessDomainId> getActiveBusinessDomainIds();

    Optional<DomibusConnectorBusinessDomain> getBusinessDomain(DomibusConnectorBusinessDomain.BusinessDomainId id);

    void updateConfig(DomibusConnectorBusinessDomain.BusinessDomainId id, Map<String, String> properties);

    void createBusinessDomain(DomibusConnectorBusinessDomain businessDomain);

}
