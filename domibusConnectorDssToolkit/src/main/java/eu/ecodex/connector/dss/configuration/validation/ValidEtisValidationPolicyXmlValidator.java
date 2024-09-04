/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.dss.configuration.validation;

import eu.europa.esig.dss.policy.ValidationPolicyFacade;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.xml.bind.JAXBException;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.xml.sax.SAXException;

/**
 * Validates whether a given string represents a valid EtsiValidationPolicy XML.
 */
@SuppressWarnings("squid:S1135")
public class ValidEtisValidationPolicyXmlValidator
    implements ConstraintValidator<ValidEtsiValidationPolicyXml, String> {
    private static final Logger LOGGER =
        LogManager.getLogger(ValidEtisValidationPolicyXmlValidator.class);
    private final ApplicationContext applicationContext;

    public ValidEtisValidationPolicyXmlValidator(ApplicationContext context) {
        this.applicationContext = context;
    }

    @Override
    public void initialize(ValidEtsiValidationPolicyXml constraintAnnotation) {
        // TODO see why this method body is empty
        // ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        try {
            var resource = applicationContext.getResource(value);
            var policyDataStream = resource.getInputStream();
            ValidationPolicyFacade.newFacade().getValidationPolicy(policyDataStream);
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
