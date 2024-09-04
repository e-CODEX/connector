
/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.link.util;

import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.cxf.ws.policy.PolicyBuilder;
import org.apache.cxf.ws.policy.PolicyBuilderImpl;
import org.apache.neethi.Policy;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

/**
 * The {@code WsdlPolicyTest} class is a test class that verifies the behavior of loading a policy
 * from an input stream.
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
class WsdlPolicyTest {
    @Test
    void testLoadPolicy() throws IOException, ParserConfigurationException, SAXException {
        InputStream inputStream = getClass().getResourceAsStream("/wsdl/backend.policy.xml");

        PolicyBuilder policyBuilder = new PolicyBuilderImpl();
        Policy policy = policyBuilder.getPolicy(inputStream);
    }
}
