package eu.domibus.connector.web.service;

import eu.domibus.connector.common.service.ConfigurationPropertyManagerService;
import eu.domibus.connector.common.service.CurrentBusinessDomain;
import eu.domibus.connector.controller.service.SubmitToLinkService;
import eu.domibus.connector.domain.model.DomibusConnectorBusinessDomain;
import eu.domibus.connector.domain.model.DomibusConnectorKeystore;
import eu.domibus.connector.evidences.spring.EvidencesToolkitConfigurationProperties;
import eu.domibus.connector.persistence.spring.DatabaseResourceLoader;
import eu.domibus.connector.security.configuration.DCEcodexContainerProperties;
import eu.domibus.connector.ui.service.WebPModeService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(
        properties = {
                "spring.liquibase.change-log=classpath:/db/changelog/install.xml",
                "spring.liquibase.enabled=true"
        }
)
@ActiveProfiles({"test"})
@Disabled("Fails at local build")
class WebPModeServiceTest {
    @MockBean
    SubmitToLinkService submitToLinkService;
    @Autowired
    EvidencesToolkitConfigurationProperties evidencesToolkitConfigurationProperties;
    @Autowired
    DCEcodexContainerProperties dcEcodexContainerProperties;
    @Autowired
    ConfigurationPropertyManagerService configManager;
    @Autowired
    WebPModeService webPModeService;

    //    @AfterEach
    public void resetCurrentDomain() {
        CurrentBusinessDomain.setCurrentBusinessDomain(null);
    }
    @Test
    void importPModes() throws IOException {
        //        Assertions.assertTimeoutPreemptively(Duration.ofSeconds(20), () -> {
        try {
            Resource resource = new ClassPathResource("pmodes/example-pmodes-1.xml");
            byte[] pMode = StreamUtils.copyToByteArray(resource.getInputStream());

            byte[] keyStoreBytes = "Hello World".getBytes(StandardCharsets.UTF_8);

            DomibusConnectorKeystore keystore = webPModeService.importConnectorstore(
                    keyStoreBytes,
                    "pw",
                    DomibusConnectorKeystore.KeystoreType.JKS
            );
            webPModeService.importPModes(pMode, "description", keystore);

            assertThat(webPModeService.getPartyList())
                    .as("example pmodes contains 24 parties")
                    .hasSize(24);

            CurrentBusinessDomain.setCurrentBusinessDomain(DomibusConnectorBusinessDomain.getDefaultMessageLaneId());

            DCEcodexContainerProperties securityToolkitConfigurationProperties = configManager.loadConfiguration(
                    DomibusConnectorBusinessDomain.getDefaultMessageLaneId(),
                    DCEcodexContainerProperties.class
            );
            assertThat(securityToolkitConfigurationProperties.getSignatureValidation().getTrustStore()
                                                             .getPassword()).isEqualTo("pw");

            Assertions.assertAll(
                    () -> assertThat(evidencesToolkitConfigurationProperties.getIssuerInfo().getAs4Party()
                                                                            .getEndpointAddress())
                            .isEqualTo("https://ctpo.example.com/domibus/services/msh"),
                    () -> assertThat(evidencesToolkitConfigurationProperties.getIssuerInfo().getAs4Party().getName())
                            .isEqualTo("service_ctp"),
                    // TODO: check why this is not working...
                    () -> assertThat(dcEcodexContainerProperties.getSignatureValidation().getTrustStore().getPassword())
                            .isEqualTo("pw"),
                    () -> assertThat(this.dcEcodexContainerProperties.getSignatureValidation().getTrustStore()
                                                                     .getPath())
                            .isEqualTo(DatabaseResourceLoader.DB_URL_PREFIX + keystore.getUuid())
            );

            // TODO: check key store config...

            //        assertThat(securityToolkitConfigurationProperties.getTruststore().get)

        } finally {
            CurrentBusinessDomain.setCurrentBusinessDomain(null);
        }
        //        });
    }

    @Test
    void importPModesSet2() throws IOException {
        assertThat(webPModeService).isNotNull();

        Resource resource = new ClassPathResource("pmodes/example-pmodes-2.xml");
        byte[] pMode = StreamUtils.copyToByteArray(resource.getInputStream());

        DomibusConnectorKeystore keystore =
                webPModeService.importConnectorstore(new byte[0], "pw", DomibusConnectorKeystore.KeystoreType.JKS);
        webPModeService.importPModes(pMode, "description", keystore);

        assertThat(webPModeService.getPartyList())
                .as("example pmodes contains 88 parties")
                .hasSize(88);

        // TODO: also check party attributes within DB!
    }

    @Test
    void importPModesTwice() throws IOException {
        assertThat(webPModeService).isNotNull();

        Resource resource = new ClassPathResource("pmodes/example-pmodes-1.xml");
        byte[] pMode = StreamUtils.copyToByteArray(resource.getInputStream());

        DomibusConnectorKeystore keystore =
                webPModeService.importConnectorstore(new byte[0], "pw", DomibusConnectorKeystore.KeystoreType.JKS);
        webPModeService.importPModes(pMode, "description", keystore);

        webPModeService.importPModes(pMode, "description", keystore);

        assertThat(webPModeService.getPartyList())
                .as("example pmodes contains 24 parties")
                .hasSize(24);
    }

    @SpringBootApplication(
            scanBasePackages = {"eu.domibus.connector"}
    )
    public static class TestContext {
    }
}
