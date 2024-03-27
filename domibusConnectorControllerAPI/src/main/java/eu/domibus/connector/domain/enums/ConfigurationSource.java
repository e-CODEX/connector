package eu.domibus.connector.domain.enums;

public enum ConfigurationSource {
    DB, // Database
    IMPL, // loaded by Code, implementation
    ENV  // loaded from Spring environment
}
