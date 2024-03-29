package eu.domibus.connector.persistence.dao;

import eu.domibus.connector.persistence.model.PDomibusConnectorLinkConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface DomibusConnectorLinkConfigurationDao extends JpaRepository<PDomibusConnectorLinkConfiguration, Long> {
    Optional<PDomibusConnectorLinkConfiguration> getOneByConfigName(String configName);
}
