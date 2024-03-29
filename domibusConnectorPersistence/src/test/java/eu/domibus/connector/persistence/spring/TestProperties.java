package eu.domibus.connector.persistence.spring;

import eu.domibus.connector.common.annotations.BusinessDomainScoped;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Component
@BusinessDomainScoped
@ConfigurationProperties(prefix = "mytest.prop-dash")
public class TestProperties {
    private String t1;

    private String camelCaseProperty;

    public String getT1() {
        return t1;
    }

    public void setT1(String t1) {
        this.t1 = t1;
    }

    public String getCamelCaseProperty() {
        return camelCaseProperty;
    }

    public void setCamelCaseProperty(String camelCaseProperty) {
        this.camelCaseProperty = camelCaseProperty;
    }
}
