/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.domain.enums;

import java.util.NoSuchElementException;
import java.util.stream.Stream;
import lombok.Getter;
import org.springframework.core.style.ToStringCreator;

/**
 * Represents the state of transport.
 */
@Getter
public enum TransportState {
    ACCEPTED("accepted", 10),
    PENDING("pending", 1),
    PENDING_DOWNLOADED("pend_down", 2),
    FAILED("failed", 10);

    private TransportState(String dbName, int priority) {
        this.priority = priority;
        this.dbName = dbName;
    }

    final String dbName;
    final int priority;

    /**
     * Returns the TransportState enum value based on the provided database name.
     *
     * @param dbName The database name to match against the enum values.
     * @return The TransportState enum value that matches the provided database name.
     * @throws NoSuchElementException If no enum value matching the provided database name is
     *                                found.
     */
    public static TransportState ofDbName(String dbName) {
        return Stream.of(TransportState.values())
            .filter(l -> l.dbName.equalsIgnoreCase(dbName))
            .findFirst().get();
    }

    @Override
    public String toString() {
        return new ToStringCreator(this)
            .append("name", this.name())
            .append("priority", this.getPriority())
            .toString();
    }
}
