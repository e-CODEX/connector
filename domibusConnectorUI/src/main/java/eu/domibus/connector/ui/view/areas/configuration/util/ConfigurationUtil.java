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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Properties;


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

    public String getPropertyValue(String propertyName) {
        String propertyValue = env.getProperty(propertyName);
        if (propertyValue == null) {
            //			throw new RuntimeException(String.format("Did not found property with name [%s] in
            //			environment", propertyName));
            return "";
        }
        return propertyValue;
    }

    public Grid<CertificateInfo> createKeystoreInformationGrid(
            TextField keyStorePathField,
            TextField keyStorePasswordField) {
        if (keyStorePathField.getValue() != null && !keyStorePasswordField.getValue().isEmpty()) {
            List<CertificateInfo> evidencesKeyStore = keystoreService.loadStoreCertificatesInformation(
                    keyStorePathField.getValue(),
                    keyStorePasswordField.getValue()
            );
            Grid<CertificateInfo> grid = createKeystoreInformationGrid();
            grid.setItems(evidencesKeyStore);
            return grid;
        }

        return null;
    }

    public List<CertificateInfo> getKeystoreInformation(InputStream is, String password) {
        List<CertificateInfo> keyStore = keystoreService.loadStoreCertificatesInformation(is, password);

        return keyStore;
    }

    public Grid<CertificateInfo> createKeystoreInformationGrid() {
        Grid<CertificateInfo> grid = new Grid<>();
        grid.addColumn(CertificateInfo::getAlias).setHeader("Alias").setWidth("200px");
        grid.addColumn(CertificateInfo::getSubject).setHeader("Subject").setWidth("300px");
        grid.addColumn(CertificateInfo::getIssuer).setHeader("Issuer").setWidth("300px");
        grid.addColumn(CertificateInfo::getNotBefore).setHeader("Valid from").setWidth("300px");
        grid.addColumn(CertificateInfo::getNotAfter).setHeader("Valid until").setWidth("300px");
        grid.addColumn(CertificateInfo::getAlgorithm).setHeader("Algorithm").setWidth("200px");
        grid.addColumn(CertificateInfo::getType).setHeader("Type").setWidth("200px");

        grid.setWidth("1800px");
        //		grid.setHeight("500px");
        grid.setMultiSort(true);

        for (Column<CertificateInfo> col : grid.getColumns()) {
            col.setSortable(true);
            col.setResizable(true);
        }
        return grid;
    }

    public Div createConfigurationItemDiv(
            ConfigurationLabel labels,
            com.vaadin.flow.component.Component c,
            Object initialValue) {
        return new ConfigurationItemDiv(c, labels, initialValue, configurationProperties);
    }

    public Div createConfigurationItemCheckboxDivWithValue(
            ConfigurationLabel labels,
            LumoCheckbox configCheckbox,
            Boolean initialValue) {
        ConfigurationItemCheckboxDiv checkboxDiv =
                new ConfigurationItemCheckboxDiv(configCheckbox, labels, initialValue, configurationProperties);
        return checkboxDiv;
    }

    public Div createConfigurationItemCheckboxDiv(ConfigurationLabel labels, LumoCheckbox configCheckbox) {
        ConfigurationItemCheckboxDiv checkboxDiv = new ConfigurationItemCheckboxDiv(
                configCheckbox,
                labels,
                Boolean.valueOf(getPropertyValue(
                        labels.PROPERTY_NAME_LABEL)),
                configurationProperties
        );
        return checkboxDiv;
    }

    public Div createConfigurationItemComboBoxDivEmpty(
            ConfigurationLabel labels,
            ComboBox<String> configComboBox,
            Collection<String> items) {
        ConfigurationItemComboBoxDiv comboBoxDiv =
                new ConfigurationItemComboBoxDiv(configComboBox, labels, items, "", configurationProperties);
        return comboBoxDiv;
    }

    public Div createConfigurationItemComboBoxDiv(
            ConfigurationLabel labels,
            ComboBox<String> configComboBox,
            Collection<String> items) {
        String val = getPropertyValue(labels.PROPERTY_NAME_LABEL);
        // check if initialValue is part of the configured services...
        if (!items.contains(val)) {
            items.add(val);
        }
        ConfigurationItemComboBoxDiv comboBoxDiv =
                new ConfigurationItemComboBoxDiv(configComboBox, labels, items, val, configurationProperties);
        return comboBoxDiv;
    }

    public Div createConfigurationItemTextFieldDivEmpty(ConfigurationLabel labels, TextField configTextField) {
        ConfigurationItemTextFieldDiv textFieldDiv =
                new ConfigurationItemTextFieldDiv(configTextField, labels, "", configurationProperties);
        return textFieldDiv;
    }

    public Div createConfigurationItemTextFieldDiv(ConfigurationLabel labels, TextField configTextField) {
        ConfigurationItemTextFieldDiv textFieldDiv = new ConfigurationItemTextFieldDiv(
                configTextField,
                labels,
                getPropertyValue(labels.PROPERTY_NAME_LABEL),
                configurationProperties
        );
        return textFieldDiv;
    }

    public void reloadConfiguration() {
        for (String componentId : configurationProperties.getConfigurationcomponents().keySet()) {
            com.vaadin.flow.component.Component c =
                    configurationProperties.getConfigurationcomponents().get(componentId);
            String contextValue = getPropertyValue(componentId);
            String componentValue = null;
            if (c instanceof ComboBox<?>) {
                componentValue = ((ComboBox<String>) c).getValue();
                if (componentValue != null && contextValue != null && !componentValue.equals(contextValue))
                    ((ComboBox<String>) c).setValue(contextValue);
            } else if (c instanceof Checkbox) {
                componentValue = ((Checkbox) c).getValue().toString().toLowerCase();
                if (componentValue != null && contextValue != null && !componentValue.equals(contextValue.toLowerCase()))
                    ((Checkbox) c).setValue(Boolean.valueOf(contextValue));
            } else if (c instanceof TextField) {
                componentValue = ((TextField) c).getValue();
                if (componentValue != null && contextValue != null && !componentValue.equals(contextValue))
                    ((TextField) c).setValue(contextValue);
            }
        }
        propertiesPersistenceService.resetProperties(configurationProperties.getProperties());
        configurationProperties.clearChanges();
    }

    public void resetConfiguration() {
        for (String componentId : configurationProperties.getChangedcomponents().keySet()) {
            com.vaadin.flow.component.Component c = configurationProperties.getChangedcomponents().get(componentId);
            Object initialValue = configurationProperties.getInitialproperties().get(componentId);
            if (c instanceof ComboBox<?>) {
                ((ComboBox<String>) c).setValue((String) initialValue);
            } else if (c instanceof Checkbox) {
                ((Checkbox) c).setValue((Boolean) initialValue);
            } else if (c instanceof TextField) {
                ((TextField) c).setValue(getPropertyValue((String) initialValue));
            }
        }
        configurationProperties.clearChanges();
    }

    public void saveConfiguration() {
        propertiesPersistenceService.saveProperties(configurationProperties.getProperties());
        for (String componentId : configurationProperties.getChangedcomponents().keySet()) {
            com.vaadin.flow.component.Component c = configurationProperties.getChangedcomponents().get(componentId);
            Object value = null;
            if (c instanceof ComboBox<?>) {
                value = ((ComboBox<String>) c).getValue();
            } else if (c instanceof Checkbox) {
                value = ((Checkbox) c).getValue();
            } else if (c instanceof TextField) {
                value = ((TextField) c).getValue();
            }
            if (value != null)
                configurationProperties.getInitialproperties().put(componentId, value);
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
