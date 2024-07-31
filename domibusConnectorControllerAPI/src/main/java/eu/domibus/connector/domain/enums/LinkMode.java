/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.domain.enums;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * LinkMode represents the different modes of communication between link partners.
 */
public enum LinkMode {
    PUSH("push"), // pushes messages to the remote link partner
    PULL("pull"), // pulls messages from the remote link partner
    PASSIVE("passive"); // rcv messages from the remote partner, remote partner does the push

    private LinkMode(String dbName) {
        this.dbName = dbName;
    }

    String dbName;

    public String getDbName() {
        return dbName;
    }

    /**
     * Returns an Optional containing the LinkMode that matches the given database name. The
     * comparison is case-insensitive.
     *
     * @param dbName the database name to match against the LinkMode's dbName
     *
     * @return Optional containing the matched LinkMode, or empty if no match was found
     */
    public static Optional<LinkMode> ofDbName(String dbName) {
        return Stream.of(LinkMode.values())
            .filter(l -> l.dbName.equalsIgnoreCase(dbName))
            .findFirst();
    }
}
