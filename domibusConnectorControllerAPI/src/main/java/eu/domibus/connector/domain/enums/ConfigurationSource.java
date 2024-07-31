/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.domain.enums;

/**
 * Represents the source of configuration.
 */
public enum ConfigurationSource {
    DB, // Database
    IMPL, // loaded by Code, implementation
    ENV;  // loaded from Spring environment
}
