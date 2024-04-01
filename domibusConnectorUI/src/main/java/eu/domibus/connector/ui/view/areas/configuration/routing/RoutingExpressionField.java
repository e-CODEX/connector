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
import eu.domibus.connector.controller.routing.*;
import eu.domibus.connector.ui.service.WebPModeService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class RoutingExpressionField extends CustomField<RoutingRulePattern> {
    public static final String ERROR_COLOR = "#ff0000";
    public static final String OPERATOR_COLOR = "#0000ff"; // blue ?
    public static final String AS4ATTRIBUTE_COLOR = "#000000"; //
    public static final String AS4VALUE_EXISTANT_COLOR = "#00ff00"; // green
    public static final String AS4VALUE_NONEXISTANT_COLOR = "#ffff00"; // yellow

    public static final List<TokenType> OPERATORS = Stream
            .of(TokenType.OR, TokenType.AND)
            .collect(Collectors.toList());
    public static final List<TokenType> MATCH_OPERATORS = Stream
            .of(TokenType.STARTSWITH, TokenType.EQUALS)
            .collect(Collectors.toList());
    public static final List<TokenType> MATCH_ATTRIBUTES = Stream
            .of(TokenType.AS4_FINAL_RECIPIENT, TokenType.AS4_ACTION,
                TokenType.AS4_FROM_PARTY_ID, TokenType.AS4_FROM_PARTY_ROLE,
                TokenType.AS4_FROM_PARTY_ID_TYPE,
                TokenType.AS4_SERVICE_NAME, TokenType.AS4_SERVICE_TYPE, TokenType.AS4_ACTION
            )
            .collect(Collectors.toList());
    private static final List<TokenType> USE_SELECT_FIELD = Stream.of(
            TokenType.AS4_ACTION, TokenType.AS4_SERVICE_NAME, TokenType.AS4_SERVICE_TYPE,
            TokenType.AS4_FROM_PARTY_ID, TokenType.AS4_FROM_PARTY_ID_TYPE, TokenType.AS4_FROM_PARTY_ROLE
    ).collect(Collectors.toList());

    private final WebPModeService webPModeService;
    private final VerticalLayout layout = new VerticalLayout();
    private final TextField tf = new TextField();
    private RoutingRulePattern value;
    private final Div routingExpressionField = new Div();
    private final VerticalLayout errorList = new VerticalLayout();

    public RoutingExpressionField(WebPModeService webPModeService) {
        this.webPModeService = webPModeService;
        //        this.eff = eff;
        this.add(layout);
        layout.setPadding(false);
        layout.setMargin(false);

        tf.setWidth("15cm");
        tf.setWidthFull();
        tf.addValueChangeListener(this::tfValueChanged);

        layout.add(tf);
        layout.add(errorList);
        errorList.getStyle().set("color", ERROR_COLOR);
        layout.add(routingExpressionField);
    }

    @Override
    protected RoutingRulePattern generateModelValue() {

        // TODO: read value convert to RoutingRulePattern..

        return value;
    }

    @Override
    protected void setPresentationValue(RoutingRulePattern routingRulePattern) {
        // value.getMatchClause().getExpression();
        Expression exp = routingRulePattern.getExpression();

        tf.setValue(exp.toString());
    }

    private void tfValueChanged(ComponentValueChangeEvent<TextField, String> textFieldStringComponentValueChangeEvent) {
        String value = textFieldStringComponentValueChangeEvent.getValue();

        ExpressionParser expressionParser = new ExpressionParser(value);
        errorList.removeAll();
        if (expressionParser.getParsedExpression().isPresent()) {
            tf.setInvalid(false);
            RoutingRulePattern rr = new RoutingRulePattern(value);
            this.value = rr;
            this.setModelValue(rr, true);
        } else {
            tf.setInvalid(true);
            expressionParser.getParsingExceptions().stream().forEach(e -> {
                Div listItem = new Div();
                //                listItem.add(e.getMessage());
                listItem.add(getErrorLocation(value, e));
                errorList.add(listItem);
            });
        }
    }

    private Component getErrorLocation(String value, ExpressionParser.ParsingException e) {
        VerticalLayout d = new VerticalLayout();

        String validValuePart = value.substring(0, e.getCol());
        String invalidValuePart = value.substring(e.getCol());

        Pre block = new Pre();
        Span preformat = new Span(invalidValuePart);
        preformat.getStyle().set("color", ERROR_COLOR);

        block.add(new Span(validValuePart));
        block.add(preformat);

        d.add(block);

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < e.getCol(); i++) { // add spaces to error location
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
        if (exp instanceof BinaryOperatorExpression) {
            BinaryOperatorExpression binaryExp = ((BinaryOperatorExpression) exp);

            Select<TokenType> selectOperator = new Select<>();
            selectOperator.setItems(OPERATORS);
            selectOperator.setValue(binaryExp.getOperand());
            selectOperator.setReadOnly(true);
            selectOperator.getStyle().set("color", OPERATOR_COLOR);

            htmlContainer.add(selectOperator, new Text("("));
            htmlContainer.add(convertToHtml(new Span(), binaryExp.getExp1()));
            htmlContainer.add(new Text(","));
            htmlContainer.add(convertToHtml(new Span(), binaryExp.getExp2()));
            htmlContainer.add(new Text(")"));
        } else if (exp instanceof MatchExpression) {
            MatchExpression matchExpression = (MatchExpression) exp;

            // build colored html with: <operator>(<as4Attribute>, '<as4valueString>')
            //            Span as4MatchOperator = new Span(matchExpression.getMatchOperator().toString());
            //            as4MatchOperator.getStyle().set("color", OPERATOR_COLOR);
            //            htmlContainer.add(as4MatchOperator);

            Select<TokenType> matchOperator = new Select<>();
            matchOperator.setItems(MATCH_OPERATORS);
            matchOperator.setValue(matchExpression.getMatchOperator());
            matchOperator.setReadOnly(true);
            matchOperator.getStyle().set("color", OPERATOR_COLOR);

            htmlContainer.add(matchOperator);

            htmlContainer.add(new Text("("));

            //            Span matchExp = new Span(matchExpression.getAs4Attribute().toString());
            //            matchExp.getStyle().set("color", AS4ATTRIBUTE_COLOR);
            //            htmlContainer.add(matchExp);
            Select<TokenType> matchAttribute = new Select<>();
            matchAttribute.setItems(MATCH_ATTRIBUTES);
            matchAttribute.setValue(matchExpression.getAs4Attribute());
            matchAttribute.setReadOnly(true);
            matchAttribute.getStyle().set("color", AS4ATTRIBUTE_COLOR);

            htmlContainer.add(matchAttribute);

            htmlContainer.add(new Text(", '"));

            //            Span as4MatchValue = new Span(matchExpression.getValueString());
            // TODO: color switch!

            if (matchExpression.getMatchOperator() == TokenType.EQUALS && USE_SELECT_FIELD.contains(matchExpression.getAs4Attribute())) {
                Select<String> as4ValueSelectField = new Select<>();
                as4ValueSelectField.setItems(loadValidItems(matchExpression.getAs4Attribute()));
                as4ValueSelectField.setValue(matchExpression.getValueString());
                as4ValueSelectField.getStyle().set("color", AS4VALUE_EXISTANT_COLOR);
                as4ValueSelectField.setReadOnly(false);
                htmlContainer.add(as4ValueSelectField);
            } else {
                TextField as4ValueSelectField = new TextField();
                as4ValueSelectField.setValue(matchExpression.getValueString());
                as4ValueSelectField.getStyle().set("color", AS4VALUE_EXISTANT_COLOR);
                as4ValueSelectField.setReadOnly(false);
                htmlContainer.add(as4ValueSelectField);
            }

            htmlContainer.add(new Text("')"));
        }
        return htmlContainer;
    }

    private List<String> loadValidItems(TokenType as4Attribute) {
        if (as4Attribute == TokenType.AS4_ACTION) {
            return webPModeService.getActionList()
                                  .stream().map(a -> a.getAction()).collect(Collectors.toList());
        } else if (as4Attribute == TokenType.AS4_SERVICE_NAME) {
            return webPModeService.getServiceList()
                                  .stream().map(s -> s.getService()).collect(Collectors.toList());
        } else if (as4Attribute == TokenType.AS4_SERVICE_TYPE) {
            return webPModeService.getServiceList()
                                  .stream().map(s -> s.getServiceType()).collect(Collectors.toList());
        } else if (as4Attribute == TokenType.AS4_FROM_PARTY_ID) {
            return webPModeService.getPartyList()
                                  .stream().map(p -> p.getPartyId()).collect(Collectors.toList());
        } else if (as4Attribute == TokenType.AS4_FROM_PARTY_ID_TYPE) {
            return webPModeService.getPartyList()
                                  .stream().map(p -> p.getPartyIdType()).collect(Collectors.toList());
        } else if (as4Attribute == TokenType.AS4_FROM_PARTY_ROLE) {
            return webPModeService.getPartyList()
                                  .stream().map(p -> p.getRole()).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
