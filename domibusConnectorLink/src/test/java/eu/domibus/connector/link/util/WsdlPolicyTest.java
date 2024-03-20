
package eu.domibus.connector.link.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.cxf.ws.policy.PolicyBuilder;
import org.apache.cxf.ws.policy.PolicyBuilderImpl;
import org.apache.neethi.Policy;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

/**
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
public class WsdlPolicyTest {
    
    
    @Test
    public void testLoadPolicy() throws IOException, ParserConfigurationException, SAXException {
        InputStream inputStream = getClass().getResourceAsStream("/wsdl/backend.policy.xml");
        
        PolicyBuilder policyBuilder = new PolicyBuilderImpl();
        Policy policy = policyBuilder.getPolicy(inputStream);
          
        
    }

}
