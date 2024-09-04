/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.utils.service;

import eu.ecodex.connector.common.annotations.BusinessDomainScoped;
import eu.ecodex.connector.common.annotations.MapNested;
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
