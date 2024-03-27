package eu.domibus.connector.domain.enums;

import java.util.stream.Stream;

import static eu.domibus.connector.domain.enums.MessageTargetSource.BACKEND;
import static eu.domibus.connector.domain.enums.MessageTargetSource.GATEWAY;


public enum DomibusConnectorMessageDirection {
    /**
     * used to mark a transport which is received by the backend
     * and transported to the gateway
     */
    BACKEND_TO_GATEWAY(BACKEND, GATEWAY),
    /**
     * used to mark a transport which is received by the gateway
     * and transported to the backend
     */
    GATEWAY_TO_BACKEND(GATEWAY, BACKEND);

    private final MessageTargetSource source;
    private final MessageTargetSource target;

    DomibusConnectorMessageDirection(MessageTargetSource source, MessageTargetSource target) {
        this.source = source;
        this.target = target;
    }

    public static DomibusConnectorMessageDirection fromMessageTargetSource(
            MessageTargetSource directionSource,
            MessageTargetSource directionTarget) {
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

    public static DomibusConnectorMessageDirection revert(DomibusConnectorMessageDirection direction) {
        return fromMessageTargetSource(direction.getTarget(), direction.getSource());
    }

    public MessageTargetSource getSource() {
        return source;
    }

    public MessageTargetSource getTarget() {
        return target;
    }
}

