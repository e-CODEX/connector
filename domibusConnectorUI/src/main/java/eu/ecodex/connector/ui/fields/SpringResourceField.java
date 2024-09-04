/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.ui.fields;

import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.textfield.TextField;
import eu.ecodex.connector.common.annotations.ConnectorConversationService;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

/**
 * Represents a custom field used for selecting a Spring resource.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SpringResourceField extends CustomField<Resource> {
    private final ApplicationContext applicationContext;
    private final ConversionService conversionService;
    private final TextField textField = new TextField("");
    private final NativeLabel statusLabel = new NativeLabel();
    private final FormLayout formLayout = new FormLayout();
    Resource value;

    /**
     * Constructor.
     *
     * @param applicationContext The ApplicationContext used for retrieving Spring resources.
     * @param conversionService  The ConversionService used for converting the Spring resource to a
     *                           String value.
     */
    public SpringResourceField(
        ApplicationContext applicationContext,
        @ConnectorConversationService ConversionService conversionService) {
        this.applicationContext = applicationContext;
        this.conversionService = conversionService;
        this.add(textField);
        textField.addValueChangeListener(this::valueChanged);
    }

    private void valueChanged(ComponentValueChangeEvent<TextField, String> valueChangeEvent) {
        var value = valueChangeEvent.getValue();
        var changedValue = applicationContext.getResource(value);
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
    public Resource getValue() {
        return value;
    }

    @Override
    protected void setPresentationValue(Resource newPresentationValue) {
        String convert = conversionService.convert(newPresentationValue, String.class);
        this.textField.setValue(convert);
    }
}
