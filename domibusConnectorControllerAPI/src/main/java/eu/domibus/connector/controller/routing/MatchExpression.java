package eu.domibus.connector.controller.routing;

public abstract class MatchExpression extends Expression {
    public abstract TokenType getMatchOperator();

    public abstract TokenType getAs4Attribute();

    public abstract String getValueString();
}
