/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.controller.routing;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;

/**
 * A converter that converts a String to a RoutingRulePattern object.
 */
public class RoutingRulePatternConverter implements Converter<String, RoutingRulePattern> {
    @Nullable
    @Override
    public RoutingRulePattern convert(String source) {
        if (source == null) {
            return null;
        }
        return new RoutingRulePattern(source);
    }
}
