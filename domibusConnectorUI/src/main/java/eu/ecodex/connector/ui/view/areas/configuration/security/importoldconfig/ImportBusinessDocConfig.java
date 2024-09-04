/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.ui.view.areas.configuration.security.importoldconfig;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.data.binder.Binder;
import eu.ecodex.connector.security.configuration.DCBusinessDocumentValidationConfigurationProperties;
import eu.ecodex.connector.ui.view.areas.configuration.ConfigurationPanelFactory;
import eu.ecodex.connector.ui.view.areas.configuration.security.BusinessDocumentValidationConfigForm;
import java.util.Map;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * This class represents the configuration for importing business documents.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Lazy
public class ImportBusinessDocConfig extends AImportOldConfigDialog {
    private final ObjectProvider<BusinessDocumentValidationConfigForm> formFactory;

    /**
     * Constructor.
     *
     * @param configurationPanelFactory The factory used to create and manage configuration panels.
     * @param formFactory               The factory used to create instances of
     *                                  BusinessDocumentValidationConfigForm.
     */
    public ImportBusinessDocConfig(
        ConfigurationPanelFactory configurationPanelFactory,
        ObjectProvider<BusinessDocumentValidationConfigForm> formFactory) {
        super(configurationPanelFactory);
        this.formFactory = formFactory;
    }

    @Override
    protected Object showImportedConfig(Div div, Map<String, String> p) {
        var oldConfigMapper = new OldConfigMapper(p);
        var properties = oldConfigMapper.migrateBusinessDocumentConfigurationProperties();

        var b = new Binder<>(DCBusinessDocumentValidationConfigurationProperties.class);
        BusinessDocumentValidationConfigForm form = formFactory.getIfAvailable();
        b.bindInstanceFields(form);
        b.setBean(properties);
        b.setReadOnly(true);

        div.add(form);

        return properties;
    }
}
