/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.common;

import lombok.experimental.UtilityClass;

/**
 * This class contains the names of the Spring profiles used in the application.
 * The profiles are used to configure different environments for the application,
 * such as development, testing, and production.
 */
@UtilityClass
public class SpringProfiles {
    public static final String TEST = "test";
}
