package wp4.testenvironment.NationalDummyImplementations;

import eu.ecodex.dss.model.token.LegalTrustLevel;
import eu.ecodex.dss.model.token.LegalValidationResult;
import eu.ecodex.dss.model.token.Token;
import eu.ecodex.dss.service.ECodexException;
import eu.ecodex.dss.service.ECodexLegalValidationService;


public class National_LegalValidationService implements
        ECodexLegalValidationService {

    private LegalValidationResult result;

    //	@Override
    //	public void setEnvironmentConfiguration(EnvironmentConfiguration conf) {
    //	}

    @Override
    public LegalValidationResult create(Token token) throws ECodexException {
        return result;
    }

    public void setValid_AllDataPresent() {
        result = new LegalValidationResult();
        result.setDisclaimer("Just a disclaimer");
        result.setTrustLevel(LegalTrustLevel.SUCCESSFUL);
    }

    public void setValid_NoDisclaimer() {
        result = new LegalValidationResult();
        result.setTrustLevel(LegalTrustLevel.SUCCESSFUL);
    }

    public void setInvalid_NullResult() {
        this.result = null;
    }

    public void setInvalid_EmptyResult() {
        result = new LegalValidationResult();
    }

    public void setInvalid_MissingTrustLevel() {
        result = new LegalValidationResult();
        result.setTrustLevel(null);
    }
}
