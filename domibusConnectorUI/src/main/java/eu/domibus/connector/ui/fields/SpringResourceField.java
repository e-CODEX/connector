package eu.domibus.connector.ui.fields;

import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.textfield.TextField;
import eu.domibus.connector.common.annotations.ConnectorConversationService;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;


@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SpringResourceField extends CustomField<Resource> {
    private final ApplicationContext applicationContext;
    private final ConversionService conversionService;
    private final Label statusLabel = new Label();
    private final FormLayout formLayout = new FormLayout();
    Resource value;
    private final TextField textField = new TextField("");

    public SpringResourceField(
            ApplicationContext applicationContext,
            @ConnectorConversationService ConversionService conversionService) {
        this.applicationContext = applicationContext;
        this.conversionService = conversionService;
        this.add(textField);
        textField.addValueChangeListener(this::valueChanged);
    }

    private void valueChanged(ComponentValueChangeEvent<TextField, String> valueChangeEvent) {
        String value = valueChangeEvent.getValue();
        Resource changedValue = applicationContext.getResource(value);
        this.value = changedValue;
        setModelValue(changedValue, valueChangeEvent.isFromClient());
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        textField.setReadOnly(readOnly);
    }

    @Override
    protected Resource generateModelValue() {
        return value;
    }

    @Override
    protected void setPresentationValue(Resource newPresentationValue) {
        String convert = conversionService.convert(newPresentationValue, String.class);
        this.textField.setValue(convert);
    }

    @Override
    public Resource getValue() {
        return value;
    }
}
