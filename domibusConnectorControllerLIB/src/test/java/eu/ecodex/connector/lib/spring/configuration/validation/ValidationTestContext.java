/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.lib.spring.configuration.validation;

import eu.ecodex.connector.common.service.DCKeyStoreService;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * The ValidationTestContext class is annotated with @SpringBootApplication and @Import. It serves
 * as the application context for validating key store properties.
 *
 * <p>It depends on the DCKeyStoreService class, which is responsible for loading and storing
 * keystores centrally.
 * The DCKeyStoreService class provides methods for loading and retrieving keystores, as well as
 * validating the existence of keys and certificates.
 *
 * @see DCKeyStoreService
 */
@SpringBootApplication
@Import(DCKeyStoreService.class)
public class ValidationTestContext {
}
