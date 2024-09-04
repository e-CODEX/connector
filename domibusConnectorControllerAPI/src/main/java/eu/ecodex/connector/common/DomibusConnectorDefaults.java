/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.common;

import lombok.experimental.UtilityClass;

/**
 * This class defines the default values for the Domibus Connector.
 */
@UtilityClass
public class DomibusConnectorDefaults {
    public static final String DEFAULT_BACKEND_NAME = "default_backend";
    public static final String DEFAULT_GATEWAY_NAME = "default_gateway";
    public static final String DEFAULT_TEST_BACKEND = "default_test_backend";
}
