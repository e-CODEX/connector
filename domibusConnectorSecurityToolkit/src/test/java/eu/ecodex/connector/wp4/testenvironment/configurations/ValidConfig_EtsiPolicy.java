/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.wp4.testenvironment.configurations;

import eu.europa.esig.dss.policy.EtsiValidationPolicy;
import eu.europa.esig.dss.policy.ValidationPolicyFacade;
import jakarta.xml.bind.JAXBException;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import org.springframework.core.io.ClassPathResource;
import org.xml.sax.SAXException;

/**
 * The ValidConfig_EtsiPolicy class provides a method to retrieve the EtsiValidationPolicy object
 * used for ETSI validation.
 *
 * <p>The class contains a single static method, etsiValidationPolicy(), which loads the validation
 * policy XML file and creates an instance of the EtsiValidationPolicy class using the
 * ValidationPolicyFacade. The XML file is loaded from the classpath using the resource
 * "/102853/constraint.xml". If the file cannot be found or there is an error parsing the XML file,
 * a RuntimeException is thrown.
 *
 * <p>This class should be used to obtain the EtsiValidationPolicy object.
 *
 * @since Do not use @since tag.
 */
@SuppressWarnings("checkstyle:TypeName")
public class ValidConfig_EtsiPolicy {
    /**
     * Retrieves the EtsiValidationPolicy object used for ETSI validation.
     *
     * <p>This method loads the validation policy XML file and creates an instance of the
     * EtsiValidationPolicy class using the ValidationPolicyFacade. The XML file is loaded from the
     * classpath using the resource "/102853/constraint.xml". If the file cannot be found or there
     * is an error parsing the XML file, a RuntimeException is thrown.
     *
     * @return the EtsiValidationPolicy object
     * @throws RuntimeException if there is an error loading the resource or parsing the
     *                          EtsiValidationPolicy
     */
    public static EtsiValidationPolicy etsiValidationPolicy() {
        try {
            var resource = new ClassPathResource("/102853/constraint.xml");
            var policyDataStream = resource.getInputStream();
            return (EtsiValidationPolicy) ValidationPolicyFacade
                .newFacade()
                .getValidationPolicy(policyDataStream);
        } catch (IOException ioe) {
            throw new RuntimeException("Error while loading resource", ioe);
        } catch (XMLStreamException | JAXBException | SAXException e) {
            throw new RuntimeException("Error while parsing EtsiValidationPolicy", e);
        }
    }
}
