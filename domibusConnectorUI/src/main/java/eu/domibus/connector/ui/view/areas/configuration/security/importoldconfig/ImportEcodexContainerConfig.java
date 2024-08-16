/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.ui.view.areas.configuration.security.importoldconfig;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.data.binder.Binder;
import eu.domibus.connector.security.configuration.DCEcodexContainerProperties;
import eu.domibus.connector.ui.view.areas.configuration.ConfigurationPanelFactory;
import eu.domibus.connector.ui.view.areas.configuration.security.EcxContainerConfigForm;
import java.util.Map;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * The ImportEcodexContainerConfig class represents a component responsible for importing and
 * displaying the configuration options for an ECX container.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Lazy
public class ImportEcodexContainerConfig extends AImportOldConfigDialog {
    private final ObjectProvider<EcxContainerConfigForm> formFactory;

    /**
     * Constructor.
     *
     * @param configurationPanelFactory The factory used to create and manage configuration panels.
     * @param formFactory               The factory used to create instances of
     *                                  EcxContainerConfigForm.
     * @throws NullPointerException If configurationPanelFactory or formFactory is null.
     */
    public ImportEcodexContainerConfig(
        ConfigurationPanelFactory configurationPanelFactory,
        ObjectProvider<EcxContainerConfigForm> formFactory) {
        super(configurationPanelFactory);
        this.formFactory = formFactory;
    }

    @Override
    protected Object showImportedConfig(Div div, Map<String, String> p) {
        var oldConfigMapper = new OldConfigMapper(p);
        var properties = oldConfigMapper.migrateEcodexContainerProperties();

        var b = new Binder<>(DCEcodexContainerProperties.class);
        EcxContainerConfigForm form = formFactory.getIfAvailable();
        b.bindInstanceFields(form);
        b.setBean(properties);
        b.setReadOnly(true);

        div.add(form);

        return properties;
    }
}
