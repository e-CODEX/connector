/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.domain.transition.tools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.validation.constraints.NotNull;
import javax.xml.XMLConstants;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import lombok.experimental.UtilityClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is intended to hold methods to convert all kind of content needed to process messages.
 * Since some of the methods are required for the tools around the domibusConnector
 * as well (domibusConnectorClient, domibus-connector-plugin), this class should help prevent
 * multiple code snippets that are the same.
 *
 * @author riederb
 */
@UtilityClass
public class ConversionTools {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConversionTools.class);
    private static final TransformerFactory transformerFactory = getTransformerFactory();

    /**
     * takes a source element and converts with
     * Transformer to a byte[] backed by ByteArrayOutputStream.
     *
     * @param xmlInput - the Source
     * @return the byte[]
     * @throws RuntimeException - in case of any error!
     */
    @NotNull
    public static byte[] convertXMLSourceToByteArray(@NotNull Source xmlInput) {
        try {
            var transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            var xmlOutput = new StreamResult(new ByteArrayOutputStream());
            transformer.transform(xmlInput, xmlOutput);

            ByteArrayOutputStream bos = (ByteArrayOutputStream) xmlOutput.getOutputStream();
            return bos.toByteArray();
        } catch (IllegalArgumentException | TransformerException e) {
            throw new RuntimeException("Exception occurred during transforming xml into byte[]", e);
        }
    }

    private static TransformerFactory getTransformerFactory() {
        TransformerFactory transformerFactory;
        try {
            transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");

        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Error setting TransformerFactory attribute", e);
        }

        return transformerFactory;
    }

    public static Source convertFileToStreamSource(File inputFile) {
        return new StreamSource(inputFile);
    }

    /**
     * Converts a file to a DataHandler object with the specified MIME type.
     *
     * @param inputFile The file to be converted.
     * @param mimeType  The MIME type of the data.
     * @return The converted DataHandler object.
     */
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
        return new StreamSource(is);
    }

    public static Source convertByteArrayToStreamSource(byte[] bytes) {
        return new StreamSource(new ByteArrayInputStream(bytes));
    }

    /**
     * Converts a {@link javax.activation.DataHandler} object to a byte[].
     *
     * @param dataHandler The DataHandler object to be converted.
     * @return The byte array representation of the DataHandler.
     * @throws RuntimeException If an IO Exception occurs while reading the InputStream
     *                          provided over the network.
     */
    @NotNull
    public static byte[] convertDataHandlerToByteArray(@NotNull DataHandler dataHandler) {
        try {
            var inputStream = dataHandler.getInputStream();
            var buffer = new ByteArrayOutputStream();
            int bytesRead;
            var data = new byte[1024];
            while ((bytesRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, bytesRead);
            }

            buffer.flush();

            return buffer.toByteArray();
        } catch (IOException ex) {
            LOGGER.error(
                "IO Exception occured while reading InputStream provided over network", ex);
            throw new RuntimeException("Cannot be mapped!", ex);
        }
    }

    /**
     * Converts a byte array to a DataHandler object with the specified MIME type.
     *
     * @param bytes    The byte array to be converted.
     * @param mimeType The MIME type of the data.
     * @return The converted DataHandler object.
     */
    public static DataHandler convertByteArrayToDataHandler(byte[] bytes, String mimeType) {
        DataSource dataSource = new ByteArrayDataSource(bytes, mimeType);
        return new DataHandler(dataSource);
    }

    /**
     * Converts a given XML Source to a DataHandler object.
     *
     * @param streamSource The XML Source to convert.
     * @return The converted DataHandler object.
     */
    public static DataHandler convertXMLSourceToDataHandler(Source streamSource) {
        byte[] content = convertXMLSourceToByteArray(streamSource);
        return convertByteArrayToDataHandler(content, "text/xml");
    }

    private static class ByteArrayDataSource implements DataSource {
        private byte[] buffer;
        private String mimeType;

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
