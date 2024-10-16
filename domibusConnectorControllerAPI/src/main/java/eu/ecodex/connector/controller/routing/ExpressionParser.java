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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;
import org.springframework.util.StringUtils;

/**
 * The ExpressionParser class is used to parse an input pattern and generate an Expression object
 * based on the pattern.
 */
@SuppressWarnings("squid:S1135")
public class ExpressionParser {
    private final String pattern;
    private Expression parsedExpression;
    private List<ParsingException> parsingExceptions = new ArrayList<>();
    private Token lastConsumedToken;
    private LinkedList<Token> tokens;
    // TODO: list with parsing errors...
    private static Token START_TOKEN_VALUE = new Token();

    static {
        START_TOKEN_VALUE.tokenType = TokenType.START_TOKEN;
        START_TOKEN_VALUE.value = "";
        START_TOKEN_VALUE.start = 0;
        START_TOKEN_VALUE.end = 0;
    }

    public ExpressionParser(String pattern) {
        this.pattern = pattern;
        parseExpression();
    }

    private void parseExpression() {
        if (!StringUtils.hasText(pattern)) {
            throw new IllegalArgumentException("Pattern is not allowed to be empty or null!");
        }

        var parsing = new StringBuilder();
        parsing.append(pattern.trim());

        tokens = new LinkedList<>();
        var t = getToken(parsing, 0);
        // TokenAndValue t = START_TOKEN_VALUE;
        while (t.tokenType != TokenType.END_TOKEN && t.tokenType != TokenType.ILLEGAL_TOKEN) {
            if (t.tokenType != TokenType.WHITESPACE) {
                tokens.add(t);
            }
            t = getToken(parsing, t.end);
        }

        // convert list of tokens into matchers
        lastConsumedToken = START_TOKEN_VALUE;

        try {
            this.parsedExpression = processTokens();
        } catch (ParsingException p) {
            this.parsingExceptions.add(p);
        }
    }

    private Token getToken(StringBuilder parsing, int offset) {
        if (parsing.isEmpty()) {
            var tv = new Token();
            tv.tokenType = TokenType.END_TOKEN;
            tv.start = offset;
            tv.end = offset;
            tv.value = "";
            return tv;
        }
        for (TokenType t : TokenType.values()) {
            int l = t.lastMatchingCharacter(parsing.toString());
            if (l != -1) {
                var tv = new Token();
                tv.tokenType = t;
                tv.value = parsing.substring(0, l);
                tv.start = offset;
                tv.end = offset + l;
                parsing.delete(0, l);
                return tv;
            }
        }
        // when no valid token could be parsed, illegal token is used as placeholder
        var tv = new Token();
        tv.tokenType = TokenType.ILLEGAL_TOKEN;
        tv.start = offset;
        tv.end = offset + parsing.length();
        tv.value = parsing.toString();
        return tv;
    }

    private Expression processTokens() {
        Expression exp = null;
        TokenType t;

        var tv = getOneOfExpectedTokenFromListOrThrow(TokenType.ALL_OPERATOR_TOKEN_TYPES);
        t = tv.tokenType;

        lastConsumedToken = tv;

        if (t == TokenType.OR || t == TokenType.AND) {
            // parsing BOOLEAN_EXPRESSION
            exp = processBooleanToken(tv);
        } else if (t == TokenType.EQUALS || t == TokenType.STARTSWITH) {
            // parsing matching expression
            exp = processCompareToken(tv);
        } else if (t == TokenType.NOT) {
            exp = processNotToken(tv);
        } else {
            throw new ParsingException(lastConsumedToken, tv, String.format(
                "Parsing error at %d: I would expect one of these: %s", tv.end + 1,
                TokenType.ALL_OPERATOR_TOKEN_TYPES
                    .stream()
                    .map(Enum::toString)
                    .collect(Collectors.joining(","))
            ));
        }

        return exp;
    }

    private Expression processNotToken(Token notToken) {
        getOneOfExpectedTokenFromListOrThrow(TokenType.BRACKET_OPEN);

        Expression exp = processTokens();

        getOneOfExpectedTokenFromListOrThrow(TokenType.BRACKET_CLOSE);

        return new NotExpression(exp, notToken);
    }

    private Expression processCompareToken(Token operatorToken) {

        Token bracketOpenToken = getOneOfExpectedTokenFromListOrThrow(TokenType.BRACKET_OPEN);
        lastConsumedToken = bracketOpenToken;

        Token as4Attribute =
            getOneOfExpectedTokenFromListOrThrow(TokenType.AS_4_ATTRIBUTE_TOKEN_TYPES);
        lastConsumedToken = as4Attribute;

        Token semiliconToken = getOneOfExpectedTokenFromListOrThrow(TokenType.SEMICOLON);
        lastConsumedToken = semiliconToken;

        Token compareString = getOneOfExpectedTokenFromListOrThrow(TokenType.VALUE);
        lastConsumedToken = compareString;
        String valueString = compareString.value.substring(
            1,
            compareString.value.length() - 1
        ); // remove leading ' and trailing '
        Expression exp;

        if (operatorToken.tokenType == TokenType.EQUALS) {
            exp = new EqualsExpression(as4Attribute.tokenType, valueString);
        } else if (operatorToken.tokenType == TokenType.STARTSWITH) {
            exp = new StartsWithExpression(as4Attribute.tokenType, valueString);
        } else {
            throw new IllegalStateException(
                "Illegal Token detected. Exception should have already been thrown!");
        }

        lastConsumedToken = getOneOfExpectedTokenFromListOrThrow(TokenType.BRACKET_CLOSE);

        return exp;
    }

    private Token getOneOfExpectedTokenFromListOrThrow(TokenType expectedTokens) {
        return getOneOfExpectedTokenFromListOrThrow(
            Stream.of(expectedTokens).collect(Collectors.toList()));
    }

    private Token getOneOfExpectedTokenFromListOrThrow(List<TokenType> expectedTokenTypes) {
        String expectedTokensString = expectedTokenTypes.stream()
            .map(TokenType::toString).collect(Collectors.joining(","));
        if (tokens.isEmpty()) {
            throw new ParsingException(
                lastConsumedToken, String.format(
                "Parsing error at %d: I would expect one of these: %s",
                this.lastConsumedToken.end + 1, expectedTokensString
            ));
        }
        Token token = tokens.removeFirst();
        if (token.tokenType == TokenType.ILLEGAL_TOKEN) {
            throw new ParsingException(
                lastConsumedToken, token, String.format(
                "Parsing error at %d: Invalid Token found. I would expect one of these: %s",
                this.lastConsumedToken.end + 1, expectedTokensString
            ));
        }
        if (isNotToken(token, expectedTokenTypes)) {
            if (tokens.isEmpty()) {
                throw new ParsingException(
                    lastConsumedToken, token, String.format(
                    "Parsing error at %d: I would expect one of these: %s, but i got %s",
                    this.lastConsumedToken.end + 1, expectedTokensString, token
                ));
            }
        }
        this.lastConsumedToken = token;
        return token;
    }

    private Expression processBooleanToken(Token operatorToken) {

        Token bracketOpen = getOneOfExpectedTokenFromListOrThrow(TokenType.BRACKET_OPEN);
        lastConsumedToken = bracketOpen;

        Expression exp1 = processTokens();

        Token semiliconToken = getOneOfExpectedTokenFromListOrThrow(TokenType.SEMICOLON);
        lastConsumedToken = semiliconToken;

        Expression exp2 = processTokens();

        Token bracketCloseToken = getOneOfExpectedTokenFromListOrThrow(TokenType.BRACKET_CLOSE);

        return new BinaryOperatorExpression(operatorToken.tokenType, exp1, exp2);
    }

    private boolean isNotToken(Token tv, List<TokenType> t) {
        return !(isToken(tv, t)); // tv == null || tv.t != t;
    }

    private boolean isNotToken(Token tv, TokenType t) {
        return !(isToken(tv, t)); // tv == null || tv.t != t;
    }

    private boolean isToken(Token tv, TokenType t) {
        return tv != null && tv.tokenType == t;
    }

    private boolean isToken(Token tv, List<TokenType> t) {
        return tv != null && t.contains(tv.tokenType);
    }

    public String getPattern() {
        return pattern;
    }

    public Optional<Expression> getParsedExpression() {
        return Optional.ofNullable(parsedExpression);
    }

    public List<ParsingException> getParsingExceptions() {
        return parsingExceptions;
    }

    /**
     * Represents a parsing exception that can occur during parsing of tokens.
     */
    public static class ParsingException extends RuntimeException {
        @Getter
        private Token lastConsumedToken;
        private Token currentToken = null;
        @Getter
        private int col = -1;

        public ParsingException(int col, String message) {
            super(message);
            this.col = col;
        }

        public ParsingException(String message) {
            super(message);
        }

        public ParsingException(String message, Throwable cause) {
            super(message, cause);
        }

        /**
         * Constructs a new ParsingException with the specified last consumed token and format.
         *
         * @param lastConsumedToken the last consumed token
         * @param format            the format of the exception message
         */
        public ParsingException(Token lastConsumedToken, String format) {
            super(format);
            this.col = lastConsumedToken.start;
            this.lastConsumedToken = lastConsumedToken;
        }

        /**
         * Constructs a new ParsingException with the specified last consumed token, current token,
         * and format.
         *
         * @param lastConsumedToken the last consumed token
         * @param currentToken      the current token
         * @param format            the format of the exception message
         */
        public ParsingException(Token lastConsumedToken, Token currentToken, String format) {
            super(format);
            this.lastConsumedToken = lastConsumedToken;
            this.currentToken = currentToken;
            this.col = currentToken.start;
        }

        /**
         * Gets the current token.
         *
         * @return an Optional object containing the current token, or an empty Optional if the
         *      current token is null.
         */
        public Optional<Token> getCurrentToken() {
            return Optional.ofNullable(currentToken);
        }
    }
}
