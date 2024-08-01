package eu.domibus.connector.utils.service;

import eu.domibus.connector.common.annotations.BusinessDomainScoped;
import eu.domibus.connector.common.annotations.MapNested;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

/**
 * This class represents the properties for the MyTestProperties component.
 */
@Getter
@Setter
@Component
@BusinessDomainScoped
@ConfigurationProperties(prefix = "test.example")
public class MyTestProperties {
    private String prop1;
    private Integer prop2;
    @NestedConfigurationProperty
    @MapNested
    private NestedProp nested = new NestedProp();
    private List<@MapNested NestedProp> nestedPropList = new ArrayList<>();
    private Map<String, @MapNested NestedProp> nestedPropMap = new HashMap<>();

    /**
     * This class represents a nested configuration property. It is annotated with @MapNested to
     * indicate that it should be mapped as a nested property.
     */
    @MapNested
    public static class NestedProp {
        @Getter
        @Setter
        private String abc;
        @Getter
        @Setter
        private Duration duration;
        @SuppressWarnings("checkstyle:MemberName")
        private String aVeryLongPropertyName;

        public String getaVeryLongPropertyName() {
            return aVeryLongPropertyName;
        }

        @SuppressWarnings("checkstyle:ParameterName")
        public void setaVeryLongPropertyName(String aVeryLongPropertyName) {
            this.aVeryLongPropertyName = aVeryLongPropertyName;
        }
    }

    public String getProp1AsAnotherString() {
        return prop1;
    }
}
