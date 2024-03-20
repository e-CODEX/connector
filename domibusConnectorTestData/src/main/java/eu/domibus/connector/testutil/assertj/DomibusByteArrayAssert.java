package eu.domibus.connector.testutil.assertj;

import org.assertj.core.api.AbstractByteArrayAssert;

import java.io.UnsupportedEncodingException;

/**
 * AssertJ assertion which has a method to check if a byte array contains
 * a specific UTF8 String
 */
public class DomibusByteArrayAssert extends AbstractByteArrayAssert<DomibusByteArrayAssert> {


    public DomibusByteArrayAssert(byte[] actual) {
        super(actual, DomibusByteArrayAssert.class);
    }

    public static DomibusByteArrayAssert assertThat(byte[] actual) {
        return new DomibusByteArrayAssert(actual);
    }

    public DomibusByteArrayAssert containsUTF8String(String expected) {
        try {
            isNotNull();
            String byteString = new String(actual, "UTF-8");
            if(!expected.equals(byteString)) {
                failWithMessage("Expected byte array contains <%s> but was <%s>", expected, byteString);
            }
            return this;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
