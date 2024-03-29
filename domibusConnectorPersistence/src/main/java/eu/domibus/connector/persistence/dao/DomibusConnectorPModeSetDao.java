package eu.domibus.connector.persistence.dao;

import eu.domibus.connector.domain.model.DomibusConnectorBusinessDomain;
import eu.domibus.connector.persistence.model.PDomibusConnectorPModeSet;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface DomibusConnectorPModeSetDao extends CrudRepository<PDomibusConnectorPModeSet, Long> {
    @Query(
            "SELECT p FROM PDomibusConnectorPModeSet p WHERE p.active = true AND p.messageLane.name=?1 ORDER by p" +
                    ".created"
    )
    List<PDomibusConnectorPModeSet> getCurrentActivePModeSet(DomibusConnectorBusinessDomain.BusinessDomainId id);

    @Query(
            "SELECT p FROM PDomibusConnectorPModeSet p WHERE p.active = false AND p.messageLane.name=?1 ORDER by p" +
                    ".created desc"
    )
    List<PDomibusConnectorPModeSet> getInactivePModeSets(DomibusConnectorBusinessDomain.BusinessDomainId id);
}
