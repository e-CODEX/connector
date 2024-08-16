/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
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
