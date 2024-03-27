package eu.domibus.connector.domain.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class DomibusConnectorMessageDirectionTest {
    @Test
    void revert() {
        assertThat(
                DomibusConnectorMessageDirection
                        .revert(DomibusConnectorMessageDirection.GATEWAY_TO_BACKEND)
        )
                .isEqualTo(DomibusConnectorMessageDirection.BACKEND_TO_GATEWAY);

        assertThat(DomibusConnectorMessageDirection.revert(
                DomibusConnectorMessageDirection.BACKEND_TO_GATEWAY)
        )
                .isEqualTo(DomibusConnectorMessageDirection.GATEWAY_TO_BACKEND);
    }
}
