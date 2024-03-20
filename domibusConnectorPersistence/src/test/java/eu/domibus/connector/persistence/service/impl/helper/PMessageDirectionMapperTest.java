package eu.domibus.connector.persistence.service.impl.helper;

import eu.domibus.connector.domain.enums.DomibusConnectorMessageDirection;
import eu.domibus.connector.persistence.model.enums.PMessageDirection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

public class PMessageDirectionMapperTest {


    @Test
    public void mapFromDomainToPersistence() throws Exception {
        for (DomibusConnectorMessageDirection direction : DomibusConnectorMessageDirection.values()) {
            MessageDirectionMapper.mapFromDomainToPersistence(direction);
        }
    }

    @Test
    public void mapFromPersistenceToDomain() throws Exception {
        for (PMessageDirection direction : PMessageDirection.values()) {
            MessageDirectionMapper.mapFromPersistenceToDomain(direction);
        }
    }

}