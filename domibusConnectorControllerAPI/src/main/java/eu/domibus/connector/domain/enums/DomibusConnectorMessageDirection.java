/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.domain.enums;

import static eu.domibus.connector.domain.enums.MessageTargetSource.BACKEND;
import static eu.domibus.connector.domain.enums.MessageTargetSource.GATEWAY;

import eu.domibus.connector.domain.model.DomibusConnectorMessageDetails;
import java.util.stream.Stream;
import lombok.Getter;

/**
 * Enum representing the message direction for the {@link DomibusConnectorMessageDetails}.
 */
@Getter
public enum DomibusConnectorMessageDirection {
    /**
     * used to mark a transport which is received by the backend and transported to the gateway.
     */
    BACKEND_TO_GATEWAY(BACKEND, GATEWAY),
    /**
     * used to mark a transport which is received by the gateway and transported to the backend.
     */
    GATEWAY_TO_BACKEND(GATEWAY, BACKEND);

    DomibusConnectorMessageDirection(MessageTargetSource source,
                                     MessageTargetSource target) {
        this.source = source;
        this.target = target;
    }

    private final MessageTargetSource source;
    private final MessageTargetSource target;

    /**
     * Returns the DomibusConnectorMessageDirection based on the directionSource and
     * directionTarget.
     *
     * @param directionSource - the source of the message direction
     * @param directionTarget - the target of the message direction
     *
     * @return the DomibusConnectorMessageDirection corresponding to the directionSource and
     *      directionTarget
     * @throws IllegalArgumentException if directionSource or directionTarget is null
     */
    public static DomibusConnectorMessageDirection fromMessageTargetSource(
        MessageTargetSource directionSource, MessageTargetSource directionTarget) {
        if (directionSource == null) {
            throw new IllegalArgumentException("Direction Source is not allowed to be null!");
        }
        if (directionTarget == null) {
            throw new IllegalArgumentException("Direction target is not allowed to be null!");
        }
        return Stream.of(DomibusConnectorMessageDirection.values())
            .filter(d -> d.getSource() == directionSource && d.getTarget() == directionTarget)
            .findFirst()
            .get();
    }

    public static DomibusConnectorMessageDirection revert(
        DomibusConnectorMessageDirection direction) {
        return fromMessageTargetSource(direction.getTarget(), direction.getSource());
    }
}
