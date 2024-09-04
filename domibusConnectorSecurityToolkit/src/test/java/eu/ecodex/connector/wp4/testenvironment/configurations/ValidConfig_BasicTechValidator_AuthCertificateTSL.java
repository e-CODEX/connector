/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.wp4.testenvironment.configurations;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;

/**
 * This class provides methods for retrieving a valid TSL (Trust Service List) for configuration.
 * The TSL can be either a LOTL (List of Trusted Lists) or a regular TSL. The methods in this class
 * return the TSL in different formats such as URI string, FileInputStream, and byte array.
 *
 * <p>Currently, the respective test cases for the TSLs are under construction.
 *
 * <p>Note: This class does not provide example code or usage examples. For usage examples,
 * refer to the containing class {@link ValidConfig_BasicTechValidator} and its methods.
 */
@SuppressWarnings("checkstyle:TypeName")
public class ValidConfig_BasicTechValidator_AuthCertificateTSL {
    private static final String PATH_TO_VALID_TSL = "src/test/resources/tsl/ecodex_ger_tsl.xml";
    private static final String PATH_TO_VALID_LOTL = "src/test/resources/tsl/LOTL.xml";

    /**
     * Gets a URI String to a valid TSL The used TSL is not a LOTL. The respective test case is
     * currently under construction.
     */
    public static String get_URIString_with_TSL() {
        return "file:./" + PATH_TO_VALID_TSL;
    }

    /**
     * Gets the valid TSL as a FileInputStream for configuration. The used TSL is not a LOTL. The
     * respective test case is currently under construction.
     */
    public static FileInputStream get_FileInputStream_with_TSL() {
        FileInputStream fis = null;

        try {
            fis = new FileInputStream(PATH_TO_VALID_TSL);
        } catch (FileNotFoundException e) {
            Assertions.fail("Invalid path to test TSL!", e);
        }

        return fis;
    }

    /**
     * Gets the valid TSL as a byte[] for configuration. The used TSL is not a LOTL. The respective
     * test case is currently under construction.
     */
    public static byte[] get_ByteArray_with_TSL() {
        FileInputStream fis = null;

        try {
            fis = new FileInputStream(PATH_TO_VALID_TSL);
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
     * Gets a URI String to a valid TSL The used TSL is a LOTL. The respective test case is
     * currently under construction.
     */
    public static String get_URIString_with_LOTL() {
        return "file:./" + PATH_TO_VALID_LOTL;
    }

    /**
     * Gets the valid TSL as a FileInputStream for configuration. The used TSL is a LOTL. The
     * respective test case is currently under construction.
     */
    public static FileInputStream get_FileInputStream_with_LOTL() {
        FileInputStream fis = null;

        try {
            fis = new FileInputStream(PATH_TO_VALID_LOTL);
        } catch (FileNotFoundException e) {
            Assertions.fail("Invalid path to test TSL!", e);
        }

        return fis;
    }

    /**
     * Gets the valid TSL as a byte[] for configuration. The used TSL is a LOTL. The respective test
     * case is currently under construction.
     */
    public static byte[] get_ByteArray_with_LOTL() {
        FileInputStream fis = null;

        try {
            fis = new FileInputStream(PATH_TO_VALID_LOTL);
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
