/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.persistence.model;

import eu.domibus.connector.domain.model.DomibusConnectorParty;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * The PDomibusConnectorParty class represents a persistent object that is stored in the database in
 * the table DOMIBUS_CONNECTOR_PARTY. It is used to store information about a party in the Domibus
 * connector, such as the party's ID, ID type, and role. This class is annotated with JPA
 * annotations to define the mapping to the database table and columns. It also includes getter and
 * setter methods for accessing and modifying the party's properties.
 */
@Getter
@Setter
@Entity
@Table(name = PDomibusConnectorParty.TABLE_NAME)
public class PDomibusConnectorParty {
    public static final String TABLE_NAME = "DOMIBUS_CONNECTOR_PARTY";
    @Id
    @Column(name = "ID")
    @TableGenerator(
        name = "seq" + TABLE_NAME,
        table = PDomibusConnectorPersistenceModel.SEQ_STORE_TABLE_NAME,
        pkColumnName = PDomibusConnectorPersistenceModel.SEQ_NAME_COLUMN_NAME,
        pkColumnValue = TABLE_NAME + ".ID",
        valueColumnName = PDomibusConnectorPersistenceModel.SEQ_VALUE_COLUMN_NAME,
        initialValue = PDomibusConnectorPersistenceModel.INITIAL_VALUE,
        allocationSize = PDomibusConnectorPersistenceModel.ALLOCATION_SIZE
    )
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "seq" + TABLE_NAME)
    private Long id;
    @Column(name = "IDENTIFIER")
    private String pmodePartyIdentifier;
    @Column(name = "PARTY_ID", nullable = false)
    private String partyId;
    @Column(name = "ROLE")
    private String role;
    @Column(name = "PARTY_ID_TYPE", nullable = false, length = 512)
    private String partyIdType;
    @Column(name = "ROLE_TYPE", length = 50)
    //    @Enumerated(EnumType.STRING)
    private DomibusConnectorParty.PartyRoleType roleType;
    @SuppressWarnings("checkstyle:MemberName")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_PMODE_SET", referencedColumnName = "ID")
    private PDomibusConnectorPModeSet pModeSet;

    @SuppressWarnings("checkstyle:ParameterName")
    public void setpModeSet(PDomibusConnectorPModeSet pModeSet) {
        this.pModeSet = pModeSet;
    }

    @Override
    public String toString() {
        var toString = new ToStringBuilder(this);
        toString.append("id", partyId);
        toString.append("role", role);
        toString.append("idType", partyIdType);
        toString.append("roleType", roleType);
        return toString.build();
    }
}
