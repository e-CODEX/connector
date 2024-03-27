package eu.domibus.connector.utils.service;

import eu.domibus.connector.common.annotations.BusinessDomainScoped;
import eu.domibus.connector.common.annotations.MapNested;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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

    public String getProp1() {
        return prop1;
    }

    public void setProp1(String prop1) {
        this.prop1 = prop1;
    }

    public Integer getProp2() {
        return prop2;
    }

    public void setProp2(Integer prop2) {
        this.prop2 = prop2;
    }

    public NestedProp getNested() {
        return nested;
    }

    public void setNested(NestedProp nested) {
        this.nested = nested;
    }

    public String getProp1AsAnotherString() {
        return prop1;
    }

    public List<NestedProp> getNestedPropList() {
        return nestedPropList;
    }

    public void setNestedPropList(List<NestedProp> nestedPropList) {
        this.nestedPropList = nestedPropList;
    }

    public Map<String, NestedProp> getNestedPropMap() {
        return nestedPropMap;
    }

    public void setNestedPropMap(Map<String, NestedProp> nestedPropMap) {
        this.nestedPropMap = nestedPropMap;
    }

    @MapNested
    public static class NestedProp {
        private String abc;
        private Duration duration;
        private String aVeryLongPropertyName;

        public String getAbc() {
            return abc;
        }

        public void setAbc(String abc) {
            this.abc = abc;
        }

        public Duration getDuration() {
            return duration;
        }

        public void setDuration(Duration duration) {
            this.duration = duration;
        }

        public String getaVeryLongPropertyName() {
            return aVeryLongPropertyName;
        }

        public void setaVeryLongPropertyName(String aVeryLongPropertyName) {
            this.aVeryLongPropertyName = aVeryLongPropertyName;
        }
    }
}
