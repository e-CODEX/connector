package eu.domibus.connector.common.annotations;

import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.annotation.*;


@Qualifier(ConnectorConversationService.VALUE)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ConnectorConversationService {
    String VALUE = "eu.domibus.connector.common.annotations.ConnectorConversionService";
}
