package eu.domibus.connector.domain.enums;

import java.util.stream.Stream;


public enum LinkType {
    GATEWAY("GATEWAY"),
    BACKEND("BACKEND");

    private final String dbName;

    LinkType(String dbName) {
        this.dbName = dbName;
    }

    public static LinkType ofDbName(String dbName) {
        return Stream.of(LinkType.values())
                     .filter(l -> l.dbName.equalsIgnoreCase(dbName))
                     .findFirst().get();
    }

    public String getDbName() {
        return dbName;
    }
}
