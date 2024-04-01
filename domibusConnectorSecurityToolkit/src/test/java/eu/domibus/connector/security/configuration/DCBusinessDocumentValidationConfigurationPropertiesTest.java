package eu.domibus.connector.security.configuration;

import eu.domibus.connector.common.configuration.ConnectorConfigurationProperties;
import eu.domibus.connector.dss.configuration.SignatureValidationConfigurationProperties;
import eu.domibus.connector.utils.service.BeanToPropertyMapConverter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(classes = DCBusinessDocumentValidationConfigurationPropertiesTest.TestContext.class)
@ActiveProfiles({"test", "seclib-test"})
class DCBusinessDocumentValidationConfigurationPropertiesTest {
    @Autowired
    BeanToPropertyMapConverter beanToPropertyMapConverter;

    @Test
    void testBusinesssDocumentValidationToBeanMap() {
        DCBusinessDocumentValidationConfigurationProperties conf =
                new DCBusinessDocumentValidationConfigurationProperties();
        conf.setSignatureValidation(new SignatureValidationConfigurationProperties());
        conf.getSignatureValidation().setCrlEnabled(false);
        conf.getSignatureValidation().setOcspEnabled(false);

        Map<String, String> stringStringMap =
                beanToPropertyMapConverter.readBeanPropertiesToMap(conf, "connector.business-document-sent");

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
        expected.put("connector.business-document-sent.allow-system-type-override-by-client", "true");
        expected.put("connector.business-document-sent.signature-validation.ocsp-enabled", "false");
        expected.put("connector.business-document-sent.signature-validation.crl-enabled", "false");
        expected.put("connector.business-document-sent.signature-validation.trust-store-enabled", "true");
        expected.put("connector.business-document-sent.signature-validation.ignore-store-enabled", "false");
        expected.put("connector.business-document-sent.signature-validation.aia-enabled", "true");
        expected.put("connector.business-document-sent.signature-validation.certificate-verifier-name", "default");

        assertThat(stringStringMap).containsExactlyInAnyOrderEntriesOf(expected);
    }

    @SpringBootApplication(scanBasePackages = "eu.domibus.connector.utils")
    @EnableConfigurationProperties(ConnectorConfigurationProperties.class)
    public static class TestContext {

    }
}
