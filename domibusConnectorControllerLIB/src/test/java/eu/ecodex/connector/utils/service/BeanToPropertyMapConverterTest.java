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

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

@SpringBootTest(
    classes = {BeanToPropertyMapConverterTest.TestContext.class}
)
class BeanToPropertyMapConverterTest {
    private static final Logger LOGGER = LogManager.getLogger(BeanToPropertyMapConverterTest.class);

    @SpringBootApplication(
        scanBasePackages = {
            "eu.ecodex.connector.utils", "eu.ecodex.connector.common.configuration"
        }
    )
    public static class TestContext {
    }

    @Autowired
    BeanToPropertyMapConverter beanToPropertyMapConverter;

    @Test
    void readBeanPropertiesToMap() {

        MyTestProperties myTestProperties = new MyTestProperties();
        myTestProperties.setProp1("prop1");
        myTestProperties.setProp2(23);
        myTestProperties.getNested().setAbc("abc");
        myTestProperties.getNested().setDuration(Duration.ofDays(23));
        myTestProperties.getNested().setaVeryLongPropertyName("propLong");

        MyTestProperties.NestedProp n1 = new MyTestProperties.NestedProp();
        n1.setAbc("abc");
        n1.setaVeryLongPropertyName("verylongprop");

        myTestProperties.getNestedPropList().add(n1);
        myTestProperties.getNestedPropMap().put("n1", n1);

        Map<String, String> expectedMap = new HashMap<>();
        expectedMap.put("test.example.prop1", "prop1");
        expectedMap.put("test.example.prop2", "23");
        expectedMap.put("test.example.nested.abc", "abc");
        expectedMap.put("test.example.nested.duration", "PT552H");
        expectedMap.put("test.example.nested.a-very-long-property-name", "propLong");
        expectedMap.put(
            "test.example.nested-prop-list[0].a-very-long-property-name", "verylongprop");
        expectedMap.put("test.example.nested-prop-list[0].abc", "abc");
        expectedMap.put(
            "test.example.nested-prop-map[n1].a-very-long-property-name", "verylongprop");
        expectedMap.put("test.example.nested-prop-map[n1].abc", "abc");

        Map<String, String> propertyMap = beanToPropertyMapConverter
            .readBeanPropertiesToMap(myTestProperties, "test.example");
        assertThat(propertyMap).containsExactlyInAnyOrderEntriesOf(expectedMap);

        LOGGER.info("Mapped properties are: [{}]", propertyMap);
    }

    @Test
    void readInheritedPropertiesToMap() {
        ExtendsMyTestProperties3 p = new ExtendsMyTestProperties3();
        p.setProp1("prop1");
        p.setProp3("prop3");

        Map<String, String> propertyMap = beanToPropertyMapConverter.readBeanPropertiesToMap(p, "");

        Map<String, String> expectedMap = new HashMap<>();
        expectedMap.put("prop1", "prop1");
        expectedMap.put("prop3", "prop3");

        assertThat(propertyMap).containsExactlyInAnyOrderEntriesOf(expectedMap);
    }

    @Test
    void testMapOfResource() {
        MyTestPropertiesWithResource r = new MyTestPropertiesWithResource();
        r.setR(new ClassPathResource("/testfile"));

        Map<String, String> propertyMap = beanToPropertyMapConverter.readBeanPropertiesToMap(r, "");

        Map<String, String> expectedMap = new HashMap<>();
        expectedMap.put("r", "classpath:testfile");

        assertThat(propertyMap).containsExactlyInAnyOrderEntriesOf(expectedMap);
    }
}
