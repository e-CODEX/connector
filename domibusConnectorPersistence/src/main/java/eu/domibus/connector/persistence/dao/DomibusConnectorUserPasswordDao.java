package eu.domibus.connector.persistence.dao;

import eu.domibus.connector.persistence.model.PDomibusConnectorUser;
import eu.domibus.connector.persistence.model.PDomibusConnectorUserPassword;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface DomibusConnectorUserPasswordDao extends CrudRepository<PDomibusConnectorUserPassword, Long> {
    @Query("SELECT p FROM PDomibusConnectorUserPassword p WHERE p.user=?1 AND p.currentPassword=true")
    PDomibusConnectorUserPassword findCurrentByUser(PDomibusConnectorUser user);
}
