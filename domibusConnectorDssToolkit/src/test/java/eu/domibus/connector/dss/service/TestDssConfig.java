package eu.domibus.connector.dss.service;

import eu.domibus.connector.common.SpringProfiles;
import eu.domibus.connector.common.configuration.ConnectorConverterAutoConfiguration;
import eu.domibus.connector.common.service.DCKeyStoreService;
import eu.domibus.connector.dss.configuration.BasicDssConfiguration;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.model.TimestampBinary;
import eu.europa.esig.dss.spi.DSSUtils;
import eu.europa.esig.dss.spi.x509.tsp.TSPSource;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.UnsupportedEncodingException;


@SpringBootTest(
        classes = {BasicDssConfiguration.class,
                DSSTrustedListsManager.class,
                ConnectorConverterAutoConfiguration.class,
                DCKeyStoreService.class
        },
        properties = "connector.dss.tlCacheLocation=file:./target/tlcache/"

)
@ActiveProfiles({"seclib-test", SpringProfiles.TEST, "dss-tl-test"})
@Disabled("Test is failing in local build")
class TestDssConfig {
    @Autowired
    TSPSource tspSource;

    @Test
    void testTspSource() throws UnsupportedEncodingException {
        final DigestAlgorithm digestAlgorithm = DigestAlgorithm.SHA256;
        final byte[] toDigest = "Hello world".getBytes("UTF-8");
        final byte[] digestValue = DSSUtils.digest(digestAlgorithm, toDigest);

        TimestampBinary timeStampResponse = tspSource.getTimeStampResponse(digestAlgorithm, digestValue);
        AssertionsForClassTypes.assertThat(timeStampResponse.getBytes()).isNotNull();
    }
}
