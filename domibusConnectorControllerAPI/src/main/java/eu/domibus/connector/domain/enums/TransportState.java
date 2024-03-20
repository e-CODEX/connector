package eu.domibus.connector.domain.enums;

import org.springframework.core.style.ToStringCreator;

import java.util.stream.Stream;

public enum TransportState {


    ACCEPTED("accepted", 10),
    PENDING("pending", 1),
    PENDING_DOWNLOADED("pend_down", 2),
    FAILED("failed", 10);


    private TransportState(String dbName, int priority) {
        this.priority = priority;
        this.dbName = dbName;
    }

    String dbName;
    int priority;

    public String getDbName() {
        return dbName;
    }

    public int getPriority() {
        return priority;
    }

    public static TransportState ofDbName(String dbName) {
        return Stream.of(TransportState.values())
                .filter(l -> l.dbName.equalsIgnoreCase(dbName))
                .findFirst().get();
    }

    public String toString() {
        return new ToStringCreator(this)
                .append("name", this.name())
                .append("priority", this.getPriority())
                .toString();
    }



}
