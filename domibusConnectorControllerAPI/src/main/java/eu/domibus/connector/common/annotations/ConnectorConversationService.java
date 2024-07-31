package eu.domibus.connector.common.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Represents a custom annotation used to qualify a ConversionService bean in the Domibus
 * Connector module.
 * It is used to specify the value of the annotation as
 * "eu.domibus.connector.common.annotations.ConnectorConversionService".
 */
@Qualifier(ConnectorConversationService.VALUE)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ConnectorConversationService {
    String VALUE = "eu.domibus.connector.common.annotations.ConnectorConversionService";
}


