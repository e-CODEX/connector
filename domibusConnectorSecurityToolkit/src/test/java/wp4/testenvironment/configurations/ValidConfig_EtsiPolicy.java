package wp4.testenvironment.configurations;

import eu.europa.esig.dss.policy.EtsiValidationPolicy;
import eu.europa.esig.dss.policy.ValidationPolicyFacade;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.InputStream;

public class ValidConfig_EtsiPolicy {

    public static EtsiValidationPolicy etsiValidationPolicy() {
        try {
            Resource resource = new ClassPathResource("/102853/constraint.xml");
            InputStream policyDataStream = resource.getInputStream();
            EtsiValidationPolicy validationPolicy = null;
            validationPolicy = (EtsiValidationPolicy) ValidationPolicyFacade.newFacade().getValidationPolicy(policyDataStream);
            return validationPolicy;
        } catch (IOException ioe) {
            throw new RuntimeException("Error while loading resource", ioe);
        } catch (XMLStreamException | JAXBException | SAXException e) {
            throw new RuntimeException("Error while parsing EtsiValidationPolicy", e);
        }
    }
}
