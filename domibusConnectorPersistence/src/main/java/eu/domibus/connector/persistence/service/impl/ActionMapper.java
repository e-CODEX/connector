/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.persistence.service.impl;

import eu.domibus.connector.domain.model.DomibusConnectorAction;
import eu.domibus.connector.persistence.model.PDomibusConnectorAction;
import javax.annotation.Nullable;
import lombok.experimental.UtilityClass;
import org.springframework.beans.BeanUtils;

/**
 * The ActionMapper class provides static methods to map actions between the persistence layer and
 * the domain layer.
 */
@UtilityClass
public class ActionMapper {
    static @Nullable DomibusConnectorAction mapActionToDomain(
        @Nullable PDomibusConnectorAction persistenceAction) {
        if (persistenceAction != null) {
            var action
                = new eu.domibus.connector.domain.model.DomibusConnectorAction(
                persistenceAction.getAction()
            );
            action.setDbKey(persistenceAction.getId());
            return action;
        }
        return null;
    }

    static @Nullable PDomibusConnectorAction mapActionToPersistence(
        @Nullable DomibusConnectorAction action) {
        if (action != null) {
            var persistenceAction = new PDomibusConnectorAction();
            BeanUtils.copyProperties(action, persistenceAction);
            persistenceAction.setId(action.getDbKey());
            return persistenceAction;
        }
        return null;
    }
}
