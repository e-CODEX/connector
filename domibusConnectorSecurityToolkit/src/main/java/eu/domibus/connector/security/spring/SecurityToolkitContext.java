
/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.security.spring;

import org.springframework.context.annotation.Configuration;

/**
 * This class is a configuration class that provides the necessary beans and configurations for the
 * security toolkit.
 *
 * <p>Use this class to define and configure security-related beans, such as security filters,
 * authentication providers, access control rules, and more.
 *
 * <p>To use this class, simply annotate it as a {@code @Configuration} class in your application.
 * Spring will then automatically scan and load the necessary configurations.
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
@Configuration
public class SecurityToolkitContext {
}
