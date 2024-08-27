/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.domain.enums;

import jakarta.annotation.Nullable;
import java.util.stream.Stream;

/**
 * Enum representing the source of a message target.
 */
public enum MessageTargetSource {
    GATEWAY("GATEWAY"),
    BACKEND("BACKEND");

    /**
     * Retrieves the {@link MessageTargetSource} enum value based on the provided database name.
     *
     * @param dbData The database name to search for.
     * @return The converterd value from the String value, null if the input is null
     */
    public static @Nullable MessageTargetSource ofOfDbName(String dbData) {
        if (dbData == null) {
            return null;
        }
        return Stream.of(MessageTargetSource.values())
                     .filter(t -> t.getDbName().equals(dbData))
                     .findFirst()
                     .orElse(null);
    }

    MessageTargetSource(String dbName) {
        this.dbName = dbName;
    }

    /**
     * This name is used within the database.
     */
    private String dbName;

    public String getDbName() {
        return dbName;
    }
}
