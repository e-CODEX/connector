package eu.domibus.connector.persistence.dao;

import eu.domibus.connector.persistence.model.PDomibusConnectorMsgCont;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
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
