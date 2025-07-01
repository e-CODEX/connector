/*
 * Copyright 2025 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connectorplugins.link.testbackend;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class responsible for scanning and registering beans within the same package
 * or sub-packages of the {@link TestBackendAutoConfiguration} class. This setup ensures that
 * all necessary components defined in the `testbackend` plugin context are properly initialized.
 */
@Configuration
@ComponentScan(basePackageClasses = TestBackendAutoConfiguration.class)
public class TestBackendScanningConfiguration {
}
