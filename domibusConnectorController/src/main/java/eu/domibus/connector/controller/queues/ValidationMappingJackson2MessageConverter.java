/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.controller.queues;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Set;
import java.util.stream.Collectors;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.jms.support.converter.SmartMessageConverter;
import org.springframework.lang.Nullable;

/**
 * This class is a MessageConverter implementation that performs bean validation
 * before serializing or deserializing the object using Jackson.
 */
public class ValidationMappingJackson2MessageConverter implements SmartMessageConverter {
    private final ObjectMapper objectMapper;
    private final Validator validator;
    private final MappingJackson2MessageConverter converter;

    /**
     * Creates a ValidationMappingJackson2MessageConverter object.
     *
     * @param objectMapper The ObjectMapper used for serialization and deserialization.
     * @param validator The Validator used for bean validation.
     */
    public ValidationMappingJackson2MessageConverter(ObjectMapper objectMapper,
                                                     Validator validator) {

        this.validator = validator;
        this.objectMapper = objectMapper;

        this.converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        converter.setObjectMapper(objectMapper);
    }

    private void validateObject(Object object) {
        Set<ConstraintViolation<Object>> result = validator.validate(object);
        if (!result.isEmpty()) {
            String errorText = result.stream()
                .map(v -> "property with path " + v.getPropertyPath().toString() + ": "
                    + v.getMessage()
                )
                .collect(Collectors.joining("\n\t"));
            throw new IllegalArgumentException("The provided object is not Valid!\n" + errorText);
        }
    }

    @Override
    public Message toMessage(Object object, Session session, @Nullable Object conversionHint)
        throws JMSException, MessageConversionException {
        validateObject(object);
        return converter.toMessage(object, session, conversionHint);
    }

    @Override
    public Message toMessage(Object object, Session session)
        throws JMSException, MessageConversionException {
        validateObject(object);
        return converter.toMessage(object, session);
    }

    @Override
    public Object fromMessage(Message message) throws JMSException, MessageConversionException {
        return converter.fromMessage(message);
    }
}
