/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.ui.view.areas.configuration.util;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.TextField;
import eu.domibus.connector.persistence.service.DomibusConnectorPropertiesPersistenceService;
import eu.domibus.connector.ui.component.LumoCheckbox;
import eu.domibus.connector.ui.enums.UserRole;
import eu.domibus.connector.ui.service.WebKeystoreService;
import eu.domibus.connector.ui.service.WebKeystoreService.CertificateInfo;
import eu.domibus.connector.ui.utils.UiStyle;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * This class provides utility methods for retrieving property values, manipulating grid components,
 * and creating configuration item widgets. It is used to interact with the configuration
 * properties, environment, and keystore service in the application.
 */
@Component
public class ConfigurationUtil {
    @Autowired
    Environment env;
    @Autowired
    DomibusConnectorPropertiesPersistenceService propertiesPersistenceService;
    @Autowired
    ConfigurationProperties configurationProperties;
    @Autowired
    WebKeystoreService keystoreService;

    /**
     * Retrieves the value of the specified property.
     *
     * @param propertyName the name of the property to retrieve
     * @return the value of the property, or an empty string if the property doesn't exist
     */
    public String getPropertyValue(String propertyName) {
        String propertyValue = env.getProperty(propertyName);
        if (propertyValue == null) {
            return "";
        }
        return propertyValue;
    }

    /**
     * Retrieves the information of the certificates stored in a keystore.
     *
     * @param is       the input stream representing the keystore
     * @param password the password to access the keystore
     * @return a list of CertificateInfo objects representing the certificates in the keystore
     */
    public List<CertificateInfo> getKeystoreInformation(InputStream is, String password) {
        return keystoreService.loadStoreCertificatesInformation(is, password);
    }

    /**
     * Creates a grid component for displaying a list of {@link CertificateInfo}. The grid will have
     * columns for the alias, subject, issuer, validity period, algorithm, and type of the
     * certificates.
     *
     * @return a {@link Grid} component for displaying {@link CertificateInfo}
     */
    public Grid<CertificateInfo> createKeystoreInformationGrid() {
        var grid = new Grid<CertificateInfo>();

        grid.addColumn(CertificateInfo::getAlias).setHeader("Alias").setWidth(UiStyle.WIDTH_200_PX);
        grid.addColumn(CertificateInfo::getSubject).setHeader("Subject")
            .setWidth(UiStyle.WIDTH_300_PX);
        grid.addColumn(CertificateInfo::getIssuer).setHeader("Issuer")
            .setWidth(UiStyle.WIDTH_300_PX);
        grid.addColumn(CertificateInfo::getNotBefore).setHeader("Valid from")
            .setWidth(UiStyle.WIDTH_300_PX);
        grid.addColumn(CertificateInfo::getNotAfter).setHeader("Valid until")
            .setWidth(UiStyle.WIDTH_300_PX);
        grid.addColumn(CertificateInfo::getAlgorithm).setHeader("Algorithm")
            .setWidth(UiStyle.WIDTH_200_PX);
        grid.addColumn(CertificateInfo::getType).setHeader("Type").setWidth(UiStyle.WIDTH_200_PX);

        grid.setWidth("1800px");
        grid.setMultiSort(true);

        for (Column<CertificateInfo> col : grid.getColumns()) {
            col.setSortable(true);
            col.setResizable(true);
        }
        return grid;
    }

    /**
     * Creates a grid component for displaying the information of certificates stored in a
     * keystore.
     *
     * @param keyStorePathField     a TextField representing the path of the keystore file
     * @param keyStorePasswordField a TextField representing the password to access the keystore
     * @return a Grid component that displays the information of certificates in the keystore, or
     *      null if the keyStorePathField value is null or the keyStorePasswordField value is empty
     */
    public Grid<CertificateInfo> createKeystoreInformationGrid(
        TextField keyStorePathField, TextField keyStorePasswordField) {
        if (keyStorePathField.getValue() != null && !keyStorePasswordField.getValue().isEmpty()) {
            var evidencesKeyStore =
                keystoreService.loadStoreCertificatesInformation(
                    keyStorePathField.getValue(),
                    keyStorePasswordField.getValue()
                );
            var grid = createKeystoreInformationGrid();
            grid.setItems(evidencesKeyStore);
            return grid;
        }

        return null;
    }

    /**
     * Creates a {@link Div} component representing a configuration item with a given initial
     * value.
     *
     * @param labels       the configuration label and other related labels
     * @param c            the component representing the configuration item
     * @param initialValue the initial value of the configuration item
     * @return a {@link Div} component representing the configuration item
     */
    public Div createConfigurationItemDiv(
        ConfigurationLabel labels, com.vaadin.flow.component.Component c, Object initialValue) {
        return new ConfigurationItemDiv(c, labels, initialValue, configurationProperties);
    }

    /**
     * Creates a {@link Div} component representing a configuration item checkbox with a given
     * initial value.
     *
     * @param labels         the configuration label and other related labels
     * @param configCheckbox the LumoCheckbox component representing the configuration item
     * @param initialValue   the initial value of the checkbox
     * @return a {@link Div} component representing the configuration item checkbox
     */
    public Div createConfigurationItemCheckboxDivWithValue(
        ConfigurationLabel labels, LumoCheckbox configCheckbox, Boolean initialValue) {
        return new ConfigurationItemCheckboxDiv(
            configCheckbox,
            labels,
            initialValue,
            configurationProperties
        );
    }

    /**
     * Creates a {@link Div} component representing a configuration item with a checkbox.
     *
     * @param labels         the configuration label and other related labels
     * @param configCheckbox the LumoCheckbox component representing the configuration item
     * @return a {@link Div} component representing the configuration item
     */
    public Div createConfigurationItemCheckboxDiv(
        ConfigurationLabel labels, LumoCheckbox configCheckbox) {
        return new ConfigurationItemCheckboxDiv(
            configCheckbox,
            labels,
            Boolean.valueOf(getPropertyValue(labels.PROPERTY_NAME_LABEL)), configurationProperties
        );
    }

    /**
     * Creates a {@link Div} component representing a configuration item with an empty ComboBox.
     *
     * @param labels         the configuration label and other related labels
     * @param configComboBox the ComboBox component representing the configuration item
     * @param items          the collection of items to be displayed in the ComboBox
     * @return a {@link Div} component representing the configuration item
     */
    public Div createConfigurationItemComboBoxDivEmpty(
        ConfigurationLabel labels, ComboBox<String> configComboBox, Collection<String> items) {
        return new ConfigurationItemComboBoxDiv(
            configComboBox,
            labels,
            items,
            "",
            configurationProperties
        );
    }

    /**
     * Creates a {@link Div} component representing a configuration item with a ComboBox.
     *
     * @param labels         the configuration label and other related labels
     * @param configComboBox the ComboBox component representing the configuration item
     * @param items          the collection of items to be displayed in the ComboBox
     * @return a {@link Div} component representing the configuration item
     */
    public Div createConfigurationItemComboBoxDiv(
        ConfigurationLabel labels, ComboBox<String> configComboBox, Collection<String> items) {
        String val = getPropertyValue(labels.PROPERTY_NAME_LABEL);
        // check if initialValue is part of the configured services...
        if (!items.contains(val)) {
            items.add(val);
        }
        return new ConfigurationItemComboBoxDiv(
            configComboBox,
            labels,
            items,
            val,
            configurationProperties
        );
    }

    /**
     * Creates a {@link Div} component representing a configuration item with an empty text field.
     *
     * @param labels          the configuration label and other related labels
     * @param configTextField the text field component representing the configuration item
     * @return a {@link Div} component representing the configuration item
     */
    public Div createConfigurationItemTextFieldDivEmpty(
        ConfigurationLabel labels, TextField configTextField) {
        return new ConfigurationItemTextFieldDiv(
            configTextField,
            labels,
            "",
            configurationProperties
        );
    }

    /**
     * Creates a {@link Div} component that represents a configuration item with a text field.
     *
     * @param labels          the configuration label and other related labels
     * @param configTextField the text field component representing the configuration item
     * @return a {@link Div} component representing the configuration item
     */
    public Div createConfigurationItemTextFieldDiv(
        ConfigurationLabel labels, TextField configTextField) {
        return new ConfigurationItemTextFieldDiv(
            configTextField,
            labels,
            getPropertyValue(labels.PROPERTY_NAME_LABEL),
            configurationProperties
        );
    }

    /**
     * Reloads the configuration properties to their initial state.
     */
    public void reloadConfiguration() {
        for (var componentId : configurationProperties.getConfigurationcomponents().keySet()) {
            com.vaadin.flow.component.Component c =
                configurationProperties.getConfigurationcomponents().get(componentId);
            var contextValue = getPropertyValue(componentId);
            String componentValue;
            if (c instanceof ComboBox<?>) {
                componentValue = ((ComboBox<String>) c).getValue();
                if (componentValue != null && contextValue != null && !componentValue.equals(
                    contextValue)) {
                    ((ComboBox<String>) c).setValue(contextValue);
                }
            } else if (c instanceof Checkbox checkbox) {
                componentValue = checkbox.getValue().toString().toLowerCase();
                if (componentValue != null && contextValue != null && !componentValue.equals(
                    contextValue.toLowerCase())) {
                    ((Checkbox) c).setValue(Boolean.valueOf(contextValue));
                }
            } else if (c instanceof TextField textField) {
                componentValue = textField.getValue();
                if (componentValue != null && contextValue != null && !componentValue.equals(
                    contextValue)) {
                    ((TextField) c).setValue(contextValue);
                }
            }
        }
        propertiesPersistenceService.resetProperties(configurationProperties.getProperties());
        configurationProperties.clearChanges();
    }

    /**
     * Resets the configuration properties of the ConfigurationUtil class.
     */
    public void resetConfiguration() {
        for (String componentId : configurationProperties.getChangedcomponents().keySet()) {
            com.vaadin.flow.component.Component c =
                configurationProperties.getChangedcomponents().get(componentId);
            Object initialValue = configurationProperties.getInitialproperties().get(componentId);
            if (c instanceof ComboBox<?>) {
                ((ComboBox<String>) c).setValue((String) initialValue);
            } else if (c instanceof Checkbox checkbox) {
                checkbox.setValue((Boolean) initialValue);
            } else if (c instanceof TextField textField) {
                textField.setValue(getPropertyValue((String) initialValue));
            }
        }
        configurationProperties.clearChanges();
    }

    /**
     * Saves the configuration properties.
     *
     * <p>This method saves the configuration properties using the properties persistence service.
     * It iterates through the changed components and checks their types to retrieve the updated
     * values.
     *
     * @see DomibusConnectorPropertiesPersistenceService#saveProperties(Properties)
     */
    public void saveConfiguration() {
        propertiesPersistenceService.saveProperties(configurationProperties.getProperties());
        for (String componentId : configurationProperties.getChangedcomponents().keySet()) {
            com.vaadin.flow.component.Component c =
                configurationProperties.getChangedcomponents().get(componentId);
            Object value = null;
            if (c instanceof ComboBox<?>) {
                value = ((ComboBox<String>) c).getValue();
            } else if (c instanceof Checkbox checkbox) {
                value = checkbox.getValue();
            } else if (c instanceof TextField textField) {
                value = textField.getValue();
            }
            if (value != null) {
                configurationProperties.getInitialproperties().put(componentId, value);
            }
        }
        configurationProperties.clearChanges();
    }

    public void updateOnRole(UserRole role) {
        configurationProperties.updateOnRole(role);
    }

    public void unregisterComponent(com.vaadin.flow.component.Component c) {
        configurationProperties.unregisterComponent(c);
    }

    public void updateConfigurationComponentsOnProperties(Properties properties) {
        configurationProperties.updateConfigurationComponentsOnProperties(properties);
    }
}
