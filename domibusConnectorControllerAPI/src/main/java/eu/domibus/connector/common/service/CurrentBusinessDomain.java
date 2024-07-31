/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.common.service;

import eu.domibus.connector.domain.model.DomibusConnectorBusinessDomain;
import lombok.experimental.UtilityClass;

/**
 * The CurrentBusinessDomain class provides methods to get and set the current business domain.
 */
@UtilityClass
public class CurrentBusinessDomain {
    private static final ThreadLocal<DomibusConnectorBusinessDomain.BusinessDomainId>
        currentMessageLaneId = new ThreadLocal<>();

    public static DomibusConnectorBusinessDomain.BusinessDomainId getCurrentBusinessDomain() {
        return currentMessageLaneId.get();
    }

    public static void setCurrentBusinessDomain(
        DomibusConnectorBusinessDomain.BusinessDomainId businessDomainId) {
        currentMessageLaneId.set(businessDomainId);
    }
}
