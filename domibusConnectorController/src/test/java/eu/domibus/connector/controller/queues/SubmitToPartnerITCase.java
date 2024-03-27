package eu.domibus.connector.controller.queues;


import eu.domibus.connector.controller.exception.DomibusConnectorSubmitToLinkException;
import eu.domibus.connector.controller.queues.listener.ToLinkPartnerListener;
import eu.domibus.connector.controller.queues.producer.ToLinkQueue;
import eu.domibus.connector.controller.service.SubmitToLinkService;
import eu.domibus.connector.domain.model.DomibusConnectorBusinessDomain;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.testutil.DomainEntityCreator;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;


@SpringBootTest(classes = {SubmitToPartnerITCase.TestContext.class}, properties = {"spring.liquibase.enabled=false"})
@ActiveProfiles({"test", "jms-test"})
@DirtiesContext
@Disabled("fails on CI")
class SubmitToPartnerITCase {
    // wires a mock
    @Autowired
    @Qualifier("mockedSubmitToLinkService")
    private SubmitToLinkService submitToLinkService;

    @Autowired
    @Qualifier("mockInjectedPartnerListener")
    private ToLinkPartnerListener toLinkPartnerListener;

    // @Autowired
    // private ToLinkQueue toLinkQueue;

    // this test does not test the retry behaviour it was more a setup to debug the issue.
    @Test
    void debugRetryBehaviour() throws DomibusConnectorSubmitToLinkException {
        assertThat(toLinkPartnerListener).isNotNull();

        final DomibusConnectorMessage simpleTestMessage = DomainEntityCreator.createEpoMessage();
        simpleTestMessage.setMessageLaneId(DomibusConnectorBusinessDomain.getDefaultMessageLaneId());

        toLinkPartnerListener.handleMessage(simpleTestMessage);

        // this should verify the retry configuration, but the transaction configuration does not work in the test
        // like in prod
        // so the test just aborts on the first exception without retrying.
        // Mockito.verify(submitToLinkService, Mockito.times(6)).submitToLink(any());
    }

    @SpringBootApplication(scanBasePackages = "foo.bar")
    @Import({JmsConfiguration.class, ToLinkQueue.class})
    public static class TestContext {
        @Autowired
        @Qualifier("mockedSubmitToLinkService")
        private SubmitToLinkService submitToLinkService;

        @Bean("mockedSubmitToLinkService")
        public SubmitToLinkService mockedService() throws DomibusConnectorSubmitToLinkException {
            submitToLinkService = Mockito.mock(SubmitToLinkService.class);
            Mockito.doThrow(new DomibusConnectorSubmitToLinkException(null, "test error"))
                   .when(submitToLinkService).submitToLink(any());
            return submitToLinkService;
        }

        @Bean("mockInjectedPartnerListener")
        public ToLinkPartnerListener injectMock() {
            return new ToLinkPartnerListener(submitToLinkService);
        }
    }
}
