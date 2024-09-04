/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.firststartup;

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
