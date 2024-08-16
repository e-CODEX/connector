/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.domain.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The DomibusConnectorPModeSet class represents a set of PMode configurations for a Domibus
 * Connector. It includes information about the business domain, description, create date, PMode
 * data, parties, actions, services, connector keystore, and home party.
 *
 * <p>This class has the following properties:
 * - businessDomainId: The business domain identifier of the PMode set. - description: A description
 * of the PMode set. - createDate: The date when the PMode set was created. - pModes: The PMode data
 * as a byte array. - parties: A list of parties configured in the PMode set. - actions: A list of
 * actions configured in the PMode set. - services: A list of services configured in the PMode set.
 * - connectorstore: The connector keystore used by the PMode set. - homeParty: The home party
 * configured in the PMode set.
 */
@SuppressWarnings("checkstyle:MemberName")
@Data
@NoArgsConstructor
public class DomibusConnectorPModeSet {
    private DomibusConnectorBusinessDomain.BusinessDomainId businessDomainId;
    private String description;
    private Date createDate;
    private byte[] pModes;
    private List<DomibusConnectorParty> parties = new ArrayList<>();
    private List<DomibusConnectorAction> actions = new ArrayList<>();
    private List<DomibusConnectorService> services = new ArrayList<>();
    private DomibusConnectorKeystore connectorstore;
    private DomibusConnectorParty homeParty;

    public DomibusConnectorBusinessDomain.BusinessDomainId getMessageLaneId() {
        return businessDomainId;
    }

    public void setMessageLaneId(DomibusConnectorBusinessDomain.BusinessDomainId businessDomainId) {
        this.businessDomainId = businessDomainId;
    }

    public byte[] getpModes() {
        return pModes;
    }

    @SuppressWarnings("checkstyle:ParameterName")
    public void setpModes(byte[] pModes) {
        this.pModes = pModes;
    }
}
