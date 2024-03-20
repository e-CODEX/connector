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
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is intended to hold methods to convert all kind of content needed to process messages.
 * Since some of the methods are required for the tools around the domibusConnector as well (domibusConnectorClient, domibus-connector-plugin), this
 * class should help prevent multiple code snippets that are the same.
 * 
 * @author riederb
 *
 */
public class ConversionTools {

	private final static Logger LOGGER = LoggerFactory.getLogger(ConversionTools.class);
	
	/**
     * takes a source element and converts with
     * Transformer to an byte[] backed by ByteArrayOutputStream
     *
     * @param xmlInput - the Source
     * @return the byte[]
     * @throws RuntimeException - in case of any error! //TODO: improve exceptions
     */
    @NotNull
    public static byte[] convertXmlSourceToByteArray(@NotNull Source xmlInput) {
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            StreamResult xmlOutput=new StreamResult(new ByteArrayOutputStream());
            transformer.transform(xmlInput, xmlOutput);
           
            ByteArrayOutputStream bos = (ByteArrayOutputStream) xmlOutput.getOutputStream();
            byte[] content = bos.toByteArray();
			return content;
        } catch (IllegalArgumentException | TransformerException e) {
            throw new RuntimeException("Exception occured during transforming xml into byte[]", e);
        }
    }
    
    public static Source convertFileToStreamSource(File inputFile) {
    	final Source xmlSource = new javax.xml.transform.stream.StreamSource(inputFile);
    	return xmlSource;
    }
    
    public static DataHandler convertFileToDataHandler(File inputFile, String mimeType) {
    	byte[] bytes = null;
		try {
			bytes = Files.readAllBytes(inputFile.toPath());
		} catch (IOException e) {
			LOGGER.error("Exception converting file to bytes: ",e);
		}
    	DataHandler dh = convertByteArrayToDataHandler(bytes, mimeType);
    	return dh;
    }
    
    public static Source convertInputStreamToStreamSource(InputStream is) {
    	final Source xmlSource = new javax.xml.transform.stream.StreamSource(is);
    	return xmlSource;
    }
    
    public static Source convertByteArrayToStreamSource(byte[] bytes) {
    	final Source xmlSource = new StreamSource(new ByteArrayInputStream(bytes));
    	return xmlSource;
    }
    
    
    
    /**
     * Converts a {@link javax.activation.DataHandler} into a byte[]
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
            byte[] byteArray = buffer.toByteArray();
            
            return byteArray;
//            Object content = dataHandler.getContent();
//            if (content instanceof byte[]) {
//                byte[] byteArray = (byte[]) content;
//                return byteArray;
//            } else if (content instanceof InputStream) {
//            	
//                InputStream is = (InputStream) content;
//				byte[] copyToByteArray = StreamUtils.copyToByteArray(is);
//				is.close();
//                return copyToByteArray;
//            } else {
//                LOGGER.error("Cannot map content [{}] to byte[]", content);
//                throw new RuntimeException("Cannot map content!");
//            }
        } catch (IOException ex) {
            LOGGER.error("IO Exception occured while reading InputStream provided over network", ex);
            throw new RuntimeException("Cannot be mapped!", ex);
        }
    }
    
    public static DataHandler convertByteArrayToDataHandler(byte[] bytes, String mimeType) {
    	DataSource dataSource = new ByteArrayDataSource(bytes, mimeType);
		DataHandler dh = new DataHandler(dataSource);
		return dh;
	}

    public static DataHandler convertXMLSourceToDataHandler(Source streamSource) {
    	byte[] content = convertXmlSourceToByteArray(streamSource);
    	DataHandler dh = convertByteArrayToDataHandler(content, "text/xml");
    	return dh;
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
