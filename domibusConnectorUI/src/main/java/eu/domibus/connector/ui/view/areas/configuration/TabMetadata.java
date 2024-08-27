/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
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
