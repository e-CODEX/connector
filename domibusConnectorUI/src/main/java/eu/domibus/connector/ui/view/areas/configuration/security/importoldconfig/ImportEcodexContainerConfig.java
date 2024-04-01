package eu.domibus.connector.ui.view.areas.configuration.security.importoldconfig;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.data.binder.Binder;
import eu.domibus.connector.security.configuration.DCEcodexContainerProperties;
import eu.domibus.connector.ui.view.areas.configuration.ConfigurationPanelFactory;
import eu.domibus.connector.ui.view.areas.configuration.security.EcxContainerConfigForm;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Lazy
public class ImportEcodexContainerConfig extends AImportOldConfigDialog {
    private final ObjectProvider<EcxContainerConfigForm> formFactory;

    public ImportEcodexContainerConfig(
            ConfigurationPanelFactory configurationPanelFactory,
            ObjectProvider<EcxContainerConfigForm> formFactory) {
        super(configurationPanelFactory);
        this.formFactory = formFactory;
    }

    @Override
    protected Object showImportedConfig(Div div, Map<String, String> p) {
        OldConfigMapper oldConfigMapper = new OldConfigMapper(p);
        DCEcodexContainerProperties properties = oldConfigMapper.migrateEcodexContainerProperties();

        Binder<DCEcodexContainerProperties> b = new Binder<>(DCEcodexContainerProperties.class);
        EcxContainerConfigForm form = formFactory.getIfAvailable();
        b.bindInstanceFields(form);
        b.setBean(properties);
        b.setReadOnly(true);

        div.add(form);

        return properties;
    }
}
