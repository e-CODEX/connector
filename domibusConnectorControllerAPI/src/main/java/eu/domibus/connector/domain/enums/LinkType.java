/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.domain.enums;

import java.util.NoSuchElementException;
import java.util.stream.Stream;

/**
 * The LinkType enum represents the different types of links available.
 */
public enum LinkType {
    GATEWAY("GATEWAY"), BACKEND("BACKEND");

    private LinkType(String dbName) {
        this.dbName = dbName;
    }

    String dbName;

    public String getDbName() {
        return dbName;
    }

    /**
     * Returns the LinkType enum constant associated with the specified database name.
     *
     * @param dbName the database name to search for
     *
     * @return the LinkType enum constant associated with the database name
     * @throws NoSuchElementException if no enum constant with the specified database name is found
     */
    public static LinkType ofDbName(String dbName) {
        return Stream.of(LinkType.values())
            .filter(l -> l.dbName.equalsIgnoreCase(dbName))
            .findFirst().get();
    }
}
