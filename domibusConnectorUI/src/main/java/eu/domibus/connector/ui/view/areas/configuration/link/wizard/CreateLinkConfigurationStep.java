/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.ui.view.areas.configuration.link.wizard;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.spring.annotation.SpringComponent;
import eu.domibus.connector.link.service.DCActiveLinkManagerService;
import eu.domibus.connector.ui.component.WizardStep;
import eu.domibus.connector.ui.utils.binder.SpringBeanValidationBinderFactory;
import eu.domibus.connector.ui.view.areas.configuration.link.DCLinkConfigurationField;
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
