package eu.domibus.connector.ui.utils;

import java.lang.annotation.*;


@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface RoleRequired {
    String role();
}
