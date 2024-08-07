package eu.domibus.connector.persistence.testutil;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider;

/**
 * LiquibaseTemplateInvocationContextProvider is a class that implements the
 * TestTemplateInvocationContextProvider interface. It provides the test template invocation
 * contexts for Liquibase tests.
 */
@SuppressWarnings("squid:S1135")
public class LiquibaseTemplateInvocationContextProvider
    implements TestTemplateInvocationContextProvider {
    private static final Logger LOGGER =
        LogManager.getLogger(LiquibaseTemplateInvocationContextProvider.class);
    public static final List<TestDatabaseFactory> AVAILABLE_DBMS = Stream.of(

        new PostgresContainerTestDatabaseFactory(), // not supported yet!
        H2TestDatabaseFactory.h2Mysql(),
        H2TestDatabaseFactory.h2Oracle(),
        new OracleContainerTestDatabaseFactory(),
        new MariaDbContainerTestDatabaseFactory(),
        new MysqlContainerTestDatabaseFactory()
    ).toList();

    @Override
    public boolean supportsTestTemplate(ExtensionContext extensionContext) {
        return true;
    }

    @Override
    public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(
        ExtensionContext extensionContext) {
        // TODO: load Test method annotations...

        Method testMethod = extensionContext.getTestMethod().get();
        FromVersion[] annotationsByType = testMethod.getAnnotationsByType(FromVersion.class);

        return AVAILABLE_DBMS.stream()
                             .map(f -> {
                                 LOGGER.info("Processing TestDBFactory [{}]", f);
                                 return f;
                             })
                             .flatMap(tdbfactory -> Stream
                                 .of(annotationsByType)
                                 .map(FromVersion::value)
                                 .map(s -> s.isEmpty() ? null : s)
                                 .filter(tdbfactory::isAvailable)
                                 .map(version -> invocationContext(
                                     tdbfactory.createNewDatabase(version))
                                 ));
    }

    private TestTemplateInvocationContext invocationContext(TestDatabase testDatabase) {
        LOGGER.info("Creating test context for DB [{}]", testDatabase);
        return new TestTemplateInvocationContext() {
            @Override
            public String getDisplayName(int invocationIndex) {
                return testDatabase.getName();
            }

            @Override
            public List<Extension> getAdditionalExtensions() {
                return Arrays.asList(new ParameterResolver() {
                    @Override
                    public boolean supportsParameter(
                        ParameterContext parameterContext, ExtensionContext extensionContext) {
                        return parameterContext.getParameter().getType().equals(Properties.class);
                    }

                    @Override
                    public Object resolveParameter(
                        ParameterContext parameterContext, ExtensionContext extensionContext) {
                        String id = extensionContext.getUniqueId();
                        TestDatabase testDatabase = (TestDatabase) extensionContext.getStore(
                            ExtensionContext.Namespace.GLOBAL).get(id + "testdb");
                        return testDatabase.getProperties();
                    }
                }, (BeforeEachCallback) extensionContext -> {

                    String id = extensionContext.getUniqueId();
                    extensionContext.getStore(ExtensionContext.Namespace.GLOBAL)
                                    .put(id + "testdb", testDatabase);
                }, (AfterEachCallback) extensionContext -> {
                    String id = extensionContext.getUniqueId();
                    TestDatabase testDatabase =
                        (TestDatabase) extensionContext.getStore(ExtensionContext.Namespace.GLOBAL)
                                                       .get(id + "testdb");
                    testDatabase.close(); // close test database after test
                });
            }
        };
    }
}
