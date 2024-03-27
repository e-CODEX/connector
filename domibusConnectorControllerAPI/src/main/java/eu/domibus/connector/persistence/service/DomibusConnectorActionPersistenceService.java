package eu.domibus.connector.persistence.service;

import eu.domibus.connector.domain.model.DomibusConnectorAction;

import java.util.List;


/**
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 * @deprecated use DomibusConnectorPModeService instead and also make use of default DomibusConnectorMessageLane
 */
@Deprecated
public interface DomibusConnectorActionPersistenceService {
    DomibusConnectorAction persistNewAction(DomibusConnectorAction service);

    List<DomibusConnectorAction> getActionList();

    DomibusConnectorAction updateAction(DomibusConnectorAction oldAction, DomibusConnectorAction newAction);

    void deleteAction(DomibusConnectorAction deleteAction);

    DomibusConnectorAction getAction(String action);

    DomibusConnectorAction getRelayREMMDAcceptanceRejectionAction();

    DomibusConnectorAction getRelayREMMDFailure();

    DomibusConnectorAction getDeliveryNonDeliveryToRecipientAction();

    DomibusConnectorAction getRetrievalNonRetrievalToRecipientAction();

    List<String> getActionListString();
}
