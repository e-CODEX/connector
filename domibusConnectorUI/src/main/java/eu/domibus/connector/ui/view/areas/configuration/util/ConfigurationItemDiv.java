/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.ui.view.areas.configuration.util;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import eu.domibus.connector.ui.utils.UiStyle;
import lombok.Data;

/**
 * The ConfigurationItemDiv class is a subclass of Div that represents a configuration item. It
 * consists of a component representing the configuration item and an info button that opens a
 * dialog with additional information about the configuration item.
 */
@Data
public class ConfigurationItemDiv extends Div {
    private static final long serialVersionUID = 1L;
    private final Component configurationItem;

    /**
     * Constructor.
     *
     * @param component               the component representing the configuration item
     * @param labels                  the configuration label and other related labels
     * @param initialValue            the initial value of the configuration item
     * @param configurationProperties the configuration properties object to track changes and
     *                                update values
     */
    public ConfigurationItemDiv(
        Component component, ConfigurationLabel labels, Object initialValue,
        ConfigurationProperties configurationProperties) {
        configurationItem = component;
        configurationItem.setId(labels.PROPERTY_NAME_LABEL);
        add(component);
        var infoButton = createInfoButton(labels);
        add(infoButton);
        configurationProperties.registerComponent(this.configurationItem, initialValue);
    }

    private Button createInfoButton(ConfigurationLabel labels) {
        var headerContent = new Div();
        var header = new Label(labels.CONFIGURATION_ELEMENT_LABEL);
        header.getStyle().set("font-weight", "bold");
        header.getStyle().set("font-style", "italic");
        headerContent.getStyle().set(UiStyle.ALIGNMENT_STYLE, UiStyle.ALIGNMENT_CENTER);
        headerContent.getStyle().set(UiStyle.TAG_PADDING, "10px");
        headerContent.add(header);

        var dialog = new Dialog();
        dialog.add(headerContent);

        var infoContent = new Div();
        for (var info : labels.INFO_LABEL) {
            var infoLine = new Div();
            infoLine.add(new Label(info));
            infoContent.add(infoLine);
        }
        infoContent.getStyle().set(UiStyle.TAG_PADDING, "10px");
        dialog.add(infoContent);

        var propertyContent = new Div();
        var correspondingProperty = new Label("\n Corresponding property: ");
        correspondingProperty.getStyle().set("font-weight", "bold");
        propertyContent.add(correspondingProperty);
        propertyContent.add(new Label(labels.PROPERTY_NAME_LABEL));
        propertyContent.getStyle().set(UiStyle.TAG_PADDING, "10px");
        dialog.add(propertyContent);

        var closeButtonContent = new Div();
        closeButtonContent.getStyle().set(UiStyle.ALIGNMENT_STYLE, UiStyle.ALIGNMENT_CENTER);
        var closeButton = new Button("Close", event -> dialog.close());
        closeButtonContent.add(closeButton);
        closeButtonContent.getStyle().set(UiStyle.TAG_PADDING, "10px");
        dialog.add(closeButtonContent);

        var infoButton = new Button(new Icon(VaadinIcon.INFO_CIRCLE_O));
        infoButton.addClickListener(event -> dialog.open());
        return infoButton;
    }
}
