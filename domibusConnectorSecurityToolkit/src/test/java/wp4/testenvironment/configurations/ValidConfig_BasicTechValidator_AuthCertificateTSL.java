package wp4.testenvironment.configurations;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


public class ValidConfig_BasicTechValidator_AuthCertificateTSL {
    private static final String Path_to_valid_TSL = "src/test/resources/tsl/ecodex_ger_tsl.xml";
    private static final String Path_to_valid_LOTL = "src/test/resources/tsl/LOTL.xml";

    /**
     * Gets a URI String to a valid TSL
     * The used TSL is not a LOTL.
     * The respective test case is currently under construction
     */
    public static String get_URIString_with_TSL() {
        return "file:./" + Path_to_valid_TSL;
    }

    /**
     * Gets the valid TSL as a FileInputStream for configuration.
     * The used TSL is not a LOTL.
     * The respective test case is currently under construction
     */
    public static FileInputStream get_FileInputStream_with_TSL() {
        FileInputStream fis = null;

        try {
            fis = new FileInputStream(Path_to_valid_TSL);
        } catch (FileNotFoundException e) {
            Assertions.fail("Invalid path to test TSL!", e);
        }

        return fis;
    }

    /**
     * Gets the valid TSL as a byte[] for configuration.
     * The used TSL is not a LOTL.
     * The respective test case is currently under construction
     */
    public static byte[] get_ByteArray_with_TSL() {
        FileInputStream fis = null;

        try {
            fis = new FileInputStream(Path_to_valid_TSL);
        } catch (FileNotFoundException e) {
            Assertions.fail("Invalid path to test TSL!", e);
        }

        byte[] result = null;

        try {
            result = IOUtils.toByteArray(fis);
        } catch (IOException e) {
            Assertions.fail("Unable to transform InputStream to byte[]!", e);
        }

        return result;
    }

    /**
     * Gets a URI String to a valid TSL
     * The used TSL is a LOTL.
     * The respective test case is currently under construction
     */
    public static String get_URIString_with_LOTL() {
        return "file:./" + Path_to_valid_LOTL;
    }

    /**
     * Gets the valid TSL as a FileInputStream for configuration.
     * The used TSL is a LOTL.
     * The respective test case is currently under construction
     */
    public static FileInputStream get_FileInputStream_with_LOTL() {
        FileInputStream fis = null;

        try {
            fis = new FileInputStream(Path_to_valid_LOTL);
        } catch (FileNotFoundException e) {
            Assertions.fail("Invalid path to test TSL!", e);
        }

        return fis;
    }

    /**
     * Gets the valid TSL as a byte[] for configuration.
     * The used TSL is a LOTL.
     * The respective test case is currently under construction
     */
    public static byte[] get_ByteArray_with_LOTL() {
        FileInputStream fis = null;

        try {
            fis = new FileInputStream(Path_to_valid_LOTL);
        } catch (FileNotFoundException e) {
            Assertions.fail("Invalid path to test TSL!", e);
        }

        byte[] result = null;

        try {
            result = IOUtils.toByteArray(fis);
        } catch (IOException e) {
            Assertions.fail("Unable to transform InputStream to byte[]!", e);
        }

        return result;
    }
}
