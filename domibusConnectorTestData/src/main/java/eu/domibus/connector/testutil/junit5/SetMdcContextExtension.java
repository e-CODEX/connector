/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.testutil.junit5;

import java.lang.reflect.Method;
import java.util.Optional;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.MDC;

/**
 * The {@code SetMdcContextExtension} class implements the {@link BeforeAllCallback},
 * {@link AfterAllCallback}, {@link BeforeEachCallback}, and {@link AfterEachCallback} interfaces,
 * allowing it to provide extension behavior before and after test execution.
 *
 * <p>This class sets the Mapped Diagnostic Context (MDC) properties for test classes and test
 * methods
 * in order to provide context information during logging.
 *
 * <p>The MDC properties that are set are:
 * <ul>
 *   <li>{@code TEST_CLASS}: The simple name of the test class.</li>
 *   <li>{@code TEST_METHOD}: The name of the test method.</li>
 * </ul>
 *
 * <p>The MDC properties are set before each test method execution and are cleared after each test
 * method execution. Additionally, the MDC property for the test class is cleared after all test
 * methods in that class have executed.
 *
 * <p>To use this extension, simply register it as a test extension using the appropriate extension
 * registration mechanism provided by your testing framework.
 */
public class SetMdcContextExtension
    implements BeforeAllCallback, AfterAllCallback, BeforeEachCallback, AfterEachCallback {
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
