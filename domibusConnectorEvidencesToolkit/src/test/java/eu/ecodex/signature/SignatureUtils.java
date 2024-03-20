package eu.ecodex.signature;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.security.KeyException;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.GregorianCalendar;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FilenameUtils;
import org.etsi.uri._01903.v1_3.AnyType;
import org.etsi.uri._02640.soapbinding.v1_.DeliveryConstraints;
import org.etsi.uri._02640.soapbinding.v1_.Destinations;
import org.etsi.uri._02640.soapbinding.v1_.MsgIdentification;
import org.etsi.uri._02640.soapbinding.v1_.MsgMetaData;
import org.etsi.uri._02640.soapbinding.v1_.OriginalMsgType;
import org.etsi.uri._02640.soapbinding.v1_.Originators;
import org.etsi.uri._02640.soapbinding.v1_.REMDispatchType;
import org.etsi.uri._02640.v2.EntityDetailsType;
import org.etsi.uri._02640.v2.EntityNameType;
import org.etsi.uri._02640.v2.NamePostalAddressType;
import org.etsi.uri._02640.v2.NamesPostalAddressListType;
import org.etsi.uri._02640.v2.ObjectFactory;
import org.etsi.uri._02640.v2.PostalAddressType;
import org.etsi.uri._02640.v2.REMEvidenceType;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import eu.spocseu.edeliverygw.JaxbContextHolder;
import eu.spocseu.edeliverygw.configuration.EDeliveryDetails;
import eu.spocseu.edeliverygw.configuration.xsd.EDeliveryDetail;
import eu.spocseu.edeliverygw.configuration.xsd.EDeliveryDetail.PostalAdress;
import eu.spocseu.edeliverygw.configuration.xsd.EDeliveryDetail.Server;
import eu.spocseu.edeliverygw.evidences.SubmissionAcceptanceRejection;
import eu.spocseu.edeliverygw.messageparts.SpocsFragments;

/**
 * This is a simple example of generating an Enveloped XML Signature using the
 * JSR 105 API. The resulting signature will look like (key and signature values
 * will be different):
 * 
 * <pre>
 * <code>
 * <Envelope xmlns="urn:envelope">
 *  <Signature xmlns="http://www.w3.org/2000/09/xmldsig#">
 *    <SignedInfo>
 *      <CanonicalizationMethod Algorithm="http://www.w3.org/TR/2001/REC-xml-c14n
 * -20010315"/>
 *      <SignatureMethod Algorithm="http://www.w3.org/2000/09/xmldsig#dsa-sha1"/>
 *      <Reference URI="">
 *        <Transforms>
 *          <Transform Algorithm="http://www.w3.org/2000/09/xmldsig#enveloped-signature"/>
 *        </Transforms>
 *        <DigestMethod Algorithm="http://www.w3.org/2000/09/xmldsig#sha1"/>
 *        <DigestValue>K8M/lPbKnuMDsO0Uzuj75lQtzQI=<DigestValue>
 *      </Reference>
 *    </SignedInfo>
 *    <SignatureValue>
 *      DpEylhQoiUKBoKWmYfajXO7LZxiDYgVtUtCNyTgwZgoChzorA2nhkQ==
 *    </SignatureValue>
 *    <KeyInfo>
 *      <KeyValue>
 *        <DSAKeyValue>
 *          <P>
 *            rFto8uPQM6y34FLPmDh40BLJ1rVrC8VeRquuhPZ6jYNFkQuwxnu/wCvIAMhukPBL
 *            FET8bJf/b2ef+oqxZajEb+88zlZoyG8g/wMfDBHTxz+CnowLahnCCTYBp5kt7G8q
 *            UobJuvjylwj1st7V9Lsu03iXMXtbiriUjFa5gURasN8=
 *          </P>
 *          <Q>
 *            kEjAFpCe4lcUOdwphpzf+tBaUds=
 *          </Q>
 *          <G>
 *            oe14R2OtyKx+s+60O5BRNMOYpIg2TU/f15N3bsDErKOWtKXeNK9FS7dWStreDxo2
 *            SSgOonqAd4FuJ/4uva7GgNL4ULIqY7E+mW5iwJ7n/WTELh98mEocsLXkNh24HcH4
 *            BZfSCTruuzmCyjdV1KSqX/Eux04HfCWYmdxN3SQ/qqw=
 *          </G>
 *          <Y>
 *            pA5NnZvcd574WRXuOA7ZfC/7Lqt4cB0MRLWtHubtJoVOao9ib5ry4rTk0r6ddnOv
 *            AIGKktutzK3ymvKleS3DOrwZQgJ+/BDWDW8kO9R66o6rdjiSobBi/0c2V1+dkqOg
 *            jFmKz395mvCOZGhC7fqAVhHat2EjGPMfgSZyABa7+1k=
 *          </Y>
 *        </DSAKeyValue>
 *      </KeyValue>
 *    </KeyInfo>
 *  </Signature>
 * </Envelope>
 *  </code>
 * </pre>
 */
 class SignatureUtils {

	public static void main(String[] args) throws FileNotFoundException,
			SAXException, IOException, ParserConfigurationException,
			TransformerException, DatatypeConfigurationException, JAXBException {
		// //
		// SignatureUtils.signEnveloped("D:\\git\\ecodex_evidences\\EvidencesModel\\src\\test\\resources\\SubmissionAcceptance.xsd");

		// String documentToBeSigned =
		// "D:\\git\\ecodex_evidences\\EvidencesModel\\src\\test\\resources\\SubmissionAcceptance.xsd";
		//
		// DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		// dbf.setNamespaceAware(true);
		// // Document doc = dbf.newDocumentBuilder().parse(new
		// FileInputStream(documentToBeSigned));
		// Document doc = dbf.newDocumentBuilder().newDocument();
		//
		// SignatureUtils test = new SignatureUtils();
		// SubmissionAcceptanceRejection jaxbObject =
		// test.createSubmissionAcceptance();
		//
		// JaxbContextHolder.getSpocsJaxBContext().createMarshaller().marshal(new
		// ObjectFactory()
		// .createSubmissionAcceptanceRejection(jaxbObject.getJaxBObj()), doc);
		//
		// Document signedDocument = SignatureUtils.signDomDocument(doc);
		//
		// OutputStream os;
		// String filename = FilenameUtils.getBaseName(documentToBeSigned);
		// String extension = FilenameUtils.getExtension(documentToBeSigned);
		// os = new
		// FileOutputStream(FilenameUtils.getFullPath(documentToBeSigned) +
		// filename + "_signed." + extension);
		//
		// TransformerFactory tf = TransformerFactory.newInstance();
		// Transformer trans = tf.newTransformer();
		// trans.transform(new DOMSource(signedDocument), new StreamResult(os));

		SignatureUtils test = new SignatureUtils();
		SubmissionAcceptanceRejection evidence = test
				.createSubmissionAcceptance();

		SignatureUtils
				.signEnveloped("D:\\git\\ecodex_evidences\\EvidencesModel\\src\\test\\resources\\SubmissionAcceptance.xsd");

	}

	public static void signEnveloped(String documentToBeSigned) {

		// Create a DOM XMLSignatureFactory that will be used to generate the
		// enveloped signature

		try {
			XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");

			// Create a Reference to the enveloped document (in this case we are
			// signing the whole document, so a URI of "" signifies that) and
			// also specify the SHA1 digest algorithm and the ENVELOPED
			// Transform.
			Reference ref = fac.newReference("", fac.newDigestMethod(
					DigestMethod.SHA1, null), Collections.singletonList(fac
					.newTransform(Transform.ENVELOPED,
							(TransformParameterSpec) null)), null, null);

			// Create the SignedInfo
			SignedInfo si = fac.newSignedInfo(fac.newCanonicalizationMethod(
					CanonicalizationMethod.INCLUSIVE_WITH_COMMENTS,
					(C14NMethodParameterSpec) null), fac.newSignatureMethod(
					SignatureMethod.RSA_SHA1, null), Collections
					.singletonList(ref));

			// Load Private and Public Key from JavaKeyStore
			KeyPair kp = getKeyPairFromKeyStore(
					"D:\\git\\ecodex_evidences\\EvidencesModel\\src\\main\\resources\\evidenceBuilderStore.jks",
					"123456", "evidenceBuilderKey", "123456");

			// Create a KeyValue containing the PublicKey from the JKS
			KeyInfoFactory kif = fac.getKeyInfoFactory();
			KeyValue kv;

			kv = kif.newKeyValue(kp.getPublic());
			// Create a KeyInfo and add the KeyValue to it
			KeyInfo ki = kif.newKeyInfo(Collections.singletonList(kv));

			// Instantiate the document to be signed
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			Document doc;
			doc = dbf.newDocumentBuilder().parse(
					new FileInputStream(documentToBeSigned));

			// Create a DOMSignContext and specify the PrivateKey and
			// location of the resulting XMLSignature's parent element
			DOMSignContext dsc = new DOMSignContext(kp.getPrivate(),
					doc.getDocumentElement());

			// Create the XMLSignature (but don't sign it yet)
			XMLSignature signature = fac.newXMLSignature(si, ki);

			// Marshal, generate (and sign) the enveloped signature
			signature.sign(dsc);

			// output the resulting document
			OutputStream os;
			String filename = FilenameUtils.getBaseName(documentToBeSigned);
			String extension = FilenameUtils.getExtension(documentToBeSigned);
			os = new FileOutputStream(
					FilenameUtils.getFullPath(documentToBeSigned) + filename
							+ "_signed." + extension);

			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer trans = tf.newTransformer();
			trans.transform(new DOMSource(doc), new StreamResult(os));
		} catch (SAXException | KeyException | InvalidAlgorithmParameterException | NoSuchAlgorithmException | XMLSignatureException | MarshalException | TransformerException | ParserConfigurationException | IOException e) {
			throw new RuntimeException(e);
		}

	}

	public static byte[] signByteArray(byte[] xmlData) {
		byte[] signedByteArray = null;

		// Create a DOM XMLSignatureFactory that will be used to generate the
		// enveloped signature

		try {
			XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");

			// Create a Reference to the enveloped document (in this case we are
			// signing the whole document, so a URI of "" signifies that) and
			// also specify the SHA1 digest algorithm and the ENVELOPED
			// Transform.
			Reference ref = fac.newReference("", fac.newDigestMethod(
					DigestMethod.SHA1, null), Collections.singletonList(fac
					.newTransform(Transform.ENVELOPED,
							(TransformParameterSpec) null)), null, null);

			// Create the SignedInfo
			SignedInfo si = fac.newSignedInfo(fac.newCanonicalizationMethod(
					CanonicalizationMethod.INCLUSIVE_WITH_COMMENTS,
					(C14NMethodParameterSpec) null), fac.newSignatureMethod(
					SignatureMethod.RSA_SHA1, null), Collections
					.singletonList(ref));

			// Load KeyPair from Java Key Store
			KeyPair kp = getKeyPairFromKeyStore(
					"D:\\git\\ecodex_evidences\\EvidencesModel\\src\\main\\resources\\evidenceBuilderStore.jks",
					"123456", "evidenceBuilderKey", "123456");

			// Create a KeyValue containing the DSA PublicKey that was generated
			KeyInfoFactory kif = fac.getKeyInfoFactory();
			KeyValue kv;

			kv = kif.newKeyValue(kp.getPublic());
			// Create a KeyInfo and add the KeyValue to it
			KeyInfo ki = kif.newKeyInfo(Collections.singletonList(kv));

			// Instantiate the document to be signed
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			Document doc;
			doc = dbf.newDocumentBuilder().parse(
					new ByteArrayInputStream(xmlData));

			// Create a DOMSignContext and specify the DSA PrivateKey and
			// location of the resulting XMLSignature's parent element
			DOMSignContext dsc = new DOMSignContext(kp.getPrivate(),
					doc.getDocumentElement());

			// Create the XMLSignature (but don't sign it yet)
			XMLSignature signature = fac.newXMLSignature(si, ki);

			// Marshal, generate (and sign) the enveloped signature
			signature.sign(dsc);

			// output the resulting document
			// OutputStream os;
			// os = new
			// FileOutputStream("D:\\git\\ecodex_evidences\\EvidencesModel\\src\\test\\resources\\SubmissionAcceptance_signed.xsd");
			//
			// TransformerFactory tf = TransformerFactory.newInstance();
			// Transformer trans = tf.newTransformer();
			// trans.transform(new DOMSource(doc), new StreamResult(os));

			ByteArrayOutputStream bos = new ByteArrayOutputStream();

			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer trans = tf.newTransformer();
			trans.transform(new DOMSource(doc), new StreamResult(bos));

			signedByteArray = bos.toByteArray();

		} catch (KeyException e1) {
			e1.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (MarshalException e) {
			e.printStackTrace();
		} catch (XMLSignatureException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		}

		return signedByteArray;

	}

	public static Document signDomDocument(Document document) {
		Document signedDocument = null;

		// Create a DOM XMLSignatureFactory that will be used to generate the
		// enveloped signature

		try {
			XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");

			// Create a Reference to the enveloped document (in this case we are
			// signing the whole document, so a URI of "" signifies that) and
			// also specify the SHA1 digest algorithm and the ENVELOPED
			// Transform.
			Reference ref = fac.newReference("", fac.newDigestMethod(
					DigestMethod.SHA1, null), Collections.singletonList(fac
					.newTransform(Transform.ENVELOPED,
							(TransformParameterSpec) null)), null, null);

			// Create the SignedInfo
			SignedInfo si = fac.newSignedInfo(fac.newCanonicalizationMethod(
					CanonicalizationMethod.INCLUSIVE_WITH_COMMENTS,
					(C14NMethodParameterSpec) null), fac.newSignatureMethod(
					SignatureMethod.RSA_SHA1, null), Collections
					.singletonList(ref));

			// Create a DSA KeyPair
			// KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
			// kpg.initialize(2048);
			// KeyPair kp = kpg.generateKeyPair();
			KeyPair kp = getKeyPairFromKeyStore(
					"D:\\git\\ecodex_evidences\\EvidencesModel\\src\\main\\resources\\evidenceBuilderStore.jks",
					"123456", "evidenceBuilderKey", "123456");

			// Create a KeyValue containing the DSA PublicKey that was generated
			KeyInfoFactory kif = fac.getKeyInfoFactory();
			KeyValue kv;

			kv = kif.newKeyValue(kp.getPublic());
			// Create a KeyInfo and add the KeyValue to it
			KeyInfo ki = kif.newKeyInfo(Collections.singletonList(kv));

			// Instantiate the document to be signed
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			Document doc;
			// doc = dbf.newDocumentBuilder().parse(new
			// FileInputStream(documentToBeSigned));
			doc = document;

			// Create a DOMSignContext and specify the DSA PrivateKey and
			// location of the resulting XMLSignature's parent element
			DOMSignContext dsc = new DOMSignContext(kp.getPrivate(),
					doc.getDocumentElement());

			// Create the XMLSignature (but don't sign it yet)
			XMLSignature signature = fac.newXMLSignature(si, ki);

			// Marshal, generate (and sign) the enveloped signature
			signature.sign(dsc);

			// output the resulting document
			// OutputStream os;
			// String filename = FilenameUtils.getBaseName(documentToBeSigned);
			// String extension =
			// FilenameUtils.getExtension(documentToBeSigned);
			// os = new
			// FileOutputStream(FilenameUtils.getFullPath(documentToBeSigned) +
			// filename + "_signed." + extension);

			// TransformerFactory tf = TransformerFactory.newInstance();
			// Transformer trans = tf.newTransformer();
			// trans.transform(new DOMSource(doc), new StreamResult(os));

			signedDocument = doc;

		} catch (KeyException e1) {
			e1.printStackTrace();
		} catch (MarshalException e) {
			e.printStackTrace();
		} catch (XMLSignatureException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		}

		return signedDocument;
	}

	public static JAXBElement<REMEvidenceType> signDOMDocument(Document document) {
		return SignatureUtils.convertIntoREMEvidenceType(SignatureUtils
				.signDomDocument(document));
	}

	public static JAXBElement<REMEvidenceType> signEvidence(
			REMEvidenceType evidence) {
		JAXBElement<REMEvidenceType> signedJaxBObject = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);

		try {
			Document unsignedDOMDocument = dbf.newDocumentBuilder()
					.newDocument();
			JaxbContextHolder
					.getSpocsJaxBContext()
					.createMarshaller()
					.marshal(
							new ObjectFactory()
									.createSubmissionAcceptanceRejection(evidence),
							unsignedDOMDocument);

			signedJaxBObject = SignatureUtils
					.signDOMDocument(unsignedDOMDocument);

		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

		return signedJaxBObject;
	}

	public static JAXBElement<REMEvidenceType> convertIntoREMEvidenceType(
			Document domDocument) {
		// JaxbContextHolder.getSpocsJaxBContext().createMarshaller().marshal(new
		// ObjectFactory()
		// .createSubmissionAcceptanceRejection(jaxbObject.getJaxBObj()), doc);
		JAXBElement<REMEvidenceType> jaxbObj = null;

		try {
			System.out.println("vor dem cast");
			jaxbObj = JaxbContextHolder.getSpocsJaxBContext()
					.createUnmarshaller()
					.unmarshal(domDocument, REMEvidenceType.class);
			System.out.println("nach dem cast");
		} catch (JAXBException e) {
			e.printStackTrace();
		}

		return jaxbObj;
	}

	private static KeyPair getKeyPairFromKeyStore(String store,
			String storePass, String alias, String keyPass) {
		KeyStore ks;
		FileInputStream kfis;
		KeyPair keyPair = null;

		Key key = null;
		PublicKey publicKey = null;
		PrivateKey privateKey = null;
		try {
			ks = KeyStore.getInstance("JKS");
			kfis = new FileInputStream(store);
			ks.load(kfis, storePass.toCharArray());
			if (ks.containsAlias(alias)) {
				key = ks.getKey(alias, keyPass.toCharArray());
				if (key instanceof PrivateKey) {
					X509Certificate cert = (X509Certificate) ks
							.getCertificate(alias);
					publicKey = cert.getPublicKey();
					privateKey = (PrivateKey) key;
					keyPair = new KeyPair(publicKey, privateKey);
				} else {
					keyPair = null;
				}
			} else {
				keyPair = null;
			}
		} catch (UnrecoverableKeyException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return keyPair;
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

	private REMDispatchType createRemDispatchTypeObject()
			throws MalformedURLException, DatatypeConfigurationException {
		GregorianCalendar cal = new GregorianCalendar();
		XMLGregorianCalendar testDate = DatatypeFactory.newInstance()
				.newXMLGregorianCalendar(cal);

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
		recipient.getAttributedElectronicAddressOrElectronicAddress().add(
				SpocsFragments.createElectoricAddress(
						"stefan.mueller@it.nrw.de", "displayName"));

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

		byte[] contentValue = { 0x000A, 0x000A };

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

	private SubmissionAcceptanceRejection createSubmissionAcceptance()
			throws DatatypeConfigurationException, JAXBException,
			MalformedURLException, FileNotFoundException {
		EDeliveryDetails details = createEntityDetailsObject();

		REMDispatchType dispatchMessage = createRemDispatchTypeObject();

		SubmissionAcceptanceRejection evidence = new SubmissionAcceptanceRejection(
				details, dispatchMessage, true);

		FileOutputStream fo = new FileOutputStream(
				"src/test/resources/SubmissionAcceptance.xml");
		evidence.serialize(fo);

		return evidence;
	}

}
