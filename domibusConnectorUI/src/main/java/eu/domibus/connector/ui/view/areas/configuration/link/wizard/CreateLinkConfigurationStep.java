package eu.domibus.connector.ui.view.areas.configuration.link.wizard;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.spring.annotation.SpringComponent;
import eu.domibus.connector.link.service.DCActiveLinkManagerService;
import eu.domibus.connector.ui.component.WizardStep;
import eu.domibus.connector.ui.utils.binder.SpringBeanValidationBinderFactory;
import eu.domibus.connector.ui.view.areas.configuration.link.DCLinkConfigurationField;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;


@SpringComponent
@Scope(SCOPE_PROTOTYPE)
public class CreateLinkConfigurationStep extends DCLinkConfigurationField implements WizardStep {
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
