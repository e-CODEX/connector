/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.persistence.service;

import eu.domibus.connector.domain.model.DomibusConnectorAction;
import java.util.List;

/**
 * The DomibusConnectorActionPersistenceService interface provides methods for managing
 * DomibusConnectorAction objects, which represent actions associated with messages in the Domibus
 * system.
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 * @deprecated use DomibusConnectorPModeService instead and also make use of default
 *      DomibusConnectorMessageLane.
 */
@Deprecated
public interface DomibusConnectorActionPersistenceService {
    DomibusConnectorAction persistNewAction(DomibusConnectorAction service);

    List<DomibusConnectorAction> getActionList();

    DomibusConnectorAction updateAction(DomibusConnectorAction oldAction,
                                        DomibusConnectorAction newAction);

    void deleteAction(DomibusConnectorAction deleteAction);

    DomibusConnectorAction getAction(String action);

    DomibusConnectorAction getRelayREMMDAcceptanceRejectionAction();

    DomibusConnectorAction getRelayREMMDFailure();

    DomibusConnectorAction getDeliveryNonDeliveryToRecipientAction();

    DomibusConnectorAction getRetrievalNonRetrievalToRecipientAction();

    List<String> getActionListString();
}
