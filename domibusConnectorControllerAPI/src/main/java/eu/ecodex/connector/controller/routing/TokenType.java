/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.controller.routing;

import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;

/**
 * Enumeration class representing different types of tokens.
 * Each token type has a corresponding regular expression pattern,
 * a string representation, and a human-readable string. It also provides
 * utility methods for pattern matching and extracting the last matching character.
 */
@Getter
public enum TokenType {
    AS4_SERVICE_TYPE("ServiceType"),
    AS4_SERVICE_NAME("ServiceName"),
    AS4_FINAL_RECIPIENT("FinalRecipient"),
    AS4_FROM_PARTY_ID("FromPartyId"),
    AS4_FROM_PARTY_ID_TYPE("FromPartyIdType"),
    AS4_FROM_PARTY_ROLE("FromPartyRole"),
    AS4_ACTION("Action"),
    OR("\\|", "|"),
    AND("&", "&"),
    NOT("not", "not"),
    EQUALS("equals"),
    STARTSWITH("startswith"),
    SEMICOLON(","),
    BRACKET_OPEN("\\(", "("),
    BRACKET_CLOSE("\\)", ")"),
    WHITESPACE("\\p{javaWhitespace}"),
    VALUE("'[\\w:_\\-~\\./#\\?]+'"),
    ILLEGAL_TOKEN("", "Illegal Token"), // special token for illegal none parseable tokens
    START_TOKEN(
        "", "Start Token"), // the start token, a placeholder to mark the start of processing
    END_TOKEN(
        "", "End Token") // the last token length 0, a placeholder to mark the end of processing
    ;
    public static final List<TokenType> AS_4_ATTRIBUTE_TOKEN_TYPES = Stream.of(
        AS4_SERVICE_TYPE,
        AS4_SERVICE_NAME,
        AS4_FINAL_RECIPIENT,
        AS4_ACTION,
        AS4_FROM_PARTY_ID,
        AS4_FROM_PARTY_ID_TYPE,
        AS4_FROM_PARTY_ROLE
    ).collect(Collectors.toList());
    public static final List<TokenType> COMPARE_OPERATOR_TOKEN_TYPES =
        Stream.of(STARTSWITH, EQUALS).collect(Collectors.toList());
    public static final List<TokenType> BOOLEAN_OPERATOR_TOKEN_TYPES =
        Stream.of(AND, OR).collect(Collectors.toList());
    public static final List<TokenType> ALL_OPERATOR_TOKEN_TYPES =
        Stream.of(
                BOOLEAN_OPERATOR_TOKEN_TYPES,
                COMPARE_OPERATOR_TOKEN_TYPES,
                Stream.of(NOT).collect(Collectors.toList())
            )
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
    private final Pattern pattern;
    private final String string;
    private final String humanString;

    TokenType(String regex) {
        this.string = regex;
        this.pattern = Pattern.compile("^" + regex); // match from beginning
        this.humanString = regex;
    }

    TokenType(String regex, String v) {
        this.string = regex;
        this.humanString = v;
        this.pattern = Pattern.compile("^" + regex); // match from beginning
    }

    @Override
    public String toString() {
        return humanString;
    }

    /**
     * Returns the index of the last matching character in the given string.
     *
     * @param s the string to search for a match in
     * @return the index of the last matching character, or -1 if no match is found
     */
    public int lastMatchingCharacter(String s) {
        var m = pattern.matcher(s);

        if (m.find()) {
            return m.end();
        }
        return -1;
    }
}
