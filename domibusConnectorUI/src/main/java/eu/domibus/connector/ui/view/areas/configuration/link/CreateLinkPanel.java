package eu.domibus.connector.ui.view.areas.configuration.link;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Setter;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.Command;
import eu.domibus.connector.domain.enums.LinkType;
import eu.domibus.connector.domain.model.DomibusConnectorLinkConfiguration;
import eu.domibus.connector.domain.model.DomibusConnectorLinkPartner;
import eu.domibus.connector.link.service.DCLinkFacade;
import eu.domibus.connector.persistence.service.DCLinkPersistenceService;
import eu.domibus.connector.ui.component.WizardComponent;
import eu.domibus.connector.ui.component.WizardStep;
import eu.domibus.connector.ui.view.areas.configuration.link.wizard.ChooseImplStep;
import eu.domibus.connector.ui.view.areas.configuration.link.wizard.CreateLinkConfigurationStep;
import eu.domibus.connector.ui.view.areas.configuration.link.wizard.CreateLinkPartnerStep;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Scope;

import java.time.Duration;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;


@Scope(SCOPE_PROTOTYPE)
@org.springframework.stereotype.Component
@Route("createlink")
public class CreateLinkPanel extends VerticalLayout {
    private static final Logger LOGGER = LogManager.getLogger(CreateLinkPanel.class);

    private final DCLinkPersistenceService dcLinkPersistenceService;
    private final DCLinkFacade dcLinkFacade;
    private final ObjectProvider<CreateLinkPartnerStep> createLinkPartnerStepProvider;
    private final ObjectProvider<CreateLinkConfigurationStep> createLinkConfigurationStepProvider;
    private LinkType linkType;
    private WizardComponent wizard;
    private Dialog parentDialog;
    private final Binder<LnkConfigItem> linkConfigItemBinder = new Binder<>();
    private LnkConfigItem linkConfigItem;
    private DomibusConnectorLinkConfiguration linkConfiguration;
    private DomibusConnectorLinkPartner linkPartner;

    public CreateLinkPanel(
            DCLinkPersistenceService dcLinkPersistenceService,
            DCLinkFacade dcLinkFacade,
            ObjectProvider<CreateLinkPartnerStep> createLinkPartnerStepProvider,
            ObjectProvider<CreateLinkConfigurationStep> createLinkConfigurationStepProvider
    ) {
        this.dcLinkFacade = dcLinkFacade;
        this.createLinkPartnerStepProvider = createLinkPartnerStepProvider;
        this.createLinkConfigurationStepProvider = createLinkConfigurationStepProvider;
        this.dcLinkPersistenceService = dcLinkPersistenceService;
        init();
    }

    private void init() {
        linkConfigItem = new LnkConfigItem();
        linkConfigItem.setLinkType(getLinkType());
        linkConfigItemBinder.setBean(linkConfigItem);

        linkConfiguration = new DomibusConnectorLinkConfiguration();
        linkPartner = new DomibusConnectorLinkPartner();
        linkPartner.setLinkType(getLinkType());
        linkPartner.setEnabled(true);
        linkPartner.setLinkConfiguration(linkConfiguration);

        initUI();
    }

    public LinkType getLinkType() {
        return this.linkType;
    }

    public void setLinkType(LinkType linkType) {
        this.linkType = linkType;
    }

    private void initUI() {
        // TODO: use binder...
        ChooseImplStep chooseImplStep = new ChooseImplStep(dcLinkFacade, linkConfigItemBinder);

        CreateLinkConfigurationStep createLinkConfigurationStep = createLinkConfigurationStepProvider.getObject();
        linkConfigItemBinder.bind(
                createLinkConfigurationStep,
                (ValueProvider<LnkConfigItem, DomibusConnectorLinkConfiguration>) LnkConfigItem::getLinkConfiguration,
                (Setter<LnkConfigItem, DomibusConnectorLinkConfiguration>) LnkConfigItem::setLinkConfiguration
        );

        CreateLinkPartnerStep createLinkPartnerStep = createLinkPartnerStepProvider.getObject();
        linkConfigItemBinder.bind(
                createLinkPartnerStep,
                (ValueProvider<LnkConfigItem, DomibusConnectorLinkPartner>) LnkConfigItem::getLinkPartner,
                (Setter<LnkConfigItem, DomibusConnectorLinkPartner>) LnkConfigItem::setLinkPartner
        );

        wizard = WizardComponent.getBuilder()
                                .addStep(chooseImplStep)
                                .addStep(createLinkConfigurationStep)
                                .addStep(createLinkPartnerStep)
                                .addFinishedListener(this::wizardFinished)
                                .build();
        add(wizard);
    }

    public void setParentDialog(Dialog parentDialog) {
        this.parentDialog = parentDialog;
    }

    private void wizardFinished(WizardComponent wizardComponent, WizardStep wizardStep) {
        linkPartner.setLinkConfiguration(linkConfiguration);
        linkPartner.setLinkType(getLinkType());
        try {
            dcLinkPersistenceService.addLinkPartner(linkPartner);
        } catch (RuntimeException e) {
            LOGGER.error("Exception occured while creating a new Link Configuration", e);
            Notification.show(
                    "No Link Configuration was created due: " + e.getMessage(),
                    (int) Duration.ofSeconds(5).toMillis(),
                    Notification.Position.TOP_CENTER
            );
        }
        Notification.show(
                "New LinkPartner configuration successfully created",
                (int) Duration.ofSeconds(5).toMillis(),
                Notification.Position.TOP_CENTER
        );
        if (parentDialog != null) {
            parentDialog.close();
        }
    }

    public WizardComponent getWizard() {
        return this.wizard;
    }

    public static class ChooseOrCreateLinkConfigurationStep extends VerticalLayout implements WizardStep {
        private static final Logger LOGGER = LogManager.getLogger(ChooseOrCreateLinkConfigurationStep.class);
        private static final String EXISTING_LINK_CONFIG = "Use existing Link Configuration";
        private static final String NEW_LINK_CONFIG = "Create new Link Configuration";

        private final RadioButtonGroup<String> newLinkConfiguration = new RadioButtonGroup<>();
        private final ComboBox<DomibusConnectorLinkConfiguration> linkConfigurationChooser = new ComboBox<>();

        private DCLinkConfigurationField linkConfigPanel;

        public ChooseOrCreateLinkConfigurationStep() {
            initUI();
        }

        private void initUI() {
            // the radio button group to switch mode between new or existing link config
            newLinkConfiguration.setItems(EXISTING_LINK_CONFIG, NEW_LINK_CONFIG);
            newLinkConfiguration.setValue(NEW_LINK_CONFIG);
            newLinkConfiguration.addValueChangeListener(this::newExistingLinkConfigChanged);

            // choose an existing link configuration
            linkConfigurationChooser.addValueChangeListener(this::linkConfigChanged);
            linkConfigurationChooser.setAllowCustomValue(false);
            //            linkConfigurationChooser.setItems(dcLinkPersistenceService.getAllLinkConfigurations());
            linkConfigurationChooser.setRequired(true);
            linkConfigurationChooser.setItemLabelGenerator(item -> {
                if (item != null) {
                    return item.getConfigName() + " with impl " + item.getLinkImpl();
                }
                return "No Configuration set";
            });

            add(newLinkConfiguration);
            add(linkConfigurationChooser);
            add(linkConfigPanel);

            newLinkConfiguration.setValue(EXISTING_LINK_CONFIG);
        }

        private void newExistingLinkConfigChanged(HasValue.ValueChangeEvent<String> valueChangeEvent) {
            if (NEW_LINK_CONFIG.equals(valueChangeEvent.getValue())) {
                linkConfigPanel.setReadOnly(false);
                linkConfigurationChooser.setReadOnly(true);
                DomibusConnectorLinkConfiguration newLinkConfig = new DomibusConnectorLinkConfiguration();
                //                linkPartner.setLinkConfiguration(newLinkConfig);
                newLinkConfig.setConfigName(new DomibusConnectorLinkConfiguration.LinkConfigName("changeme"));
                //                linkConfiguration = newLinkConfig;
                //                linkConfigPanel.setValue(linkConfiguration);
            } else if (EXISTING_LINK_CONFIG.equals(valueChangeEvent.getValue())) {
                linkConfigPanel.setReadOnly(true);
                linkConfigurationChooser.setReadOnly(false);
            }
        }

        private void linkConfigChanged(AbstractField.ComponentValueChangeEvent<ComboBox<DomibusConnectorLinkConfiguration>, DomibusConnectorLinkConfiguration> changeEvent) {
            DomibusConnectorLinkConfiguration value = changeEvent.getValue();
            linkConfigPanel.setVisible(value != null);
        }

        @Override
        public Component getComponent() {
            return this;
        }

        @Override
        public String getStepTitle() {
            return "Configure Link Implementation";
        }

        @Override
        public void onForward(Command success) {
        }
    }
}
