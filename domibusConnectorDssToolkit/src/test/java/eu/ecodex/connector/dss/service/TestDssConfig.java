/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.dss.service;

import eu.ecodex.connector.common.SpringProfiles;
import eu.ecodex.connector.common.configuration.ConnectorConverterAutoConfiguration;
import eu.ecodex.connector.common.service.DCKeyStoreService;
import eu.ecodex.connector.dss.configuration.BasicDssConfiguration;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.model.TimestampBinary;
import eu.europa.esig.dss.spi.DSSUtils;
import eu.europa.esig.dss.spi.x509.tsp.TSPSource;
import java.io.UnsupportedEncodingException;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

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

        TimestampBinary timeStampResponse =
            tspSource.getTimeStampResponse(digestAlgorithm, digestValue);
        AssertionsForClassTypes.assertThat(timeStampResponse.getBytes()).isNotNull();
    }
}
