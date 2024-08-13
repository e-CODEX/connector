/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.firststartup;

import java.util.UUID;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * The `InitializeAdminUserProperties` class represents the properties required to initialize
 * the admin user.
 * It is annotated with`@Data` and
 * `@ConfigurationProperties(prefix = InitializeAdminUserProperties.PREFIX)`,
 * making it a data object and allowing it to be configured using properties with a specific prefix.
 *
 * <p>The class contains the following properties:
 * - `enabled` (boolean): Indicates whether the initialization of the admin user is enabled.
 * - `initialChangeRequired` (boolean): Indicates whether the admin user should be required to
 * change the initial password.
 * - `initialUserName` (String): The initial username for the admin user.
 * - `logInitialToConsole` (boolean): Indicates whether the initialization process should log the
 * initial password to the console.
 * - `initialUserPassword` (String): The initial password for the admin user.
 */
@Data
@ConfigurationProperties(prefix = InitializeAdminUserProperties.PREFIX)
public class InitializeAdminUserProperties {
    public static final String PREFIX = "connector.init.user";
    private boolean enabled;
    private boolean initialChangeRequired = true;
    private String initialUserName = "admin";
    private boolean logInitialToConsole = true;
    private String initialUserPassword = UUID.randomUUID().toString();
}
