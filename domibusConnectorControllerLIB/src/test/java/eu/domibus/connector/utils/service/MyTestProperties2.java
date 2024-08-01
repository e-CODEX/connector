package eu.domibus.connector.utils.service;

import eu.domibus.connector.common.annotations.BusinessDomainScoped;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

/**
 * The MyTestProperties2 class represents a set of configuration properties for the "test.example2"
 * namespace. It is annotated with @Component and @BusinessDomainScoped, indicating that it is a
 * component scoped to a specific business domain.
 *
 * <p>The properties are defined as private fields within the class, with corresponding getter and
 * setter methods for each property. The class also contains a nested class called NestedProp, which
 * represents a subset of properties within MyTestProperties2.
 */
@Component
@BusinessDomainScoped
@ConfigurationProperties(prefix = "test.example2")
@Getter
@Setter
public class MyTestProperties2 {
    private String prop1;
    private Integer prop2;
    @NestedConfigurationProperty
    private NestedProp nested = new NestedProp();
    private List<String> list = new ArrayList<>();
    private List<NestedProp> nestedProps = new ArrayList<>();

    /**
     * This class represents a nested property with three fields: abc, duration, and
     * aVeryLongPropertyName. It provides getter and setter methods for each field.
     */
    @Getter
    @Setter
    public static class NestedProp {
        private String abc;
        private Duration duration;
        @SuppressWarnings("checkstyle:MemberName")
        private String aVeryLongPropertyName;
    }

    public String getProp1AsAnotherString() {
        return prop1;
    }
}
