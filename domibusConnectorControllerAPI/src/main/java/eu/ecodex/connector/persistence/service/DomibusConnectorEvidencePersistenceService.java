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

import eu.ecodex.connector.domain.model.DomibusConnectorMessage;
import eu.ecodex.connector.domain.model.DomibusConnectorMessageConfirmation;
import eu.ecodex.connector.domain.model.DomibusConnectorMessageId;

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
