package eu.domibus.connector.controller.routing;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;


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
