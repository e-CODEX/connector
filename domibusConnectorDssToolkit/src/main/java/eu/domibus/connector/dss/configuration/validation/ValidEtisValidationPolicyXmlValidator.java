package eu.domibus.connector.dss.configuration.validation;

import eu.europa.esig.dss.policy.ValidationPolicy;
import eu.europa.esig.dss.policy.ValidationPolicyFacade;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.xml.sax.SAXException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.InputStream;

public class ValidEtisValidationPolicyXmlValidator implements ConstraintValidator<ValidEtsiValidationPolicyXml, String> {

    private static final Logger LOGGER = LogManager.getLogger(ValidEtisValidationPolicyXmlValidator.class);

    private final ApplicationContext applicationContext;

    public ValidEtisValidationPolicyXmlValidator(ApplicationContext context) {
        this.applicationContext = context;
    }


    @Override
    public void initialize(ValidEtsiValidationPolicyXml constraintAnnotation) {
//        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        try {
            Resource resource = applicationContext.getResource(value);
            InputStream policyDataStream = resource.getInputStream();
            ValidationPolicy validationPolicy = null;
            validationPolicy = ValidationPolicyFacade.newFacade().getValidationPolicy(policyDataStream);
            return true;
        } catch (IOException ioe) {
            LOGGER.warn("Error while loading resource", ioe);
            return false;
        } catch (XMLStreamException | JAXBException | SAXException e) {
            LOGGER.warn("Parsing error during validation", e);
            return false;
        } catch (Exception e) {
            LOGGER.error("Unable to validate due exception!", e);
            return true;
        }
    }
}
