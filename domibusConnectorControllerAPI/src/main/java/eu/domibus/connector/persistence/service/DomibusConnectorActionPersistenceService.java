/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
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
