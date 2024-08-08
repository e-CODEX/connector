package eu.domibus.connector.persistence.largefiles.provider;

import eu.domibus.connector.persistence.dao.CommonPersistenceTest;

/**
 * Integration test case for the LargeFilePersistenceServiceJpaImpl class.
 */
@CommonPersistenceTest
public class LargeFilePersistenceServiceJpaImplITCase
    extends CommonLargeFilePersistenceProviderITCase {
    @Override
    protected String getProviderName() {
        return LargeFilePersistenceServiceJpaImpl.PROVIDER_NAME;
    }
}
