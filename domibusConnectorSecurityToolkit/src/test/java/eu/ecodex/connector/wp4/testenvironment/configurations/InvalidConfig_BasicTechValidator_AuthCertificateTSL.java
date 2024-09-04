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

/**
 * The InvalidConfig_BasicTechValidator_AuthCertificateTSL class represents a utility class that
 * provides paths to various invalid test files for authentication certificate configuration .
 *
 * <p>This class should not be used directly by external applications. It is intended for internal
 * use only.
 */
@SuppressWarnings("checkstyle:TypeName")
public class InvalidConfig_BasicTechValidator_AuthCertificateTSL {
    private static String Invalid_Path = "I/dont/exist.xml";
    private static String Path_to_RandomXML =
        "src/test/resources/tsl/invalid_testentries/Formsheet_A_sample1.xml";
    private static String Path_to_RandomFile =
        "src/test/resources/tsl/invalid_testentries/Attachment2.bmp";
    private static String Path_to_LOTL_with_RandomFile =
        "src/test/resources/tsl/invalid_testentries/LOTL_RandomXML.xml";
    private static String Path_to_LOTL_with_RandomXML =
        "src/test/resources/tsl/invalid_testentries/LOTL_RandomFile.xml";

    public static String get_Invalid_Path() {
        return "file:./" + Invalid_Path;
    }

    public static String get_RandomXML() {
        return "file:./" + Path_to_RandomXML;
    }

    public static String get_RandomFile() {
        return "file:./" + Path_to_RandomFile;
    }

    public static String get_LOTL_with_RandomFile() {
        return "file:./" + Path_to_LOTL_with_RandomFile;
    }

    public static String get_LOTL_with_RandomXML() {
        return "file:./" + Path_to_LOTL_with_RandomXML;
    }
}
