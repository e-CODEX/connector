/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.ui.utils;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The RoleRequired annotation is used to specify the role required to access a particular class or
 * annotation. This annotation can be applied to target types (classes, interfaces, enums) or other
 * annotations.
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface RoleRequired {
    /**
     * Returns the role required to access a particular class or annotation.
     *
     * @return the role required to access a particular class or annotation
     */
    String role();
}
