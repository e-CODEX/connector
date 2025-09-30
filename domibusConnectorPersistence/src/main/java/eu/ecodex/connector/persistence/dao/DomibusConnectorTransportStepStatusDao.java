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

import eu.ecodex.connector.persistence.model.PDomibusConnectorTransportStepStatusUpdate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * This interface provides data access methods for
 * the PDomibusConnectorTransportStepStatusUpdate entity.
 */
@Repository
public interface DomibusConnectorTransportStepStatusDao
    extends JpaRepository<PDomibusConnectorTransportStepStatusUpdate, Long> {

    @Query(
            value = "SELECT TSS.TRANSPORT_STEP_ID FROM DC_TRANSPORT_STEP_STATUS TSS "
                    + "WHERE TSS.STATE = 'accepted'",
            nativeQuery = true
    )
    List<Long> getCompletedTransportStepIds();

    @Modifying
    @Query(
            value = "DELETE FROM DC_TRANSPORT_STEP_STATUS DTS "
                    + "WHERE DTS.TRANSPORT_STEP_ID IN (:transportStepIds)",
            nativeQuery = true
    )
    void clean(@Param("transportStepIds") List<Long> transportStepIds);
}
