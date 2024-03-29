package eu.domibus.connector.persistence.dao;

import eu.domibus.connector.persistence.model.PDomibusConnectorLinkPartner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface DomibusConnectorLinkPartnerDao extends JpaRepository<PDomibusConnectorLinkPartner, Long> {
    Optional<PDomibusConnectorLinkPartner> findOneBackendByLinkNameAndEnabledIsTrue(String name);

    Optional<PDomibusConnectorLinkPartner> findOneByLinkName(String linkName);

    @Query("SELECT max(e.id) from PDomibusConnectorLinkPartner e")
    Long findHighestId();

    List<PDomibusConnectorLinkPartner> findAllByEnabledIsTrue();
}
