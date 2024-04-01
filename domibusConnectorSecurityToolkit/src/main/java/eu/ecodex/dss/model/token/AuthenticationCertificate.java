// New class, created by klara

package eu.ecodex.dss.model.token;

/**
 * This class holds information about the validation result for a certificate used for an authentication-based system.
 *
 * <p>DISCLAIMER: Project owner e-CODEX</p>
 *
 * @author e-CODEX WP4
 */

public class AuthenticationCertificate {

    protected boolean validationSuccessful;

    public AuthenticationCertificate() {
        this.validationSuccessful = false;
    }

    public boolean isValidationSuccessful() {
        return validationSuccessful;
    }

    public void setValidationSuccessful(boolean validationSuccessful) {
        this.validationSuccessful = validationSuccessful;
    }
}
