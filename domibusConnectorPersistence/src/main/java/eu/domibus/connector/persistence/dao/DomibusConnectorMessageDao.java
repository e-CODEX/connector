package eu.domibus.connector.persistence.dao;

import eu.domibus.connector.domain.enums.MessageTargetSource;
import eu.domibus.connector.persistence.model.PDomibusConnectorMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
@Repository
public interface DomibusConnectorMessageDao extends JpaRepository<PDomibusConnectorMessage, Long> {
	
	public List<PDomibusConnectorMessage> findAllByOrderByCreatedDesc();

    public List<PDomibusConnectorMessage> findByBackendMessageId(String backendId);

    public Optional<PDomibusConnectorMessage> findOneByBackendMessageIdAndDirectionTarget(String backendId, MessageTargetSource directionTarget);
    
    public List<PDomibusConnectorMessage> findByEbmsMessageId(String ebmsMessageId);

    public Optional<PDomibusConnectorMessage> findOneByEbmsMessageIdAndDirectionTarget(String ebmsMessageId, MessageTargetSource directionTarget);

    @Query("SELECT m FROM PDomibusConnectorMessage m WHERE (m.ebmsMessageId = ?1 OR m.backendMessageId = ?1) AND m.directionTarget = ?2 ")
    public Optional<PDomibusConnectorMessage> findOneByEbmsMessageIdOrBackendMessageIdAndDirectionTarget(String id, MessageTargetSource directionTarget);
    
    public Optional<PDomibusConnectorMessage> findOneByConnectorMessageId(String messageConnectorId);

    public List<PDomibusConnectorMessage> findByConversationId(String conversationId);
    
    public List<PDomibusConnectorMessage> findByBackendName(String backendName);
    
    @Query("SELECT m FROM PDomibusConnectorMessage m WHERE m.confirmed is null AND m.rejected is null AND m.directionTarget = 'GATEWAY' AND m.deliveredToGateway is not null ")
    public List<PDomibusConnectorMessage> findOutgoingUnconfirmedMessages();
        
    @Query("SELECT m FROM PDomibusConnectorMessage m WHERE m.confirmed is null AND m.rejected is null AND m.directionTarget = 'GATEWAY' AND m.deliveredToGateway is not null "
        + "AND not exists (SELECT 1 FROM PDomibusConnectorEvidence e WHERE e.businessMessage = m AND (e.type='DELIVERY' or e.type='NON_DELIVERY'))")
    public List<PDomibusConnectorMessage> findOutgoingMessagesNotRejectedNorConfirmedAndWithoutDelivery();
    
    @Query("SELECT m FROM PDomibusConnectorMessage m WHERE m.confirmed is null AND m.rejected is null AND m.directionTarget = 'GATEWAY' AND m.deliveredToGateway is not null "
        + "AND not exists (SELECT 1 FROM PDomibusConnectorEvidence e WHERE e.businessMessage = m AND (e.type='RELAY_REMMD_ACCEPTANCE' or e.type='RELAY_REMMD_REJECTION'))")
    public List<PDomibusConnectorMessage> findOutgoingMessagesNotRejectedNorConfirmedAndWithoutRelayREMMD();
        
    @Query("SELECT m FROM PDomibusConnectorMessage m WHERE m.confirmed is null AND m.rejected is null AND m.directionTarget = 'BACKEND' AND m.deliveredToGateway is not null")
    public List<PDomibusConnectorMessage> findIncomingUnconfirmedMessages();
    
    @Query("SELECT m FROM PDomibusConnectorMessage m WHERE (m.deliveredToNationalSystem is not null AND m.deliveredToNationalSystem between ?1 and ?2) "
    		+ "OR (m.deliveredToGateway is not null AND m.deliveredToGateway between ?1 and ?2) "
    		+ "OR (m.created is not null AND m.created between ?1 and ?2)")
    public List<PDomibusConnectorMessage> findByPeriod(Date from, Date to);
        
    // if DB fields confirmed OR rejected are NOT NULL -> then true
    @Query("SELECT case when (count(m) > 0) then true else false end "
            + "FROM PDomibusConnectorMessage m "
            + "WHERE m.id = ?1 AND (m.confirmed is not null OR m.rejected is not null)")
    public boolean checkMessageConfirmedOrRejected(Long messageId);

    // if DB fields confirmed OR rejected are NOT NULL -> then true
    @Query("SELECT case when (count(m) > 0) then true else false end "
            + "FROM PDomibusConnectorMessage m "
            + "WHERE m.connectorMessageId = ?1 AND (m.confirmed is not null OR m.rejected is not null)")
    public boolean checkMessageConfirmedOrRejected(String messageId);

    // if DB field rejected is NOT NULL -> then true
    @Query("SELECT case when (count(m) > 0) then true else false end FROM PDomibusConnectorMessage m WHERE m.id = ?1 AND m.rejected is not null")
    public boolean checkMessageRejected(Long messageId);     
    
    // if DB field confirmend is NOT NULL -> then true
    @Query("SELECT case when (count(m) > 0)  then true else false end FROM PDomibusConnectorMessage m WHERE m.id = ?1 AND m.confirmed is not null")
    public boolean checkMessageConfirmed(Long messageId);
    
    @Modifying
    @Query("update PDomibusConnectorMessage m set confirmed=CURRENT_TIMESTAMP, rejected=NULL WHERE m.id = ?1")
    public int confirmMessage(Long messageId);

    @Modifying
    @Query("update PDomibusConnectorMessage m set confirmed=?2, rejected=NULL WHERE m.id = ?1")
    public int confirmMessage(Long messageId, ZonedDateTime d);
    
    @Modifying
    @Query("update PDomibusConnectorMessage m set rejected=CURRENT_TIMESTAMP, confirmed=NULL WHERE m.id = ?1")
    public int rejectMessage(Long messageId);

    @Modifying
    @Query("update PDomibusConnectorMessage m set rejected=?2, confirmed=NULL WHERE m.id = ?1")
    public int rejectMessage(Long messageId, ZonedDateTime d);


    @Modifying
    @Query("update PDomibusConnectorMessage m set m.deliveredToGateway=CURRENT_TIMESTAMP WHERE m = ?1")
    public int setMessageDeliveredToGateway(PDomibusConnectorMessage dbMessage);

    @Modifying
    @Query("update PDomibusConnectorMessage m set m.deliveredToGateway=CURRENT_TIMESTAMP WHERE m.connectorMessageId = ?1")
    public int setMessageDeliveredToGateway(String connectorId);
    
    @Modifying
    @Query("update PDomibusConnectorMessage m set m.deliveredToNationalSystem=CURRENT_TIMESTAMP WHERE m = ?1")
    public int setMessageDeliveredToBackend(PDomibusConnectorMessage dbMessage);

    @Modifying
    @Query("update PDomibusConnectorMessage m set m.deliveredToNationalSystem=CURRENT_TIMESTAMP WHERE m.connectorMessageId = ?1")
    public int setMessageDeliveredToBackend(String connectorId);

    @Modifying
    @Query("update PDomibusConnectorMessage m set m.deliveredToGateway=?2 WHERE m.connectorMessageId = ?1")
    void setMessageDeliveredToGateway(String connectorid, Date deliveryDate);

}
