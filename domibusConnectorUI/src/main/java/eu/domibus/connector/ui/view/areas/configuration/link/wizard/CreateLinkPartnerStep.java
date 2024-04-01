package eu.domibus.connector.ui.view.areas.configuration.link.wizard;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.server.Command;
import com.vaadin.flow.spring.annotation.SpringComponent;
import eu.domibus.connector.domain.model.DomibusConnectorLinkPartner;
import eu.domibus.connector.link.service.DCActiveLinkManagerService;
import eu.domibus.connector.ui.component.WizardStep;
import eu.domibus.connector.ui.dialogs.ConfirmDialogBuilder;
import eu.domibus.connector.ui.utils.binder.SpringBeanValidationBinderFactory;
import eu.domibus.connector.ui.view.areas.configuration.link.DCLinkPartnerField;
import eu.ecodex.utils.configuration.service.ConfigurationPropertyCollector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;

import java.util.stream.Collectors;


@SpringComponent(CreateLinkPartnerStep.BEAN_NAME)
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CreateLinkPartnerStep extends DCLinkPartnerField implements WizardStep {
    private static final Logger LOGGER = LogManager.getLogger(CreateLinkPartnerStep.class);
    public static final String BEAN_NAME = "CreateLinkPartnerStep";

    public CreateLinkPartnerStep(
            ApplicationContext applicationContext,
            SpringBeanValidationBinderFactory springBeanValidationBinderFactory,
            DCActiveLinkManagerService linkManagerService,
            ConfigurationPropertyCollector configurationPropertyCollector) {
        super(
                applicationContext,
                springBeanValidationBinderFactory,
                linkManagerService,
                configurationPropertyCollector
        );
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public String getStepTitle() {
        return "Create Link Partner Configuration";
    }

    @Override
    public void onForward(Command success) {
        BinderValidationStatus<DomibusConnectorLinkPartner> validate = super.binder.validate();
        if (validate.isOk()) {
            success.execute();
        } else {
            String errorMessages = validate
                    .getValidationErrors()
                    .stream()
                    .map(ValidationResult::getErrorMessage)
                    .collect(Collectors.joining(","));
            ConfirmDialogBuilder.getBuilder()
                                .setMessage(errorMessages)
                                .setOnConfirmCallback(() -> {
                                    success.execute();
                                })
                                .setOnCancelCallback(() -> {
                                    LOGGER.debug("Saving with errors has been canceled!");
                                })
                                .show();
        }
    }
}
