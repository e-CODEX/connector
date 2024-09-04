/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.security.container.service;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import test.context.SecurityToolkitTestContext;

/**
 * This class is a test class that extends the {@link ECodexContainerFactoryServiceITCaseTemplate}
 * class.
 */
@SpringBootTest(classes = SecurityToolkitTestContext.class)
@ActiveProfiles({"test", "test-sig", "seclib-test"})
public class ECodexContainerFactoryServiceSignatureBasedTest
    extends ECodexContainerFactoryServiceITCaseTemplate {
}
