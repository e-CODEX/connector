/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.persistence.dao;

import eu.ecodex.connector.persistence.model.PDomibusConnectorLinkPartner;
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
