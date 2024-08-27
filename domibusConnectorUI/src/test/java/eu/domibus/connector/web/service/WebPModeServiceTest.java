package eu.domibus.connector.web.service;

import static org.assertj.core.api.Assertions.assertThat;

import eu.domibus.connector.common.service.ConfigurationPropertyManagerService;
import eu.domibus.connector.common.service.CurrentBusinessDomain;
import eu.domibus.connector.domain.model.DomibusConnectorBusinessDomain;
import eu.domibus.connector.domain.model.DomibusConnectorKeystore;
import eu.domibus.connector.evidences.spring.EvidencesToolkitConfigurationProperties;
import eu.domibus.connector.persistence.spring.DatabaseResourceLoader;
import eu.domibus.connector.security.configuration.DCEcodexContainerProperties;
import eu.domibus.connector.ui.service.WebPModeService;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.StreamUtils;

@SpringBootTest(
    properties = {
        "spring.liquibase.change-log=classpath:/db/changelog/install.xml",
        "spring.liquibase.enabled=true"
    }
)
@ActiveProfiles({"test"})
@Disabled("Fails at local build")
@SuppressWarnings("squid:S1135")
class WebPModeServiceTest {
    @SpringBootApplication(
        scanBasePackages = {"eu.domibus.connector"}
    )
    public static class TestContext {
    }

    @Autowired
    EvidencesToolkitConfigurationProperties evidencesToolkitConfigurationProperties;
    @Autowired
    DCEcodexContainerProperties dcEcodexContainerProperties;
    @Autowired
    ConfigurationPropertyManagerService configManager;
    @Autowired
    WebPModeService webPModeService;

    // @AfterEach
    public void resetCurrentDomain() {
        CurrentBusinessDomain.setCurrentBusinessDomain(null);
    }

    @Test
    void importPModes() throws IOException {
        try {
            var resource = new ClassPathResource("pmodes/example-pmodes-1.xml");
            var connectorPMode = StreamUtils.copyToByteArray(resource.getInputStream());
            var keyStoreBytes = "Hello World".getBytes(StandardCharsets.UTF_8);

            var keystore = webPModeService.importConnectorstore(
                keyStoreBytes,
                "pw",
                DomibusConnectorKeystore.KeystoreType.JKS
            );
            webPModeService.importPModes(connectorPMode, "description", keystore);

            assertThat(webPModeService.getPartyList())
                .as("example pmodes contains 24 parties")
                .hasSize(24);

            CurrentBusinessDomain.setCurrentBusinessDomain(
                DomibusConnectorBusinessDomain.getDefaultMessageLaneId()
            );

            var securityToolkitConfigurationProperties = configManager.loadConfiguration(
                DomibusConnectorBusinessDomain.getDefaultMessageLaneId(),
                DCEcodexContainerProperties.class
            );
            assertThat(
                securityToolkitConfigurationProperties.getSignatureValidation().getTrustStore()
                                                      .getPassword()
            ).isEqualTo("pw");

            Assertions.assertAll(
                () -> assertThat(
                    evidencesToolkitConfigurationProperties.getIssuerInfo().getAs4Party()
                                                           .getEndpointAddress())
                    .isEqualTo("https://ctpo.example.com/domibus/services/msh"),
                () -> assertThat(
                    evidencesToolkitConfigurationProperties.getIssuerInfo().getAs4Party().getName())
                    .isEqualTo("service_ctp"),
                // TODO: check why this is not working...
                () -> assertThat(
                    dcEcodexContainerProperties.getSignatureValidation().getTrustStore()
                                               .getPassword())
                    .isEqualTo("pw"),
                () -> assertThat(
                    this.dcEcodexContainerProperties.getSignatureValidation().getTrustStore()
                                                    .getPath())
                    .isEqualTo(DatabaseResourceLoader.DB_URL_PREFIX + keystore.getUuid())
            );

            // TODO: check key store config...
            //  assertThat(securityToolkitConfigurationProperties.getTruststore().get)

        } finally {
            CurrentBusinessDomain.setCurrentBusinessDomain(null);
        }
    }

    @Test
    void importPModesSet2() throws IOException {
        assertThat(webPModeService).isNotNull();

        var resource = new ClassPathResource("pmodes/example-pmodes-2.xml");
        var connectorPMode = StreamUtils.copyToByteArray(resource.getInputStream());

        var keystore = webPModeService.importConnectorstore(
            new byte[0],
            "pw",
            DomibusConnectorKeystore.KeystoreType.JKS
        );
        webPModeService.importPModes(connectorPMode, "description", keystore);

        assertThat(webPModeService.getPartyList())
            .as("example pmodes contains 88 parties")
            .hasSize(88);

        // TODO: also check party attributes within DB!
    }

    @Test
    void importPModesTwice() throws IOException {
        assertThat(webPModeService).isNotNull();

        var resource = new ClassPathResource("pmodes/example-pmodes-1.xml");
        var connectorPMode = StreamUtils.copyToByteArray(resource.getInputStream());

        var keystore = webPModeService.importConnectorstore(
            new byte[0],
            "pw",
            DomibusConnectorKeystore.KeystoreType.JKS
        );
        webPModeService.importPModes(connectorPMode, "description", keystore);

        webPModeService.importPModes(connectorPMode, "description", keystore);

        assertThat(webPModeService.getPartyList())
            .as("example pmodes contains 24 parties")
            .hasSize(24);
    }
}
