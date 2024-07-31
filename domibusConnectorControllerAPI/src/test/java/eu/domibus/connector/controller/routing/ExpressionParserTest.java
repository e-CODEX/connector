/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.controller.routing;

import static org.assertj.core.api.Assertions.assertThat;

import eu.domibus.connector.tools.logging.LoggingMarker;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ExpressionParserTest {
    private static final Logger LOGGER = LogManager.getLogger(ExpressionParserTest.class);

    @ParameterizedTest
    @ValueSource(strings = {
            "|(&(equals(ServiceName, 'Test'), equals(FromPartyId, 'gw01')), "
                + "equals(FromPartyId, 'gw02'))",
            "not(|(&(equals(ServiceName, 'Test'), equals(FromPartyId, 'gw01')), "
                + "equals(FromPartyId, 'gw02')))"

    })
    void testParseOfValidExpressions(String expression) {
        ExpressionParser expressionParser = new ExpressionParser(expression);
        assertThat(expressionParser.getParsedExpression()).isPresent();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "|(&(equals(SrviceName, 'Test'), equals(FromPartyId, 'gw01')), "
                + "equals(FromPartyId, 'gw02'))",
            "|((equals(ServiceName, 'Test'), equals(FromPartyId, 'gw01')), "
                + "equals(FromPartyId, 'gw02'))",
            "|(&(euals(ServiceName, 'Test'), equals(FromPartyId, 'gw01')), "
                + "equals(FromPartyId, 'gw02'))"

    })
    void testParseOfInvalidExpressions(String expression) {
        ExpressionParser expressionParser = new ExpressionParser(expression);
        assertThat(expressionParser.getParsedExpression()).isEmpty();
        assertThat(expressionParser.getParsingExceptions()).hasSize(1);

        LOGGER.info(LoggingMarker.Log4jMarker.TEST_LOG, expressionParser.getParsingExceptions()
            .stream()
            .map(Exception::getMessage)
            .collect(Collectors.joining(",")));
    }
}
