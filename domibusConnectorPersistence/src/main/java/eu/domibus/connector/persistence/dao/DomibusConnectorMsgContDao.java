/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.persistence.dao;

import eu.domibus.connector.persistence.model.PDomibusConnectorMsgCont;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * The DomibusConnectorMsgContDao interface is responsible for the persistence operations related to
 * DomibusConnectorMsgCont entities. It extends the CrudRepository interface, which provides basic
 * CRUD (Create, Read, Update, Delete) operations.
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
@Repository
public interface DomibusConnectorMsgContDao extends CrudRepository<PDomibusConnectorMsgCont, Long> {
    @Modifying
    @Query("delete PDomibusConnectorMsgCont c where c.connectorMessageId = ?1")
    void deleteByMessage(String messageId);

    @Query("SELECT c FROM PDomibusConnectorMsgCont c where c.connectorMessageId = ?1")
    List<PDomibusConnectorMsgCont> findByMessage(String messageId);
}
