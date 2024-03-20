package wp4.testenvironment.NationalDummyImplementations;

import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeFactory;

import eu.ecodex.dss.model.EnvironmentConfiguration;
import eu.ecodex.dss.model.token.AuthenticationInformation;
import eu.ecodex.dss.model.token.TechnicalTrustLevel;
import eu.ecodex.dss.model.token.TechnicalValidationResult;
import eu.ecodex.dss.model.token.Token;
import eu.ecodex.dss.model.token.TokenValidation;
import eu.ecodex.dss.model.token.ValidationVerification;
import eu.ecodex.dss.service.ECodexException;
import eu.ecodex.dss.service.ECodexTechnicalValidationService;
import eu.europa.esig.dss.model.DSSDocument;

public class National_Authentication_TechnicalValidationService implements ECodexTechnicalValidationService {

	private TokenValidation tknValidation;
	private TechnicalValidationResult techResult;
	private AuthenticationInformation authInfo;
	private ValidationVerification valVeri;


	@Override
	public TokenValidation create(DSSDocument businessDocument, DSSDocument detachedSignature) throws ECodexException {
			return tknValidation;
	}

	@Override
	public DSSDocument createReportPDF(Token token) throws ECodexException {
		return null;
	}
	
	public void setValid_AllDataPresent() {
		
		tknValidation = new TokenValidation();
		techResult = new TechnicalValidationResult();
		authInfo = new AuthenticationInformation();
		valVeri = new ValidationVerification();
		
		try{
			authInfo.setIdentityProvider("Identity Inc.");
			authInfo.setUsernameSynonym("John Doe");
			authInfo.setTimeOfAuthentication(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));

			valVeri.setAuthenticationData(authInfo);

			techResult.setTrustLevel(TechnicalTrustLevel.SUCCESSFUL);
			techResult.setComment("This document has been successfully validated");

			tknValidation.setTechnicalResult(techResult);
			tknValidation.setVerificationData(valVeri);
			tknValidation.setVerificationTime(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));

		} catch(Exception e) {
			System.err.println("Exception within the configuration of the authentication based technical validation service:");
			e.printStackTrace();
		}
	}

	// No Result
	public void setInvalid_NullResult() {
		this.tknValidation = null;
	}
	
	// Empty Result
	public void setInvalid_EmptyResult() {
		tknValidation = new TokenValidation();
	}
	
	// Invalid Result
	public void setInvalid_InvalidResult() {
		
		tknValidation = new TokenValidation();
		techResult = new TechnicalValidationResult();
		authInfo = new AuthenticationInformation();
		valVeri = new ValidationVerification();
		
		try{
			authInfo.setIdentityProvider("Identity Inc.");
			authInfo.setUsernameSynonym(null); // Invalid Entry
			authInfo.setTimeOfAuthentication(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));

			valVeri.setAuthenticationData(authInfo);

			techResult.setTrustLevel(TechnicalTrustLevel.SUCCESSFUL);
			techResult.setComment("This document has been successfully validated");

			tknValidation.setTechnicalResult(techResult);
			tknValidation.setVerificationData(valVeri);
			tknValidation.setVerificationTime(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));

		} catch(Exception e) {
			System.err.println("Exception within the configuration of the authentication based technical validation service:");
			e.printStackTrace();
		}
	}
}
