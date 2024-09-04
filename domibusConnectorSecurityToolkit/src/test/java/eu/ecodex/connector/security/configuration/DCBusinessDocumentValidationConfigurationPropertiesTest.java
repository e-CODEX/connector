/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.security.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import eu.ecodex.connector.common.configuration.ConnectorConfigurationProperties;
import eu.ecodex.connector.dss.configuration.SignatureValidationConfigurationProperties;
import eu.ecodex.connector.utils.service.BeanToPropertyMapConverter;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = DCBusinessDocumentValidationConfigurationPropertiesTest.TestContext.class)
@ActiveProfiles({"test", "seclib-test"})
class DCBusinessDocumentValidationConfigurationPropertiesTest {
    @SpringBootApplication(
        scanBasePackages =
            {"eu.ecodex.connector.utils", "eu.ecodex.connector.common.configuration"}
    )
    @EnableConfigurationProperties(ConnectorConfigurationProperties.class)
    public static class TestContext {
    }

    @Autowired
    BeanToPropertyMapConverter beanToPropertyMapConverter;

    @Test
    void testBusinessDocumentValidationToBeanMap() {
        var conf = new DCBusinessDocumentValidationConfigurationProperties();
        conf.setSignatureValidation(new SignatureValidationConfigurationProperties());
        conf.getSignatureValidation().setCrlEnabled(false);
        conf.getSignatureValidation().setOcspEnabled(false);

        Map<String, String> expected = new HashMap<>();
        expected.put(
            "connector.business-document-sent.allowed-advanced-system-types",
            "SIGNATURE_BASED,AUTHENTICATION_BASED"
        );
        expected.put(
            "connector.business-document-sent.signature-validation.validation-constraints-xml",
            "classpath:/102853/constraint.xml"
        );
        expected.put("connector.business-document-sent.country", "");
        expected.put("connector.business-document-sent.service-provider", "");
        expected.put(
            "connector.business-document-sent.allow-system-type-override-by-client", "true");
        expected.put("connector.business-document-sent.signature-validation.ocsp-enabled", "false");
        expected.put("connector.business-document-sent.signature-validation.crl-enabled", "false");
        expected.put(
            "connector.business-document-sent.signature-validation.trust-store-enabled", "true");
        expected.put(
            "connector.business-document-sent.signature-validation.ignore-store-enabled", "false");
        expected.put("connector.business-document-sent.signature-validation.aia-enabled", "true");
        expected.put(
            "connector.business-document-sent.signature-validation.certificate-verifier-name",
            "default"
        );

        var stringStringMap = beanToPropertyMapConverter.readBeanPropertiesToMap(
            conf,
            "connector.business-document-sent"
        );
        assertThat(stringStringMap).containsExactlyInAnyOrderEntriesOf(expected);
    }
}
