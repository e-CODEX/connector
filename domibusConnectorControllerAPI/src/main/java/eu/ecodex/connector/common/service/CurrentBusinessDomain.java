/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.common.service;

import eu.ecodex.connector.domain.model.DomibusConnectorBusinessDomain;
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
