package eu.domibus.connector.persistence.largefiles.provider;

import eu.domibus.connector.persistence.dao.CommonPersistenceTest;
import org.junit.jupiter.api.BeforeEach;

import static org.assertj.core.api.Assertions.assertThat;

@CommonPersistenceTest
public class LargeFilePersistenceServiceJpaImplITCase extends CommonLargeFilePersistenceProviderITCase {


    @Override
    protected String getProviderName() {
        return LargeFilePersistenceServiceJpaImpl.PROVIDER_NAME;
    }
}