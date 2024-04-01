package eu.domibus.connector.testutil.junit5;

import org.junit.jupiter.api.extension.*;
import org.slf4j.MDC;

import java.lang.reflect.Method;
import java.util.Optional;


public class SetMdcContextExtension implements BeforeAllCallback, AfterAllCallback, BeforeEachCallback,
        AfterEachCallback {
    public static final String MDC_PROPERTY_KEY_TEST_CLZ = "TEST_CLASS";
    public static final String MDC_PROPERTY_KEY_TEST_METHOD = "TEST_METHOD";

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        Optional<Class<?>> testClass = context.getTestClass();
        if (testClass.isPresent()) {
            MDC.put(MDC_PROPERTY_KEY_TEST_CLZ, testClass.get().getSimpleName());
        }
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        MDC.remove(MDC_PROPERTY_KEY_TEST_CLZ);
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        MDC.remove(MDC_PROPERTY_KEY_TEST_METHOD);
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        Optional<Method> testMethod = context.getTestMethod();
        if (testMethod.isPresent()) {
            MDC.put(MDC_PROPERTY_KEY_TEST_METHOD, testMethod.get().getName());
        }
    }
}
