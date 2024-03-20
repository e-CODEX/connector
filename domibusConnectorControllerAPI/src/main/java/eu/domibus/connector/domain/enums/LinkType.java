package eu.domibus.connector.domain.enums;

import java.util.stream.Stream;

public enum LinkType {

    GATEWAY("GATEWAY"), BACKEND("BACKEND");

    private LinkType(String dbName) {
        this.dbName = dbName;
    }

    String dbName;

    public String getDbName() {
        return dbName;
    }

    public static LinkType ofDbName(String dbName) {
        return Stream.of(LinkType.values())
                .filter(l -> l.dbName.equalsIgnoreCase(dbName))
                .findFirst().get();
    }

}
