/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
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
