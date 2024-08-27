/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

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
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;

/**
 * Represents a step in the wizard for creating a link partner configuration.
 */
@SpringComponent(CreateLinkPartnerStep.BEAN_NAME)
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CreateLinkPartnerStep extends DCLinkPartnerField implements WizardStep {
    private static final Logger LOGGER = LogManager.getLogger(CreateLinkPartnerStep.class);
    public static final String BEAN_NAME = "CreateLinkPartnerStep";

    /**
     * Connector.
     *
     * @param applicationContext                the application context
     * @param springBeanValidationBinderFactory the Spring bean validation binder factory
     * @param linkManagerService                the link manager service
     * @param configurationPropertyCollector    the configuration property collector
     */
    public CreateLinkPartnerStep(
        ApplicationContext applicationContext,
        SpringBeanValidationBinderFactory springBeanValidationBinderFactory,
        DCActiveLinkManagerService linkManagerService,
        ConfigurationPropertyCollector configurationPropertyCollector) {
        super(
            applicationContext, springBeanValidationBinderFactory, linkManagerService,
            configurationPropertyCollector
        );
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public void onForward(Command success) {

        BinderValidationStatus<DomibusConnectorLinkPartner> validate = super.binder.validate();
        if (validate.isOk()) {
            success.execute();
        } else {
            String errorMessages = validate.getValidationErrors()
                                           .stream()
                                           .map(ValidationResult::getErrorMessage)
                                           .collect(Collectors.joining(","));
            ConfirmDialogBuilder.getBuilder()
                                .setMessage(errorMessages)
                                .setOnConfirmCallback(success::execute)
                                .setOnCancelCallback(
                                    () -> LOGGER.debug("Saving with errors has been canceled!"))
                                .show();
        }
    }

    @Override
    public String getStepTitle() {
        return "Create Link Partner Configuration";
    }
}
