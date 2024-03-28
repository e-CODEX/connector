package eu.ecodex;


import eu.ecodex.evidences.ECodexEvidenceBuilder;
import eu.ecodex.evidences.EvidenceBuilder;
import eu.ecodex.evidences.exception.ECodexEvidenceBuilderException;
import eu.ecodex.evidences.types.ECodexMessageDetails;
import eu.ecodex.signature.EvidenceUtils;
import eu.ecodex.signature.EvidenceUtilsXades;
import eu.spocseu.edeliverygw.configuration.EDeliveryDetails;
import eu.spocseu.edeliverygw.configuration.xsd.EDeliveryDetail;
import eu.spocseu.edeliverygw.configuration.xsd.EDeliveryDetail.PostalAdress;
import eu.spocseu.edeliverygw.configuration.xsd.EDeliveryDetail.Server;
import eu.spocseu.edeliverygw.messageparts.SpocsFragments;
import org.apache.commons.io.FileUtils;
import org.etsi.uri._01903.v1_3.AnyType;
import org.etsi.uri._02640.soapbinding.v1_.*;
import org.etsi.uri._02640.v2.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.util.GregorianCalendar;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class SubmissionAcceptanceRejectionTest {
    private static EvidenceBuilder builder = new ECodexEvidenceBuilder(
            new ClassPathResource("keystore.jks"),
            "JKS",
            "test123",
            "new_Testcert",
            "test123"
    );
    private static EvidenceUtils utils =
            new EvidenceUtilsXades(new ClassPathResource("keystore.jks"), "JKS", "test123", "new_Testcert", "test123");

    private static final String PATH_OUTPUT_FILES = "target/test/SubmissionAcceptanceRejectionTest/";
    private static final String SUBMISSION_ACCEPTANCE_FILE = "submissionAcceptance.xml";
    private static final String RELAYREMMD_ACCEPTANCE_FILE = "relayremmdAcceptance.xml";
    private static final String DELIVERY_ACCEPTANCE_FILE = "deliveryAcceptance.xml";
    private static final String RETRIEVAL_ACCEPTANCE_FILE = "retrievalAcceptance.xml";
    private static final String FAILURE_FILE = "failure.xml";

    @BeforeAll
    public static void setUpTestEnv() throws IOException {
        File testDir = Paths.get(PATH_OUTPUT_FILES).toFile();
        try {
            FileUtils.forceDelete(testDir);
        } catch (IOException e) {
        }
        FileUtils.forceMkdir(testDir);
    }

    private EDeliveryDetails createEntityDetailsObject() {
        PostalAdress address = new PostalAdress();
        address.setCountry("country");
        address.setLocality("locality");
        address.setPostalCode("postalcode");
        address.setStreetAddress("streetaddress");

        Server server = new Server();
        server.setDefaultCitizenQAAlevel(1);
        server.setGatewayAddress("gatewayaddress");
        server.setGatewayDomain("gatewaydomain");
        server.setGatewayName("gatewayname");

        EDeliveryDetail detail = new EDeliveryDetail();

        detail.setPostalAdress(address);
        detail.setServer(server);

        return new EDeliveryDetails(detail);
    }

    @SuppressWarnings("unused")
    private REMDispatchType createRemDispatchTypeObject() throws MalformedURLException, DatatypeConfigurationException {
        GregorianCalendar cal = new GregorianCalendar();
        XMLGregorianCalendar testDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);

        DeliveryConstraints deliveryConstraints = new DeliveryConstraints();
        deliveryConstraints.setAny(new AnyType());
        deliveryConstraints.setInitialSend(testDate);
        deliveryConstraints.setObsoleteAfter(testDate);
        deliveryConstraints.setOrigin(testDate);

        PostalAddressType postAdressType = new PostalAddressType();
        postAdressType.setCountryName("countryName");
        postAdressType.setLang("lang");
        postAdressType.setLocality("locality");
        postAdressType.setPostalCode("postalcode");
        postAdressType.setStateOrProvince("stateOrProvince");

        EntityNameType entityNameType = new EntityNameType();
        entityNameType.setLang("lang");

        NamePostalAddressType namesPostal = new NamePostalAddressType();
        namesPostal.setPostalAddress(postAdressType);
        namesPostal.setEntityName(entityNameType);

        NamesPostalAddressListType namesPostalList = new NamesPostalAddressListType();
        namesPostalList.getNamePostalAddress().add(namesPostal);

        EntityDetailsType recipient = new EntityDetailsType();
        recipient.setNamesPostalAddresses(namesPostalList);
        recipient.getAttributedElectronicAddressOrElectronicAddress()
                 .add(SpocsFragments.createElectoricAddress("stefan.mueller@it.nrw.de", "displayName"));

        Destinations destinations = new Destinations();
        destinations.setRecipient(recipient);

        MsgIdentification msgIdentification = new MsgIdentification();
        msgIdentification.setMessageID("messageID");

        Originators originators = new Originators();
        originators.setFrom(recipient);
        originators.setReplyTo(recipient);
        originators.setSender(recipient);

        MsgMetaData msgMetaData = new MsgMetaData();
        msgMetaData.setDeliveryConstraints(deliveryConstraints);
        msgMetaData.setDestinations(destinations);
        msgMetaData.setMsgIdentification(msgIdentification);
        msgMetaData.setOriginators(originators);

        byte[] contentValue = {0x000A, 0x000A};

        OriginalMsgType orgMsgType = new OriginalMsgType();
        orgMsgType.setContentType("contentType");
        orgMsgType.setSize(BigInteger.valueOf(1000));
        orgMsgType.setValue(contentValue);

        REMDispatchType dispatchMessage = new REMDispatchType();
        dispatchMessage.setId("id");
        dispatchMessage.setMsgMetaData(msgMetaData);
        dispatchMessage.setOriginalMsg(orgMsgType);

        return dispatchMessage;
    }

    @Test
    public void evidenceChain() throws DatatypeConfigurationException, ECodexEvidenceBuilderException, IOException {
        EDeliveryDetails details = createEntityDetailsObject();
        ECodexMessageDetails msgDetails = new ECodexMessageDetails();
        msgDetails.setEbmsMessageId("ebmsMessageId");
        msgDetails.setHashAlgorithm("hashAlgorithm");
        msgDetails.setHashValue("abc".getBytes());
        msgDetails.setNationalMessageId("nationalMessageId");
        msgDetails.setRecipientAddress("recipientAddress");
        msgDetails.setSenderAddress("senderAddress");

        byte[] subm = builder.createSubmissionAcceptanceRejection(true, (EventReasonType) null, details, msgDetails);

        writeFile(subm, PATH_OUTPUT_FILES + SUBMISSION_ACCEPTANCE_FILE);
        assertTrue(utils.verifySignature(subm));

        byte[] relayrem = builder.createRelayREMMDAcceptanceRejection(false, (EventReasonType) null, details, subm);
        writeFile(relayrem, PATH_OUTPUT_FILES + RELAYREMMD_ACCEPTANCE_FILE);
        assertTrue(utils.verifySignature(relayrem));

        byte[] delivery = builder.createDeliveryNonDeliveryToRecipient(true, (EventReasonType) null, details, relayrem);
        writeFile(delivery, PATH_OUTPUT_FILES + DELIVERY_ACCEPTANCE_FILE);
        assertTrue(utils.verifySignature(delivery));

        byte[] retrieval =
                builder.createRetrievalNonRetrievalByRecipient(true, (EventReasonType) null, details, delivery);
        writeFile(retrieval, PATH_OUTPUT_FILES + RETRIEVAL_ACCEPTANCE_FILE);
        assertTrue(utils.verifySignature(retrieval));

        byte[] failure = builder.createRelayREMMDFailure((EventReasonType) null, details, delivery);
        writeFile(failure, PATH_OUTPUT_FILES + FAILURE_FILE);
        assertTrue(utils.verifySignature(retrieval));
    }

    private void writeFile(byte[] data, String fileName) throws IOException {
        FileOutputStream fos = new FileOutputStream(new File(fileName));
        fos.write(data);
        fos.flush();
        fos.close();
    }
}
