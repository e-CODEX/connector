package eu.domibus.connector.lib.spring.configuration.validation;

import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.validation.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class ResourceReadableValidator implements ConstraintValidator<CheckResourceIsReadable, String> {

    private final ApplicationContext ctx;

    public ResourceReadableValidator(ApplicationContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void initialize(CheckResourceIsReadable constraintAnnotation) {

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        context.disableDefaultConstraintViolation();
        try {
            Resource r = ctx.getResource(value);

            if (!r.exists()) {
//                context.buildConstraintViolationWithTemplate("eu.domibus.connector.lib.spring.configuration.validation.resource_input_stream_valid")
//                        .addConstraintViolation();
                String message = String.format("Cannot open provided resource [%s]! Check if the path is correct and exists!", value);
                context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
                return false;
            }
            r.getInputStream().close();
//            inputStream.close();
        } catch (IOException e) {
            String message = String.format("Cannot open provided resource [%s]! Check if the path is correct and exists!", value);
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            return false;
        }
        return true;
    }

    private String getUrl(Resource value) {
        URL url = null;
        try {
            url = value.getURL();
            return url.toString();
        } catch (IOException e) {
            return value.toString();
        }
    }
}
