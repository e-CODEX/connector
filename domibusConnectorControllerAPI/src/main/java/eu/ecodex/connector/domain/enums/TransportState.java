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
