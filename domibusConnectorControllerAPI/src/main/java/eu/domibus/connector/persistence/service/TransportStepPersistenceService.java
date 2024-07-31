/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.persistence.service;

import eu.domibus.connector.controller.service.TransportStateService;
import eu.domibus.connector.domain.enums.TransportState;
import eu.domibus.connector.domain.model.DomibusConnectorLinkPartner;
import eu.domibus.connector.domain.model.DomibusConnectorMessageId;
import eu.domibus.connector.domain.model.DomibusConnectorTransportStep;
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
