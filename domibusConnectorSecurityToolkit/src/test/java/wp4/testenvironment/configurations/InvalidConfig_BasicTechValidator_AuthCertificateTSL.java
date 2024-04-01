package wp4.testenvironment.configurations;

public class InvalidConfig_BasicTechValidator_AuthCertificateTSL {
    private static final String Invalid_Path = "I/dont/exist.xml";
    private static final String Path_to_RandomXML = "src/test/resources/tsl/invalid_testentries/Formsheet_A_sample1.xml";
    private static final String Path_to_RandomFile = "src/test/resources/tsl/invalid_testentries/Attachment2.bmp";
    private static final String Path_to_LOTL_with_RandomFile =
            "src/test/resources/tsl/invalid_testentries/LOTL_RandomXML.xml";
    private static final String Path_to_LOTL_with_RandomXML =
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
