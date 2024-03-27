package eu.domibus.connector.common.service;

import eu.domibus.connector.domain.model.DomibusConnectorBusinessDomain;

public class CurrentBusinessDomain {
    private static final ThreadLocal<DomibusConnectorBusinessDomain.BusinessDomainId> currentMessageLaneId = new ThreadLocal<>();

    public static DomibusConnectorBusinessDomain.BusinessDomainId getCurrentBusinessDomain() {
        return currentMessageLaneId.get();
    }

    public static void setCurrentBusinessDomain(DomibusConnectorBusinessDomain.BusinessDomainId businessDomainId) {
        currentMessageLaneId.set(businessDomainId);
    }
}
