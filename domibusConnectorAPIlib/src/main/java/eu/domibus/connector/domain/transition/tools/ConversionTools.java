package eu.domibus.connector.domain.transition.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.validation.constraints.NotNull;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.nio.file.Files;


/**
 * This class is intended to hold methods to convert all kind of content needed to process messages.
 * Since some of the methods are required for the tools around the domibusConnector as well (domibusConnectorClient,
 * domibus-connector-plugin), this class should help prevent multiple code snippets that are the same.
 *
 * @author riederb
 */
public class ConversionTools {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConversionTools.class);

    /**
     * takes a source element and converts with
     * Transformer to a byte[] backed by ByteArrayOutputStream
     *
     * @param xmlInput - the Source
     * @return the byte[]
     * @throws RuntimeException - in case of any error! // TODO: improve exceptions
     */
    @NotNull
    public static byte[] convertXmlSourceToByteArray(@NotNull Source xmlInput) {
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            StreamResult xmlOutput = new StreamResult(new ByteArrayOutputStream());
            transformer.transform(xmlInput, xmlOutput);

            ByteArrayOutputStream bos = (ByteArrayOutputStream) xmlOutput.getOutputStream();
            return bos.toByteArray();
        } catch (IllegalArgumentException | TransformerException e) {
            throw new RuntimeException("Exception occurred during transforming xml into byte[]", e);
        }
    }

    public static Source convertFileToStreamSource(File inputFile) {
        return new javax.xml.transform.stream.StreamSource(inputFile);
    }

    public static DataHandler convertFileToDataHandler(File inputFile, String mimeType) {
        byte[] bytes = null;
        try {
            bytes = Files.readAllBytes(inputFile.toPath());
        } catch (IOException e) {
            LOGGER.error("Exception converting file to bytes: ", e);
        }
        return convertByteArrayToDataHandler(bytes, mimeType);
    }

    public static Source convertInputStreamToStreamSource(InputStream is) {
        return new javax.xml.transform.stream.StreamSource(is);
    }

    public static Source convertByteArrayToStreamSource(byte[] bytes) {
        return new StreamSource(new ByteArrayInputStream(bytes));
    }

    /**
     * Converts a {@link javax.activation.DataHandler} into a byte[]
     *
     * @param dataHandler
     * @return
     */
    @NotNull
    public static byte[] convertDataHandlerToByteArray(@NotNull DataHandler dataHandler) {
        try {
            InputStream inputStream = dataHandler.getInputStream();
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[1024];
            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }

            buffer.flush();
            return buffer.toByteArray();
        } catch (IOException ex) {
            LOGGER.error("IO Exception occurred while reading InputStream provided over network", ex);
            throw new RuntimeException("Cannot be mapped!", ex);
        }
    }

    public static DataHandler convertByteArrayToDataHandler(byte[] bytes, String mimeType) {
        DataSource dataSource = new ByteArrayDataSource(bytes, mimeType);
        return new DataHandler(dataSource);
    }

    public static DataHandler convertXMLSourceToDataHandler(Source streamSource) {
        byte[] content = convertXmlSourceToByteArray(streamSource);
        return convertByteArrayToDataHandler(content, "text/xml");
    }

    private static class ByteArrayDataSource implements DataSource {
        private final byte[] buffer;
        private final String mimeType;

        public ByteArrayDataSource(byte[] buffer, String mimeType) {
            this.buffer = buffer;
            this.mimeType = mimeType;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(buffer);
        }

        @Override
        public OutputStream getOutputStream() throws IOException {
            throw new UnsupportedOperationException("Read Only Data Source!");
        }

        @Override
        public String getContentType() {
            return this.mimeType;
        }

        @Override
        public String getName() {
            return "";
        }
    }
}
