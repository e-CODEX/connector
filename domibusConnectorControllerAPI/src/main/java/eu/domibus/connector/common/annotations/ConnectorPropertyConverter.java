package eu.domibus.connector.common.annotations;

import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.annotation.*;


@Qualifier(ConnectorPropertyConverter.VALUE)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ConnectorPropertyConverter {
    String VALUE = "eu.domibus.connector.common.annotations.ConnectorPropertyConverter";
}
