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
