package eu.domibus.connector.link.wsbackendplugin;

import jakarta.xml.ws.EndpointReference;
import jakarta.xml.ws.WebServiceContext;
import jakarta.xml.ws.handler.MessageContext;
import org.apache.cxf.jaxws.context.WebServiceContextImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.w3c.dom.Element;

import java.security.Principal;

@Configuration
public class TestConfigurationCXFWsBug {

    @Bean
    public WebServiceContext webServiceContext(){
        return new WebServiceContextImpl();
    }

}
