package eu.domibus.connector.ui.view.areas.configuration.security.importoldconfig;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.data.binder.Binder;
import eu.domibus.connector.evidences.spring.EvidencesToolkitConfigurationProperties;
import eu.domibus.connector.ui.view.areas.configuration.ConfigurationPanelFactory;
import eu.domibus.connector.ui.view.areas.configuration.evidences.EvidencesToolkitConfigurationPropertiesForm;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Lazy
public class ImportOldEvidenceConfigDialog extends AImportOldConfigDialog {
    private final ObjectProvider<EvidencesToolkitConfigurationPropertiesForm> formFactory;

    public ImportOldEvidenceConfigDialog(
            ConfigurationPanelFactory configurationPanelFactory,
            ObjectProvider<EvidencesToolkitConfigurationPropertiesForm> formFactory) {
        super(configurationPanelFactory);
        this.formFactory = formFactory;
    }

    @Override
    protected Object showImportedConfig(Div div, Map<String, String> p) {
        OldConfigMapper oldConfigMapper = new OldConfigMapper(p);
        EvidencesToolkitConfigurationProperties properties = oldConfigMapper.migrateEvidencesToolkitConfig();

        Binder<EvidencesToolkitConfigurationProperties> b =
                new Binder<>(EvidencesToolkitConfigurationProperties.class);
        EvidencesToolkitConfigurationPropertiesForm form = formFactory.getIfAvailable();
        b.bindInstanceFields(form);
        b.setBean(properties);
        b.setReadOnly(true);

        div.add(form);

        return properties;
    }
}

