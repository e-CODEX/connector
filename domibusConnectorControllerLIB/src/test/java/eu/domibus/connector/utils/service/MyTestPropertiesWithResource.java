package eu.domibus.connector.utils.service;

import eu.domibus.connector.common.annotations.ConnectorConversationService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class MyTestPropertiesWithResource {

    private Resource r = new ClassPathResource("/testfile");

    public Resource getR() {
        return r;
    }

    public void setR(Resource r) {
        this.r = r;
    }
}
