package eu.domibus.connector.link.service;

import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.annotation.*;

import static eu.domibus.connector.link.service.LinkPluginQualifier.LINK_QUALIFIER_NAME;


@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Qualifier(LINK_QUALIFIER_NAME)
public @interface LinkPluginQualifier {
    String LINK_QUALIFIER_NAME = "eu.domibus.connector.link.service.LinkPluginQualifier";
}
