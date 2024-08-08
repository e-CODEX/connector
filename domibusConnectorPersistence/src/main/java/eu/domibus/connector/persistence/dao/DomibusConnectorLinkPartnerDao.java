/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.persistence.dao;

import eu.domibus.connector.persistence.model.PDomibusConnectorLinkPartner;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * The DomibusConnectorLinkPartnerDao interface provides methods to perform CRUD operations on the
 * DC_LINK_PARTNER table in the database.
 */
@Repository
public interface DomibusConnectorLinkPartnerDao
    extends JpaRepository<PDomibusConnectorLinkPartner, Long> {
    Optional<PDomibusConnectorLinkPartner> findOneBackendByLinkNameAndEnabledIsTrue(String name);

    Optional<PDomibusConnectorLinkPartner> findOneByLinkName(String linkName);

    @Query("SELECT max(e.id) from PDomibusConnectorLinkPartner e")
    Long findHighestId();

    List<PDomibusConnectorLinkPartner> findAllByEnabledIsTrue();
}
