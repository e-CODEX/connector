package eu.domibus.connector.persistence.spring;

import eu.domibus.connector.common.annotations.BusinessDomainScoped;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * This class represents the test properties for a specific business domain. It is used to configure
 * the properties for the test environment.
 */
@Component
@BusinessDomainScoped
@ConfigurationProperties(prefix = "mytest.prop-dash")
@Getter
@Setter
public class TestProperties {
    private String t1;
    private String camelCaseProperty;
}
