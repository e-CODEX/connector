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

import eu.ecodex.connector.controller.service.TransportStateService;
import eu.ecodex.connector.domain.model.DomibusConnectorLinkPartner;
import eu.ecodex.connector.persistence.model.PDomibusConnectorTransportStep;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * This interface provides data access methods for the DomibusConnectorTransportStep entity.
 */
@Repository
public interface DomibusConnectorTransportStepDao
    extends JpaRepository<PDomibusConnectorTransportStep, Long> {
    @Query(
        "SELECT MAX(step.attempt) FROM PDomibusConnectorTransportStep step "
            + "WHERE step.connectorMessageId = ?1 AND step.linkPartnerName = ?2"
    )
    Optional<Integer> getHighestAttemptBy(
        String messageId, DomibusConnectorLinkPartner.LinkPartnerName linkPartnerName);

    @Query(
        "SELECT step FROM PDomibusConnectorTransportStep step "
            + "WHERE step.connectorMessageId = ?1 AND step.linkPartnerName = ?2 "
            + "AND step.attempt = ?3"
    )
    Optional<PDomibusConnectorTransportStep> findbyMsgLinkPartnerAndAttempt(
        String msgId, DomibusConnectorLinkPartner.LinkPartnerName partnerName, int attempt);

    @Query("SELECT step FROM PDomibusConnectorTransportStep step WHERE step.transportId = ?1")
    Optional<PDomibusConnectorTransportStep> findByTransportId(
        TransportStateService.TransportId transportId);

    @Query(
        "SELECT status.transportStep "
            + "FROM PDomibusConnectorTransportStepStatusUpdate status "
            + "WHERE status.transportStep.finalStateReached IS NULL "
            + "AND status.transportStep.linkPartnerName = ?1 AND status.transportStateString = ?2 "
    )
    List<PDomibusConnectorTransportStep> findByMsgLinkPartnerAndLastStateIs(
        DomibusConnectorLinkPartner.LinkPartnerName linkPartnerName, String stateDbName);

    @Query(
        "SELECT step FROM PDomibusConnectorTransportStep step WHERE "
            + "step.linkPartnerName IN ?2 AND "
            + "step.id IN ( "
            + "   SELECT status.transportStep.id "
            + "           FROM PDomibusConnectorTransportStepStatusUpdate status "
            + "               WHERE status.transportStateString IN ?1 "
            + "               AND CONCAT(status.created, '_', status.transportStep.id) IN ("
            + "                   SELECT CONCAT(MAX(s2.created), '_', s2.transportStep.id) "
            + "                   FROM PDomibusConnectorTransportStepStatusUpdate s2"
            + "                   GROUP BY s2.transportStep.id) "
            + "                   AND CONCAT(status.transportStep.attempt, '_', "
            + "                         status.transportStep.connectorMessageId) IN ("
            + "                             SELECT CONCAT(MAX(s3.transportStep.attempt), '_', "
            + "                                 s3.transportStep.connectorMessageId) "
            + "                             FROM PDomibusConnectorTransportStepStatusUpdate s3 "
            + "                             GROUP BY s3.transportStep.connectorMessageId)) "
    )
    Page<PDomibusConnectorTransportStep> findLastAttemptStepByLastStateAndLinkPartnerIsOneOf(
        String[] states, DomibusConnectorLinkPartner.LinkPartnerName[] linkPartnerStrings,
        Pageable pageable);

    @Query(
        "SELECT step.linkPartnerName FROM PDomibusConnectorTransportStep step "
            + "GROUP BY step.linkPartnerName"
    )
    List<DomibusConnectorLinkPartner.LinkPartnerName> findAllLinkPartnerNames();

    @Query(
        "SELECT step FROM PDomibusConnectorTransportStep step WHERE step.connectorMessageId = ?1"
    )
    List<PDomibusConnectorTransportStep> findByConnectorMessageId(String connectorMessageId);
}
