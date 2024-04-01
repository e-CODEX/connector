package eu.domibus.connector.ui.dto;

public class WebMessageFile {
    private String fileName;
    private WebMessageFileType fileType;
    private byte[] fileContent;

    public WebMessageFile() {
        // TODO Auto-generated constructor stub
    }

    /**
     * @param fileName
     * @param fileType
     * @param fileContent
     */
    public WebMessageFile(String fileName, WebMessageFileType fileType, byte[] fileContent) {
        super();
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileContent = fileContent;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public WebMessageFileType getFileType() {
        return fileType;
    }

    public void setFileType(WebMessageFileType fileType) {
        this.fileType = fileType;
    }

    public byte[] getFileContent() {
        return fileContent;
    }

    public void setFileContent(byte[] fileContent) {
        this.fileContent = fileContent;
    }
}
