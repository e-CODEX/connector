package eu.domibus.connector.common.persistence.dao;

import eu.domibus.connector.domain.model.DomibusConnectorBusinessDomain;
import eu.domibus.connector.persistence.model.PDomibusConnectorMessageLane;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface DomibusConnectorBusinessDomainDao extends JpaRepository<PDomibusConnectorMessageLane, Long> {
    Optional<PDomibusConnectorMessageLane> findByName(DomibusConnectorBusinessDomain.BusinessDomainId name);
}
