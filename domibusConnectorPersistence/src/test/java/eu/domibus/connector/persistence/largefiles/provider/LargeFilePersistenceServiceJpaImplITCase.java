package eu.domibus.connector.persistence.largefiles.provider;

import eu.domibus.connector.persistence.dao.CommonPersistenceTest;


@CommonPersistenceTest
public class LargeFilePersistenceServiceJpaImplITCase extends CommonLargeFilePersistenceProviderITCase {
    @Override
    protected String getProviderName() {
        return LargeFilePersistenceServiceJpaImpl.PROVIDER_NAME;
    }
}
