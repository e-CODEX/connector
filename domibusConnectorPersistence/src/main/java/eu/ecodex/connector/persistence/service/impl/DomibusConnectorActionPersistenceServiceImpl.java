/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.persistence.service.impl;

import eu.ecodex.connector.domain.model.DomibusConnectorAction;
import eu.ecodex.connector.persistence.dao.DomibusConnectorActionDao;
import eu.ecodex.connector.persistence.model.PDomibusConnectorAction;
import eu.ecodex.connector.persistence.service.DomibusConnectorActionPersistenceService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation of the DomibusConnectorActionPersistenceService interface that provides methods
 * for managing DomibusConnectorAction objects, which represent actions associated with messages in
 * the Domibus system.
 */
@Service
public class DomibusConnectorActionPersistenceServiceImpl
    implements DomibusConnectorActionPersistenceService {
    static final String RETRIEVAL_NON_RETRIEVAL_TO_RECIPIENT_ACTION =
        "RetrievalNonRetrievalToRecipient";
    static final String DELIVERY_NON_DELIVERY_TO_RECIPIENT_ACTION =
        "DeliveryNonDeliveryToRecipient";
    static final String RELAY_REMMD_FAILURE_ACTION = "RelayREMMDFailure";
    static final String RELAY_REMMD_ACCEPTANCE_REJECTION_ACTION = "RelayREMMDAcceptanceRejection";
    DomibusConnectorActionDao actionDao;

    @Autowired
    public void setActionDao(DomibusConnectorActionDao actionDao) {
        this.actionDao = actionDao;
    }

    @Override
    public DomibusConnectorAction
    getRelayREMMDAcceptanceRejectionAction() {
        return getAction(
            DomibusConnectorActionPersistenceServiceImpl.RELAY_REMMD_ACCEPTANCE_REJECTION_ACTION);
    }

    @Override
    public DomibusConnectorAction getRelayREMMDFailure() {
        return getAction(DomibusConnectorActionPersistenceServiceImpl.RELAY_REMMD_FAILURE_ACTION);
    }

    @Override
    public DomibusConnectorAction
    getDeliveryNonDeliveryToRecipientAction() {
        return getAction(
            DomibusConnectorActionPersistenceServiceImpl.DELIVERY_NON_DELIVERY_TO_RECIPIENT_ACTION);
    }

    @Override
    public DomibusConnectorAction
    getRetrievalNonRetrievalToRecipientAction() {
        return getAction(
            DomibusConnectorActionPersistenceServiceImpl.RETRIEVAL_NON_RETRIEVAL_TO_RECIPIENT_ACTION
        );
    }

    @Override
    public DomibusConnectorAction persistNewAction(DomibusConnectorAction action) {
        PDomibusConnectorAction dbAction = ActionMapper.mapActionToPersistence(action);
        dbAction = this.actionDao.save(dbAction);
        return ActionMapper.mapActionToDomain(dbAction);
    }

    @Override
    public List<DomibusConnectorAction> getActionList() {
        List<DomibusConnectorAction> actions = new ArrayList<>();
        for (PDomibusConnectorAction dbAction : this.actionDao.findAll()) {
            actions.add(ActionMapper.mapActionToDomain(dbAction));
        }
        return actions;
    }

    @Override
    public List<String> getActionListString() {
        List<String> actions = new ArrayList<>();
        for (PDomibusConnectorAction dbAction : this.actionDao.findAll()) {
            actions.add(dbAction.getAction());
        }
        return actions;
    }

    @Override
    public DomibusConnectorAction updateAction(
        DomibusConnectorAction oldAction, DomibusConnectorAction newAction) {
        PDomibusConnectorAction newDbAction = ActionMapper.mapActionToPersistence(newAction);
        newDbAction = this.actionDao.save(newDbAction);
        return ActionMapper.mapActionToDomain(newDbAction);
    }

    @Override
    public void deleteAction(DomibusConnectorAction deleteAction) {
        PDomibusConnectorAction del = ActionMapper.mapActionToPersistence(deleteAction);
        this.actionDao.delete(del);
    }

    @Override
    public DomibusConnectorAction getAction(String action) {
        // PDomibusConnectorAction findOne = actionDao.findById(action).get();
        //  return ActionMapper.mapActionToDomain(findOne);
        return null;
    }
}
