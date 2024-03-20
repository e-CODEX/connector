package eu.domibus.connector.persistence.service.impl;

import eu.domibus.connector.domain.model.DomibusConnectorAction;
import eu.domibus.connector.persistence.model.PDomibusConnectorAction;
import org.springframework.beans.BeanUtils;

import javax.annotation.Nullable;

public class ActionMapper {


    static @Nullable DomibusConnectorAction mapActionToDomain(@Nullable PDomibusConnectorAction persistenceAction) {
        if (persistenceAction != null) {
            eu.domibus.connector.domain.model.DomibusConnectorAction action
                    = new eu.domibus.connector.domain.model.DomibusConnectorAction(
                    persistenceAction.getAction()
//                    persistenceAction.isDocumentRequired()
            );
            action.setDbKey(persistenceAction.getId());
            return action;
        }
        return null;
    }


    static @Nullable PDomibusConnectorAction mapActionToPersistence(@Nullable DomibusConnectorAction action) {
        if (action != null) {
            PDomibusConnectorAction persistenceAction = new PDomibusConnectorAction();
            BeanUtils.copyProperties(action, persistenceAction);
            persistenceAction.setId(action.getDbKey());
            return persistenceAction;
        }
        return null;
    }

}
