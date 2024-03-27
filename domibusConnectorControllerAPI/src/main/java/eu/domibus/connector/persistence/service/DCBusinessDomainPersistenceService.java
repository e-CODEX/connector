package eu.domibus.connector.persistence.service;

import eu.domibus.connector.domain.model.DomibusConnectorBusinessDomain;

import java.util.List;
import java.util.Optional;


public interface DCBusinessDomainPersistenceService {
    Optional<DomibusConnectorBusinessDomain> findById(DomibusConnectorBusinessDomain.BusinessDomainId businessDomainId);

    List<DomibusConnectorBusinessDomain> findAll();

    DomibusConnectorBusinessDomain update(DomibusConnectorBusinessDomain domibusConnectorBusinessDomain);

    DomibusConnectorBusinessDomain create(DomibusConnectorBusinessDomain businessDomain);
}
