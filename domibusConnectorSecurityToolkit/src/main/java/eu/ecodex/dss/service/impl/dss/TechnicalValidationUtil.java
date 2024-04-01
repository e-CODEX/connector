/*
 * Project: e-CODEX Connector - Container Services/DSS
 * Contractor: ARHS-Developments
 *
 * $HeadURL: http://forge.aris-lux.lan/svn/dgmarktdss/ecodex/src/main/java/eu/ecodex/dss/service/impl/dss
 * /TechnicalValidationUtil.java $
 * $Revision: 1879 $
 * $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 * $Author: meyerfr $
 */
package eu.ecodex.dss.service.impl.dss;

import eu.ecodex.dss.model.token.TechnicalTrustLevel;
import eu.europa.esig.dss.detailedreport.DetailedReport;
import eu.europa.esig.dss.detailedreport.jaxb.*;
import eu.europa.esig.dss.diagnostic.CertificateWrapper;
import eu.europa.esig.dss.diagnostic.DiagnosticData;
import eu.europa.esig.dss.diagnostic.jaxb.XmlDiagnosticData;
import eu.europa.esig.dss.enumerations.SignatureLevel;
import eu.europa.esig.dss.enumerations.SignatureQualification;
import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.simplereport.SimpleReport;
import eu.europa.esig.dss.validation.AdvancedSignature;

import javax.security.auth.x500.X500Principal;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;


/**
 * Provides convenience-methods for Validation report.
 * <p>
 * DISCLAIMER: Project owner e-CODEX
 * </p>
 *
 * @author <a href="mailto:eCodex.Project-DSS@arhs-developments.com">ARHS Developments</a>
 * @version $Revision: 1879 $ - $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 */
class TechnicalValidationUtil {
    /**
     * Get the signing time (via the {@link AdvancedSignature}).
     *
     * @param diagnosticData See {@link DiagnosticData}
     * @param signatureId    signature id
     * @return a {@link javax.xml.datatype.XMLGregorianCalendar}
     * @throws javax.xml.datatype.DatatypeConfigurationException as of the underlying {@link DatatypeFactory}
     */
    public static XMLGregorianCalendar getSigningTime(
            final DiagnosticData diagnosticData,
            final String signatureId) throws DatatypeConfigurationException {

        if (diagnosticData == null) {
            return null;
        }
        final Date signatureDate = diagnosticData.getSignatureDate(signatureId);
        if (signatureDate == null) {
            return null;
        }
        final GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(signatureDate);
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
    }

    public static XMLGregorianCalendar getSigningTime(
            final AdvancedSignature signature,
            final String signatureId) throws DatatypeConfigurationException {

        if (signature == null) {
            return null;
        }
        final Date signatureDate = signature.getSigningTime();
        if (signatureDate == null) {
            return null;
        }
        final GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(signatureDate);
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
    }

    /**
     * Get the signing certificate token (via the {@link AdvancedSignature}).
     *
     * @param signature the signature level analysis
     * @return The signing certificate token
     */
    public static CertificateToken getSigningCertificateToken(final AdvancedSignature signature) {
        if (signature == null) {
            return null;
        }
        final CertificateToken certificateToken = signature.getSigningCertificateToken();
        return certificateToken;
    }

    public static CertificateToken getCertificateToken(final AdvancedSignature signature, String certificateId) {
        if (signature == null) {
            return null;
        }

        CertificateToken certificateToken = null;
        List<CertificateToken> certificateList = signature.getCertificates();

        for (CertificateToken certificate : certificateList) {
            if (certificate.getDSSId().asXmlId().equals(certificateId)) {
                certificateToken = certificate;
                break;
            }
        }

        return certificateToken;
    }

    /**
     * Get the signing certificate (via the {@link AdvancedSignature}).
     *
     * @param certificateToken the certificateToken level analysis
     * @return The signing certificate
     */
    public static X509Certificate getCertificate(final CertificateToken certificateToken) {
        if (certificateToken == null) {
            return null;
        }
        return certificateToken.getCertificate();
    }

    public static CertificateWrapper getCertificateWrapper(
            List<CertificateWrapper> usedCertificates,
            String issuerName) {

        if (usedCertificates == null || issuerName == null || issuerName.equals("")) {
            return null;
        }

        for (CertificateWrapper certificateWrapper : usedCertificates) {
            certificateWrapper.getCertificateDN().equals(issuerName);
            return certificateWrapper;
        }

        return null;
    }

    /**
     * Gets the issuer certificate of the signing certificate: via subjectDN == issuerDN
     *
     * @param certificateToken the certificate token
     * @return The issuer certificate
     */
    public static CertificateToken getIssuerCertificateToken(final CertificateToken certificateToken) {
        if (certificateToken == null) {
            return null;
        }
        if (certificateToken.isSelfSigned()) {
            return certificateToken;
        }

        final CertificateToken issuerCertificateToken = new CertificateToken(getIssuerCertificate(certificateToken));

        return issuerCertificateToken;
    }

    /**
     * Gets the issuer certificate of the signing certificate: via subjectDN == issuerDN
     *
     * @param certificateToken the certificate token
     * @return The issuer certificate
     */
    public static X509Certificate getIssuerCertificate(final CertificateToken certificateToken) {
        final CertificateToken issuerCertificateToken = getIssuerCertificateToken(certificateToken);
        if (issuerCertificateToken == null) {
            return null;
        }
        final X509Certificate issuerCertificate = issuerCertificateToken.getCertificate();
        return issuerCertificate;
    }

    /**
     * Get the issuer name from the signing certificate.
     *
     * @param certificate the signature level analysis
     * @return The signing certificate
     */
    public static String getSigningCertificateIssuerName(final X509Certificate certificate) {
        return (certificate == null) ? null : certificate.getIssuerX500Principal().getName(X500Principal.RFC1779);
    }

    /**
     * Get the subject name from the signing certificate.
     *
     * @param certificate the signature level analysis
     * @return The signing certificate
     */
    public static String getSigningCertificateSubjectName(final X509Certificate certificate) {
        return (certificate == null) ? null : certificate.getSubjectX500Principal().getName(X500Principal.RFC1779);
    }

    /**
     * get the signature format and level (e.g. PAdES-LTV)
     *
     * @param simpleReport The {@link SimpleReport}
     * @param signatureId  signature id
     * @return the format (if present) concatenated with "-" and the level (if present)
     */
    @Deprecated // use getSignatureFormatLvl instead!
    public static String getSignatureFormatLevelAsString(final SimpleReport simpleReport, final String signatureId) {
        return getSignatureFormatLevel(simpleReport, signatureId)
                .map(SignatureLevel::toString)
                .orElse(null);
    }

    public static Optional<SignatureLevel> getSignatureFormatLevel(
            final SimpleReport simpleReport,
            final String signatureId) {
        if (simpleReport == null) {
            return Optional.empty();
        }
        final SignatureLevel format = simpleReport.getSignatureFormat(signatureId);
        return Optional.ofNullable(format);
    }

    /**
     * Get the signature level. If the signature information are incomplete , it return by default UNDETERMINED.
     *
     * @param simpleReport The {@link SimpleReport}
     * @param signatureId  signature id
     * @return The result
     */
    public static SignatureQualification getSignatureConclusion(
            final SimpleReport simpleReport,
            final String signatureId) {

        if (simpleReport == null) {
            return SignatureQualification.NA;
        }

        return simpleReport.getSignatureQualification(signatureId);
    }

    /**
     * Check if the signature is mathematically correct.
     *
     * @param simpleReport The {@link SimpleReport}
     * @param signatureId  signature id
     * @return The result
     */
    public static boolean checkSignatureCorrectness(final SimpleReport simpleReport, final String signatureId) {
        if (simpleReport == null) {
            return false;
        }
        final boolean result = simpleReport.isValid(signatureId);
        return result;
    }

    /**
     * Search the signing certificate in the Certificate path revocation analysis, compare the two's certificates and
     * check if the status is valid.
     *
     * @param certificateToken The {@link CertificateToken}
     * @return The result (a {@link TechnicalTrustLevel}, where {@link TechnicalTrustLevel#SUFFICIENT} means that no
     * online source could be contacted)
     */
    public static TechnicalTrustLevel checkCertificateRevocation(
            final CertificateToken certificateToken,
            boolean... revoked) {

        if (certificateToken == null) {
            return TechnicalTrustLevel.FAIL;
        }

        if (revoked.length == 0) {
            if (certificateToken.isSelfSigned()) {
                return TechnicalTrustLevel.SUCCESSFUL;
            } else {
                // as defined by http://www.jira.e-codex.eu/browse/ECDX-25
                // 4-3. Being valid at the time of signing with an CRL and/or an OCSP being defined, but none of them
                // is reachable: YELLOW
                return TechnicalTrustLevel.SUFFICIENT;
            }
        }

        return revoked[0] ? TechnicalTrustLevel.FAIL : TechnicalTrustLevel.SUCCESSFUL;
    }

    /**
     * Search the signing signatureId in the Certificate path revocation analysis, compare the two's certificates and
     * check the validity at singing time. Or: use directly the signatureId to compute the result.
     *
     * @param certificateToken The {@link CertificateToken}
     * @param signingTime
     * @return the result (a {@link TechnicalTrustLevel}, where {@link TechnicalTrustLevel#SUFFICIENT} means that no
     * online source could be contacted)
     */
    public static TechnicalTrustLevel checkCertificateValidity(
            final CertificateToken certificateToken,
            XMLGregorianCalendar signingTime) {

        if (certificateToken == null) {
            return TechnicalTrustLevel.FAIL;
        }
        final boolean valid = certificateToken.isValidOn(signingTime.toGregorianCalendar().getTime());
        return valid ? TechnicalTrustLevel.SUCCESSFUL : TechnicalTrustLevel.FAIL;
    }

    /**
     * @param diagnosticData The {@link DiagnosticData}
     * @param certificateId  The {@link String}
     * @return The result
     */
    public static boolean checkTrustAnchor(DiagnosticData diagnosticData, String certificateId) {
        XmlDiagnosticData model = (diagnosticData == null) ? null : diagnosticData.getJaxbModel();
        List<eu.europa.esig.dss.diagnostic.jaxb.XmlCertificate> certificates =
                (model == null) ? null : model.getUsedCertificates();

        if (certificates == null) {
            return false;
        }

        for (eu.europa.esig.dss.diagnostic.jaxb.XmlCertificate xmlCertificate : certificates) {
            if (xmlCertificate.getId() != null && xmlCertificate.getId()
                                                                .equals(certificateId) && xmlCertificate.isTrusted()) {
                return true;
            }
        }

        return false;
    }

    public static boolean checkSignatureConclusion(
            SimpleReport simpleReport,
            DetailedReport detailedReport,
            String signatureId) {

        SignatureQualification conclusion = getSignatureConclusion(simpleReport, signatureId);

        switch (conclusion) {
            case NA:
                if (detailedReport != null) {

                    XmlSignature signature = detailedReport.getXmlSignatureById(signatureId);
                    XmlValidationSignatureQualification signatureQualification =
                            (signature == null) ? null : signature.getValidationSignatureQualification();
                    List<XmlConstraint> signatureConstraints =
                            (signatureQualification == null) ? null : signatureQualification.getConstraint();

                    if (signatureConstraints == null || signatureConstraints.isEmpty()) {
                        return false;
                    } else {
                        for (XmlConstraint curConstraint : signatureConstraints) {
                            XmlMessage curName = (curConstraint == null) ? null : curConstraint.getName();
                            String nameId = (curName == null) ? null : curName.getValue();

                            XmlStatus curStatus = (curConstraint == null) ? null : curConstraint.getStatus();
                            String finalStatus = (curStatus == null) ? null : curStatus.value();

                            if (nameId.equals("QUAL_IS_ADES") && finalStatus.equals("OK")) {
                                return true;
                            } else if (nameId.equals("QUAL_IS_ADES_IND") && finalStatus.equals("OK")) {
                                return true;
                            }
                        }
                    }
                }

                return false;

            case NOT_ADES:
            case NOT_ADES_QC:
            case NOT_ADES_QC_QSCD:
                return false;
            //		case ADES:
            //		case ADES_QC:
            case ADESEAL:
            case ADESEAL_QC:
            case ADESIG:
            case ADESIG_QC:
                //		case INDETERMINATE_ADES:
                //		case INDETERMINATE_ADES_QC:
            case INDETERMINATE_ADESEAL:
            case INDETERMINATE_ADESEAL_QC:
            case INDETERMINATE_ADESIG:
            case INDETERMINATE_ADESIG_QC:
                //		case INDETERMINATE_QES:
            case INDETERMINATE_QESEAL:
            case INDETERMINATE_QESIG:
                //		case QES:
            case QESIG:
            case QESEAL:
                return true;
            default:
                return false;
        }
    }
}
