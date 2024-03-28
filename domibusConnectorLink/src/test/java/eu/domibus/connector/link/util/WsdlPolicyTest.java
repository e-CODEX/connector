package eu.domibus.connector.link.util;

import org.apache.cxf.ws.policy.PolicyBuilder;
import org.apache.cxf.ws.policy.PolicyBuilderImpl;
import org.apache.neethi.Policy;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;


/**
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
