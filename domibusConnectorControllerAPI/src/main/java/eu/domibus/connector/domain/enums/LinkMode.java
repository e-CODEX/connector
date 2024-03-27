package eu.domibus.connector.domain.enums;

import java.util.Optional;
import java.util.stream.Stream;


public enum LinkMode {
    PUSH("push"), // pushes messages to the remote link partner
    PULL("pull"), // pulls messages from the remote link partner
    PASSIVE("passive"); // rcv messages from the remote partner, remote partner does the push

    String dbName;

    LinkMode(String dbName) {
        this.dbName = dbName;
    }

    public static Optional<LinkMode> ofDbName(String dbName) {
        return Stream.of(LinkMode.values())
                     .filter(l -> l.dbName.equalsIgnoreCase(dbName))
                     .findFirst();
    }

    public String getDbName() {
        return dbName;
    }
}
