/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.ui.view.areas.configuration;

import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The TabMetadata annotation is used to annotate a class that represents the metadata of a tab in
 * an application.
 *
 * <p>The annotation is used to specify the title and tab group of the tab.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@UIScope
@SpringComponent
public @interface TabMetadata {
    /**
     * Returns the title of the tab.
     *
     * @return the title of the tab
     */
    String title();

    /**
     * Returns the tab group of a tab in an application.
     *
     * @return the tab group of the tab
     */
    String tabGroup();
}
