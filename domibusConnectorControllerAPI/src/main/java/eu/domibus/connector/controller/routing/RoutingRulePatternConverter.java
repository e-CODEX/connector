/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
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
