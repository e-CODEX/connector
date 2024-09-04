/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.ui.view.areas.configuration.link.wizard;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.spring.annotation.SpringComponent;
import eu.ecodex.connector.link.service.DCActiveLinkManagerService;
import eu.ecodex.connector.ui.component.WizardStep;
import eu.ecodex.connector.ui.utils.binder.SpringBeanValidationBinderFactory;
import eu.ecodex.connector.ui.view.areas.configuration.link.DCLinkConfigurationField;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;

/**
 * The {@code CreateLinkConfigurationStep} class is a component that represents a step in a wizard
 * for creating a link configuration.
 */
@SpringComponent
@Scope(SCOPE_PROTOTYPE)
public class CreateLinkConfigurationStep extends DCLinkConfigurationField implements WizardStep {
    /**
     * Constructor.
     *
     * @param applicationContext
     *              The application context. A context that provides access to the application's
     *              beans and other components.
     * @param linkManagerService
     *              The link manager service. A service that allows managing links.
     * @param springBeanValidationBinderFactory
     *              The spring bean validation binder factory. A factory for creating binders that
     *              validate and bind fields in forms.
     */
    public CreateLinkConfigurationStep(
        ApplicationContext applicationContext,
        DCActiveLinkManagerService linkManagerService,
        SpringBeanValidationBinderFactory springBeanValidationBinderFactory) {
        super(applicationContext, linkManagerService, springBeanValidationBinderFactory);
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public String getStepTitle() {
        return "Link Configuration";
    }
}
