package eu.domibus.connector.persistence.service.impl;

import eu.domibus.connector.domain.model.DomibusConnectorAction;
import eu.domibus.connector.persistence.dao.DomibusConnectorActionDao;
import eu.domibus.connector.persistence.model.PDomibusConnectorAction;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Service
public class DomibusConnectorActionPersistenceServiceImpl implements eu.domibus.connector.persistence.service.DomibusConnectorActionPersistenceService {

    static final String RETRIEVAL_NON_RETRIEVAL_TO_RECIPIENT_ACTION = "RetrievalNonRetrievalToRecipient";
    static final String DELIVERY_NON_DELIVERY_TO_RECIPIENT_ACTION = "DeliveryNonDeliveryToRecipient";
    static final String RELAY_REMMD_FAILURE_ACTION = "RelayREMMDFailure";
    static final String RELAY_REMMD_ACCEPTANCE_REJECTION_ACTION = "RelayREMMDAcceptanceRejection";

    DomibusConnectorActionDao actionDao;

    @Autowired
    public void setActionDao(DomibusConnectorActionDao actionDao) {
        this.actionDao = actionDao;
    }

    @Override
    public eu.domibus.connector.domain.model.DomibusConnectorAction getRelayREMMDAcceptanceRejectionAction() {
        return getAction(DomibusConnectorActionPersistenceServiceImpl.RELAY_REMMD_ACCEPTANCE_REJECTION_ACTION);
    }

    @Override
    public eu.domibus.connector.domain.model.DomibusConnectorAction getRelayREMMDFailure() {
        return getAction(DomibusConnectorActionPersistenceServiceImpl.RELAY_REMMD_FAILURE_ACTION);
    }

    @Override
    public eu.domibus.connector.domain.model.DomibusConnectorAction getDeliveryNonDeliveryToRecipientAction() {
        return getAction(DomibusConnectorActionPersistenceServiceImpl.DELIVERY_NON_DELIVERY_TO_RECIPIENT_ACTION);
    }

    @Override
    public eu.domibus.connector.domain.model.DomibusConnectorAction getRetrievalNonRetrievalToRecipientAction() {
        return getAction(DomibusConnectorActionPersistenceServiceImpl.RETRIEVAL_NON_RETRIEVAL_TO_RECIPIENT_ACTION);
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
    public List<String> getActionListString(){
    	List<String> actions = new ArrayList<>();
    	for (PDomibusConnectorAction dbAction : this.actionDao.findAll()) {
            actions.add(dbAction.getAction());
        }
        return actions;
    }

    @Override
    public DomibusConnectorAction updateAction(DomibusConnectorAction oldAction, DomibusConnectorAction newAction) {
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
//        PDomibusConnectorAction findOne = actionDao.findById(action).get();
//        return ActionMapper.mapActionToDomain(findOne);
        return null;
    }


}
