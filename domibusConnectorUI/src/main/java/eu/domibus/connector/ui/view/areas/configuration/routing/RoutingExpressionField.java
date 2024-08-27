/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.ui.view.areas.configuration.routing;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HtmlContainer;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Pre;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import eu.domibus.connector.controller.routing.BinaryOperatorExpression;
import eu.domibus.connector.controller.routing.Expression;
import eu.domibus.connector.controller.routing.ExpressionParser;
import eu.domibus.connector.controller.routing.MatchExpression;
import eu.domibus.connector.controller.routing.RoutingRulePattern;
import eu.domibus.connector.controller.routing.TokenType;
import eu.domibus.connector.domain.model.DomibusConnectorAction;
import eu.domibus.connector.domain.model.DomibusConnectorParty;
import eu.domibus.connector.domain.model.DomibusConnectorService;
import eu.domibus.connector.ui.service.WebPModeService;
import eu.domibus.connector.ui.utils.UiStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * The RoutingExpressionField class represents a custom field used for inputting and displaying
 * routing rule patterns.
 */
@SuppressWarnings("squid:S1135")
public class RoutingExpressionField extends CustomField<RoutingRulePattern> {
    public static final String ERROR_COLOR = "#ff0000";
    public static final String OPERATOR_COLOR = "#0000ff"; // blue ?
    public static final String AS4ATTRIBUTE_COLOR = "#000000"; //
    public static final String AS4VALUE_EXISTANT_COLOR = "#00ff00"; // green
    public static final String AS4VALUE_NONEXISTANT_COLOR = "#ffff00"; // yellow
    public static final List<TokenType> OPERATORS = Stream
        .of(TokenType.OR, TokenType.AND)
        .toList();
    public static final List<TokenType> MATCH_OPERATORS = Stream
        .of(TokenType.STARTSWITH, TokenType.EQUALS)
        .toList();
    public static final List<TokenType> MATCH_ATTRIBUTES = Stream
        .of(TokenType.AS4_FINAL_RECIPIENT, TokenType.AS4_ACTION,
            TokenType.AS4_FROM_PARTY_ID, TokenType.AS4_FROM_PARTY_ROLE,
            TokenType.AS4_FROM_PARTY_ID_TYPE,
            TokenType.AS4_SERVICE_NAME, TokenType.AS4_SERVICE_TYPE, TokenType.AS4_ACTION
        )
        .toList();
    private final VerticalLayout layout = new VerticalLayout();
    private final TextField textField = new TextField();
    private RoutingRulePattern value;
    private final Div routingExpressionField = new Div();
    private final VerticalLayout errorList = new VerticalLayout();
    private final WebPModeService webPModeService;

    /**
     * Constructor.
     *
     * @param webPModeService The service used to retrieve webPMode information.
     */
    public RoutingExpressionField(WebPModeService webPModeService) {
        this.webPModeService = webPModeService;
        this.add(layout);
        layout.setPadding(false);
        layout.setMargin(false);

        textField.setWidth("15cm");
        textField.setWidthFull();
        textField.addValueChangeListener(this::tfValueChanged);

        layout.add(textField);
        layout.add(errorList);
        errorList.getStyle().set(UiStyle.TAG_COLOR, ERROR_COLOR);
        layout.add(routingExpressionField);
    }

    @Override
    protected RoutingRulePattern generateModelValue() {
        // TODO: read value convert to RoutingRulePattern..
        return value;
    }

    @Override
    protected void setPresentationValue(RoutingRulePattern routingRulePattern) {
        var exp = routingRulePattern.getExpression();
        textField.setValue(exp.toString());
    }

    private void tfValueChanged(
        ComponentValueChangeEvent<TextField, String> textFieldStringComponentValueChangeEvent) {
        var eventValue = textFieldStringComponentValueChangeEvent.getValue();

        var expressionParser = new ExpressionParser(eventValue);
        errorList.removeAll();
        if (expressionParser.getParsedExpression().isPresent()) {
            textField.setInvalid(false);
            var routingRulePattern = new RoutingRulePattern(eventValue);
            this.value = routingRulePattern;
            this.setModelValue(routingRulePattern, true);
        } else {
            textField.setInvalid(true);
            expressionParser.getParsingExceptions().forEach(e -> {
                var listItem = new Div();
                listItem.add(getErrorLocation(eventValue, e));
                errorList.add(listItem);
            });
        }
    }

    private Component getErrorLocation(String value, ExpressionParser.ParsingException e) {
        var validValuePart = value.substring(0, e.getCol());
        var invalidValuePart = value.substring(e.getCol());

        var block = new Pre();
        var preformat = new Span(invalidValuePart);
        preformat.getStyle().set(UiStyle.TAG_COLOR, ERROR_COLOR);

        block.add(new Span(validValuePart));
        block.add(preformat);

        var d = new VerticalLayout();
        d.add(block);

        var stringBuilder = new StringBuilder();
        for (var i = 0; i < e.getCol(); i++) { // add spaces to error location
            stringBuilder.append(" ");
        }
        stringBuilder.append("^---- ");
        stringBuilder.append(" Column: ");
        stringBuilder.append(e.getCol());
        stringBuilder.append(" ");
        stringBuilder.append(e.getMessage());
        d.add(new Pre(stringBuilder.toString()));
        // set font family to monospace so locating the parsing error does work
        d.getStyle().set("font-family", "monospace");
        d.setPadding(false);
        d.setMargin(false);
        return d;
    }

    private HtmlContainer convertToHtml(HtmlContainer htmlContainer, Expression exp) {
        if (exp instanceof BinaryOperatorExpression binaryOperatorExpression) {
            Select<TokenType> selectOperator = new Select<>();
            selectOperator.setItems(OPERATORS);
            selectOperator.setValue(binaryOperatorExpression.getOperand());
            selectOperator.setReadOnly(true);
            selectOperator.getStyle().set(UiStyle.TAG_COLOR, OPERATOR_COLOR);

            htmlContainer.add(selectOperator, new Text("("));
            htmlContainer.add(convertToHtml(new Span(), binaryOperatorExpression.getExp1()));
            htmlContainer.add(new Text(","));
            htmlContainer.add(convertToHtml(new Span(), binaryOperatorExpression.getExp2()));
            htmlContainer.add(new Text(")"));
        } else if (exp instanceof MatchExpression matchExpression) {
            Select<TokenType> matchOperator = new Select<>();
            matchOperator.setItems(MATCH_OPERATORS);
            matchOperator.setValue(matchExpression.getMatchOperator());
            matchOperator.setReadOnly(true);
            matchOperator.getStyle().set(UiStyle.TAG_COLOR, OPERATOR_COLOR);

            htmlContainer.add(matchOperator);

            htmlContainer.add(new Text("("));
            Select<TokenType> matchAttribute = new Select<>();
            matchAttribute.setItems(MATCH_ATTRIBUTES);
            matchAttribute.setValue(matchExpression.getAs4Attribute());
            matchAttribute.setReadOnly(true);
            matchAttribute.getStyle().set(UiStyle.TAG_COLOR, AS4ATTRIBUTE_COLOR);

            htmlContainer.add(matchAttribute);

            htmlContainer.add(new Text(", '"));

            // TODO: color switch!

            if (matchExpression.getMatchOperator() == TokenType.EQUALS && USE_SELECT_FIELD.contains(
                matchExpression.getAs4Attribute())) {
                Select<String> as4ValueSelectField = new Select<>();
                as4ValueSelectField.setItems(loadValidItems(matchExpression.getAs4Attribute()));
                as4ValueSelectField.setValue(matchExpression.getValueString());
                as4ValueSelectField.getStyle().set(UiStyle.TAG_COLOR, AS4VALUE_EXISTANT_COLOR);
                as4ValueSelectField.setReadOnly(false);
                htmlContainer.add(as4ValueSelectField);
            } else {
                var as4ValueSelectField = new TextField();
                as4ValueSelectField.setValue(matchExpression.getValueString());
                as4ValueSelectField.getStyle().set(UiStyle.TAG_COLOR, AS4VALUE_EXISTANT_COLOR);
                as4ValueSelectField.setReadOnly(false);
                htmlContainer.add(as4ValueSelectField);
            }

            htmlContainer.add(new Text("')"));
        }
        return htmlContainer;
    }

    private static final List<TokenType> USE_SELECT_FIELD = Stream.of(
        TokenType.AS4_ACTION, TokenType.AS4_SERVICE_NAME, TokenType.AS4_SERVICE_TYPE,
        TokenType.AS4_FROM_PARTY_ID, TokenType.AS4_FROM_PARTY_ID_TYPE, TokenType.AS4_FROM_PARTY_ROLE
    ).toList();

    private List<String> loadValidItems(TokenType as4Attribute) {
        if (as4Attribute == TokenType.AS4_ACTION) {
            return webPModeService.getActionList()
                                  .stream().map(DomibusConnectorAction::getAction).toList();
        } else if (as4Attribute == TokenType.AS4_SERVICE_NAME) {
            return webPModeService.getServiceList()
                                  .stream().map(DomibusConnectorService::getService).toList();
        } else if (as4Attribute == TokenType.AS4_SERVICE_TYPE) {
            return webPModeService.getServiceList()
                                  .stream().map(DomibusConnectorService::getServiceType)
                                  .toList();
        } else if (as4Attribute == TokenType.AS4_FROM_PARTY_ID) {
            return webPModeService.getPartyList()
                                  .stream().map(DomibusConnectorParty::getPartyId).toList();
        } else if (as4Attribute == TokenType.AS4_FROM_PARTY_ID_TYPE) {
            return webPModeService.getPartyList()
                                  .stream().map(DomibusConnectorParty::getPartyIdType)
                                  .toList();
        } else if (as4Attribute == TokenType.AS4_FROM_PARTY_ROLE) {
            return webPModeService.getPartyList()
                                  .stream().map(DomibusConnectorParty::getRole).toList();
        }
        return new ArrayList<>();
    }
}
