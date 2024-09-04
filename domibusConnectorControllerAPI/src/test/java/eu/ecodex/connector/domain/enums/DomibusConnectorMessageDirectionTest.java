/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.enums;

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
