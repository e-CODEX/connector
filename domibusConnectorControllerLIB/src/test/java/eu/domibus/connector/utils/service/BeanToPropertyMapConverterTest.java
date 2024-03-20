package eu.domibus.connector.utils.service;

import eu.domibus.connector.common.configuration.ConnectorConfigurationProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        classes = {BeanToPropertyMapConverterTest.TestContext.class}
)
class BeanToPropertyMapConverterTest {

    private static final Logger LOGGER = LogManager.getLogger(BeanToPropertyMapConverterTest.class);

    @SpringBootApplication(scanBasePackages = "eu.domibus.connector.utils")
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

        Map<String, String> propertyMap = beanToPropertyMapConverter.readBeanPropertiesToMap(myTestProperties, "test.example");

        Map<String, String> expectedMap = new HashMap<>();
        expectedMap.put("test.example.prop1", "prop1");
        expectedMap.put("test.example.prop2", "23");
        expectedMap.put("test.example.nested.abc", "abc");
        expectedMap.put("test.example.nested.duration", "PT552H");
        expectedMap.put("test.example.nested.a-very-long-property-name", "propLong");
        expectedMap.put("test.example.nested-prop-list[0].a-very-long-property-name", "verylongprop");
        expectedMap.put("test.example.nested-prop-list[0].abc", "abc");
        expectedMap.put("test.example.nested-prop-map[n1].a-very-long-property-name", "verylongprop");
        expectedMap.put("test.example.nested-prop-map[n1].abc", "abc");

        assertThat(propertyMap).containsExactlyInAnyOrderEntriesOf(expectedMap);

        LOGGER.info("Mapped properties are: [{}]", propertyMap);
    }

    @Test
    public void readInheritedPropertiesToMap() {
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
    public void testMapOfResource() {
        MyTestPropertiesWithResource r = new MyTestPropertiesWithResource();
        r.setR(new ClassPathResource("/testfile"));

        Map<String, String> propertyMap = beanToPropertyMapConverter.readBeanPropertiesToMap(r, "");

        Map<String, String> expectedMap = new HashMap<>();
        expectedMap.put("r", "classpath:testfile");

        assertThat(propertyMap).containsExactlyInAnyOrderEntriesOf(expectedMap);
    }

}