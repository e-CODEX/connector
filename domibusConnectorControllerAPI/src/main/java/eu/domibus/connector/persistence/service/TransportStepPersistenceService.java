package eu.domibus.connector.persistence.service;

import eu.domibus.connector.controller.service.TransportStateService;
import eu.domibus.connector.domain.enums.TransportState;
import eu.domibus.connector.domain.model.DomibusConnectorLinkPartner;
import eu.domibus.connector.domain.model.DomibusConnectorMessageId;
import eu.domibus.connector.domain.model.DomibusConnectorTransportStep;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;


public interface TransportStepPersistenceService {
    DomibusConnectorTransportStep createNewTransportStep(DomibusConnectorTransportStep transportStep);

    DomibusConnectorTransportStep getTransportStepByTransportId(TransportStateService.TransportId connectorTransportId);

    DomibusConnectorTransportStep update(DomibusConnectorTransportStep transportStep);

    List<DomibusConnectorTransportStep> findPendingStepBy(DomibusConnectorLinkPartner.LinkPartnerName linkPartnerName);

    Optional<DomibusConnectorTransportStep> findStepById(TransportStateService.TransportId transportId);

    /**
     * Returns a list of transport states, where the last state is one of
     * the provided states within the list
     * For multiple transport steps only the step with the highest attempt is returned,
     * lower attempts are omitted!
     *
     * @param states:   the states which have be included in the search
     * @param pageable: the paging parameters
     * @return the Page
     */
    Page<DomibusConnectorTransportStep> findLastAttemptStepByLastStateIsOneOf(
            Set<TransportState> states,
            Set<DomibusConnectorLinkPartner.LinkPartnerName> linkPartnerNames,
            Pageable pageable);

    List<DomibusConnectorTransportStep> findStepByConnectorMessageId(DomibusConnectorMessageId messageId);

    /**
     * @return a list of all LinkPartnerNames used in the transport
     */
    List<DomibusConnectorLinkPartner.LinkPartnerName> findAllLinkPartners();
}
