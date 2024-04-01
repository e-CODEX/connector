package eu.domibus.connector.ui.view.areas.configuration.util;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;


public class ConfigurationItemDiv extends Div {
    private static final long serialVersionUID = 1L;

    private final Component configurationItem;

    public ConfigurationItemDiv(
            Component component,
            ConfigurationLabel labels,
            Object initialValue,
            ConfigurationProperties configurationProperties) {
        configurationItem = component;
        configurationItem.setId(labels.PROPERTY_NAME_LABEL);
        add(component);
        Button infoButton = createInfoButton(labels);
        add(infoButton);
        configurationProperties.registerComponent(this.configurationItem, initialValue);
    }

    private Button createInfoButton(ConfigurationLabel labels) {
        Button infoButton = new Button(new Icon(VaadinIcon.INFO_CIRCLE_O));
        Dialog dialog = new Dialog();

        Div headerContent = new Div();
        Label header = new Label(labels.CONFIGURATION_ELEMENT_LABEL);
        header.getStyle().set("font-weight", "bold");
        header.getStyle().set("font-style", "italic");
        headerContent.getStyle().set("text-align", "center");
        headerContent.getStyle().set("padding", "10px");
        headerContent.add(header);
        dialog.add(headerContent);

        Div infoContent = new Div();
        for (String info : labels.INFO_LABEL) {
            Div infoLine = new Div();
            infoLine.add(new Label(info));
            infoContent.add(infoLine);
        }
        infoContent.getStyle().set("padding", "10px");
        dialog.add(infoContent);

        Div propertyContent = new Div();
        Label correspondingProperty = new Label("\n Corresponding property: ");
        correspondingProperty.getStyle().set("font-weight", "bold");
        propertyContent.add(correspondingProperty);
        propertyContent.add(new Label(labels.PROPERTY_NAME_LABEL));
        propertyContent.getStyle().set("padding", "10px");
        dialog.add(propertyContent);

        Div closeButtonContent = new Div();
        closeButtonContent.getStyle().set("text-align", "center");
        Button closeButton = new Button("Close", event -> {
            dialog.close();
        });
        closeButtonContent.add(closeButton);
        closeButtonContent.getStyle().set("padding", "10px");
        dialog.add(closeButtonContent);

        infoButton.addClickListener(event -> dialog.open());
        return infoButton;
    }

    public Component getConfigurationItem() {
        return configurationItem;
    }
}
