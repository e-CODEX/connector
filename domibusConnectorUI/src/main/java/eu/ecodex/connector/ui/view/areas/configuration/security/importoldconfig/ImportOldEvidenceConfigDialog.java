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
import eu.ecodex.connector.evidences.spring.EvidencesToolkitConfigurationProperties;
import eu.ecodex.connector.ui.view.areas.configuration.ConfigurationPanelFactory;
import eu.ecodex.connector.ui.view.areas.configuration.evidences.EvidencesToolkitConfigurationPropertiesForm;
import java.util.Map;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * This class represents a dialog that allows the user to import old evidence configuration.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Lazy
public class ImportOldEvidenceConfigDialog extends AImportOldConfigDialog {
    private final ObjectProvider<EvidencesToolkitConfigurationPropertiesForm> formFactory;

    /**
     * Constructor.
     *
     * @param configurationPanelFactory The factory used to create and manage configuration panels.
     * @param formFactory               The provider of EvidencesToolkitConfigurationPropertiesForm
     *                                  instances.
     */
    public ImportOldEvidenceConfigDialog(
        ConfigurationPanelFactory configurationPanelFactory,
        ObjectProvider<EvidencesToolkitConfigurationPropertiesForm> formFactory) {
        super(configurationPanelFactory);
        this.formFactory = formFactory;
    }

    @Override
    protected Object showImportedConfig(Div div, Map<String, String> p) {
        var oldConfigMapper = new OldConfigMapper(p);
        var properties = oldConfigMapper.migrateEvidencesToolkitConfig();

        var b = new Binder<>(EvidencesToolkitConfigurationProperties.class);
        var form = formFactory.getIfAvailable();
        b.bindInstanceFields(form);
        b.setBean(properties);
        b.setReadOnly(true);

        div.add(form);

        return properties;
    }
}

