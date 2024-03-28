package eu.domibus.connector.evidences;

import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.security.NoSuchAlgorithmException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;


class HashValueBuilderAlgorithmTest {
    static final String INITIAL_VALUE = "This is a JUnit Test to test the HashValueBuilder";

    @ParameterizedTest
    @ArgumentsSource(TestdataArgumentsProvider.class)
    void testHashAlgorithm(
            DigestAlgorithm hashAlgorithm,
            String expectedHashValue) throws NoSuchAlgorithmException {
        HashValueBuilder builder = new HashValueBuilder(hashAlgorithm);
        String hash = builder.buildHashValueAsString(INITIAL_VALUE.getBytes());
        assertThat(hash).isEqualTo(expectedHashValue);
    }

    static class TestdataArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    Arguments.of(DigestAlgorithm.MD5, "71bb69d05ca4f082e6d7ab99212f0713"),
                    Arguments.of(DigestAlgorithm.SHA1, "2ce13638c8e2b792a9f381f8d5bd409dd158057f"),
                    Arguments.of(
                            DigestAlgorithm.SHA256,
                            "cbb0dd0acbcd7a98a6e1b5ff9685d10874de9d9bdbcc4a5fa12a258c285908db"
                    ),
                    Arguments.of(
                            DigestAlgorithm.SHA512,
                            "19f4cf71e261a4e6ecb52a93fb2c4bd993397399219362c0cbe7670af9556d32e3c7331f17381321604e9f01af7ccbff0cba9f1e176c44aae0e38250e95d55c1"
                    )
            );
        }
    }
}
