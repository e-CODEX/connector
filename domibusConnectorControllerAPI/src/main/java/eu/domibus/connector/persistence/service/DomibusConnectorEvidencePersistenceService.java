package eu.domibus.connector.persistence.service;

import eu.domibus.connector.domain.enums.DomibusConnectorEvidenceType;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.model.DomibusConnectorMessageConfirmation;
import eu.domibus.connector.domain.model.DomibusConnectorMessageId;

import javax.validation.constraints.NotNull;

/**
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
public interface DomibusConnectorEvidencePersistenceService {

    /**
     * Sets the confirmation as transported to gateway
     *  the confirmation must have already been persisted to the database
     * @param confirmation the confirmation, the dbEvidenceId must not be null!
     */
    void setConfirmationAsTransportedToGateway(DomibusConnectorMessageConfirmation confirmation);


    /**
     * Sets the confirmation as transported to backend
     *  the confirmation must have already been persisted to the database
     * @param confirmation the confirmation, the dbEvidenceId must not be null!
     */
    void setConfirmationAsTransportedToBackend(DomibusConnectorMessageConfirmation confirmation);

    /**
     * Persist the confirmation to the business message
     * @param businessMessage - the business Message the confirmation should be associated with
     * @param transportId - the transportId used to transport the confirmation
     * @param confirmation - the confirmation, within the confirmation the databaseId property will be updated
     *                     with the value from DB
     *
     */
    void persistEvidenceMessageToBusinessMessage(DomibusConnectorMessage businessMessage, DomibusConnectorMessageId transportId, DomibusConnectorMessageConfirmation confirmation);
}
