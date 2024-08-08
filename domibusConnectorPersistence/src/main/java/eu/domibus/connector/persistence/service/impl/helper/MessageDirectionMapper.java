/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.persistence.service.impl.helper;

import eu.domibus.connector.domain.enums.DomibusConnectorMessageDirection;
import eu.domibus.connector.domain.enums.MessageTargetSource;
import eu.domibus.connector.persistence.model.enums.PMessageDirection;
import javax.annotation.Nonnull;
import lombok.experimental.UtilityClass;

/**
 * This class provides mapping functionality between {@link DomibusConnectorMessageDirection} and
 * {@link PMessageDirection} enums.
 */
@UtilityClass
public class MessageDirectionMapper {
    /**
     * Maps a {@link DomibusConnectorMessageDirection} to a {@link PMessageDirection}.
     *
     * @param direction the {@link DomibusConnectorMessageDirection} to be mapped
     * @return the corresponding {@link PMessageDirection}
     * @throws IllegalArgumentException if the provided direction is invalid
     */
    public static @Nonnull
    PMessageDirection mapFromDomainToPersistence(
        @Nonnull DomibusConnectorMessageDirection direction) {
        return switch (direction) {
            case BACKEND_TO_GATEWAY -> PMessageDirection.NAT_TO_GW;
            case GATEWAY_TO_BACKEND -> PMessageDirection.GW_TO_NAT;
            default -> throw new IllegalArgumentException("Provided direction is invalid!");
        };
    }

    /**
     * Maps the {@link PMessageDirection} enum from persistence layer to the corresponding
     * {@link DomibusConnectorMessageDirection} enum in the domain layer.
     *
     * @param direction the {@link PMessageDirection} to be mapped
     * @return the corresponding {@link DomibusConnectorMessageDirection}
     * @throws IllegalArgumentException if the provided direction is null or invalid
     */
    public static @Nonnull
    DomibusConnectorMessageDirection mapFromPersistenceToDomain(
        @Nonnull PMessageDirection direction) {
        if (direction == null) {
            throw new IllegalArgumentException("Cannot map null to a direction!");
        }
        return switch (direction) {
            case NAT_TO_GW -> DomibusConnectorMessageDirection.BACKEND_TO_GATEWAY;
            case GW_TO_NAT -> DomibusConnectorMessageDirection.GATEWAY_TO_BACKEND;
            default -> throw new IllegalArgumentException("Provided direction is invalid!");
        };
    }

    /**
     * Maps the {@link MessageTargetSource} enums from persistence layer to the corresponding
     * {@link DomibusConnectorMessageDirection} enum in the domain layer.
     *
     * @param directionSource the source {@link MessageTargetSource} of the message direction
     * @param directionTarget the target {@link MessageTargetSource} of the message direction
     * @return the corresponding {@link DomibusConnectorMessageDirection}
     * @throws IllegalArgumentException if either directionSource or directionTarget is null
     */
    public static DomibusConnectorMessageDirection mapFromPersistenceToDomain(
        MessageTargetSource directionSource, MessageTargetSource directionTarget) {
        return DomibusConnectorMessageDirection.fromMessageTargetSource(
            directionSource, directionTarget);
    }

    public static DomibusConnectorMessageDirection map(PMessageDirection direction) {
        return mapFromPersistenceToDomain(direction);
    }

    public static PMessageDirection map(DomibusConnectorMessageDirection direction) {
        return mapFromDomainToPersistence(direction);
    }
}
