/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.domain.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class DomibusConnectorMessageDirectionTest {
    @Test
    void revert() {
        assertThat(DomibusConnectorMessageDirection.revert(
            DomibusConnectorMessageDirection.GATEWAY_TO_BACKEND))
            .isEqualTo(DomibusConnectorMessageDirection.BACKEND_TO_GATEWAY);

        assertThat(DomibusConnectorMessageDirection.revert(
            DomibusConnectorMessageDirection.BACKEND_TO_GATEWAY))
            .isEqualTo(DomibusConnectorMessageDirection.GATEWAY_TO_BACKEND);
    }
}
