/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.persistence.service;

import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.model.DomibusConnectorMessageConfirmation;
import eu.domibus.connector.domain.model.DomibusConnectorMessageId;

/**
 * The DomibusConnectorEvidencePersistenceService interface provides methods to handle evidence
 * persistence for DomibusConnectorMessage objects.
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
public interface DomibusConnectorEvidencePersistenceService {
    /**
     * Sets the confirmation as transported to gateway the confirmation must have already been
     * persisted to the database.
     *
     * @param confirmation the confirmation, the dbEvidenceId must not be null!
     */
    void setConfirmationAsTransportedToGateway(DomibusConnectorMessageConfirmation confirmation);

    /**
     * Sets the confirmation as transported to backend the confirmation must have already been
     * persisted to the database.
     *
     * @param confirmation the confirmation, the dbEvidenceId must not be null!
     */
    void setConfirmationAsTransportedToBackend(DomibusConnectorMessageConfirmation confirmation);

    /**
     * Persist the confirmation to the business message.
     *
     * @param businessMessage - the business Message the confirmation should be associated with
     * @param transportId     - the transportId used to transport the confirmation
     * @param confirmation    - the confirmation, within the confirmation the databaseId property
     *                        will be updated with the value from DB
     */
    void persistEvidenceMessageToBusinessMessage(DomibusConnectorMessage businessMessage,
                                                 DomibusConnectorMessageId transportId,
                                                 DomibusConnectorMessageConfirmation confirmation);
}
