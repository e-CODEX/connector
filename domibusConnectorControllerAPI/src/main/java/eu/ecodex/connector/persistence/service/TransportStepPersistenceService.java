/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.persistence.service;

import eu.ecodex.connector.controller.service.TransportStateService;
import eu.ecodex.connector.domain.enums.TransportState;
import eu.ecodex.connector.domain.model.DomibusConnectorLinkPartner;
import eu.ecodex.connector.domain.model.DomibusConnectorMessageId;
import eu.ecodex.connector.domain.model.DomibusConnectorTransportStep;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * The TransportStepPersistenceService interface provides methods to interact with the persistence
 * layer for managing transportation steps in the Domibus connector.
 */
public interface TransportStepPersistenceService {
    DomibusConnectorTransportStep createNewTransportStep(
        DomibusConnectorTransportStep transportStep);

    DomibusConnectorTransportStep getTransportStepByTransportId(
        TransportStateService.TransportId connectorTransportId);

    DomibusConnectorTransportStep update(DomibusConnectorTransportStep transportStep);

    List<DomibusConnectorTransportStep> findPendingStepBy(
        DomibusConnectorLinkPartner.LinkPartnerName linkPartnerName);

    Optional<DomibusConnectorTransportStep> findStepById(
        TransportStateService.TransportId transportId);

    /**
     * Returns a list of transport states, where the last state is one of the provided states within
     * the list For multiple transport steps only the step with the highest attempt is returned,
     * lower attempts are omitted.
     *
     * @param states   the states which have be included in the search
     * @param pageable the paging parameters
     * @return the Page
     */
    Page<DomibusConnectorTransportStep> findLastAttemptStepByLastStateIsOneOf(
        Set<TransportState> states,
        Set<DomibusConnectorLinkPartner.LinkPartnerName> linkPartnerNames, Pageable pageable);

    List<DomibusConnectorTransportStep> findStepByConnectorMessageId(
        DomibusConnectorMessageId messageId);

    /**
     * Returns a list of all link partners used in the transport in the Domibus Connector.
     *
     * @return a list of DomibusConnectorLinkPartner.LinkPartnerName objects representing the link
     *      partner names
     */
    List<DomibusConnectorLinkPartner.LinkPartnerName> findAllLinkPartners();
}
