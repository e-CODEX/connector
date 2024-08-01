package eu.domibus.connector.utils.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * This class represents an instance of MyTestPropertiesWithResource. It provides methods to get and
 * set the value of the 'r' property, which represents a resource.
 */
@Getter
@Setter
public class MyTestPropertiesWithResource {
    @SuppressWarnings("checkstyle:MemberName")
    private Resource r = new ClassPathResource("/testfile");
}
