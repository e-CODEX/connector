package eu.domibus.connector.ui.view.areas.configuration;

import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

import java.lang.annotation.*;


@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@UIScope
@SpringComponent
public @interface TabMetadata {
    String title();

    String tabGroup();
}
