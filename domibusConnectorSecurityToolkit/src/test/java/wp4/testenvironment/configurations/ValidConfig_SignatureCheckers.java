package wp4.testenvironment.configurations;

import eu.ecodex.dss.model.ECodexContainer;
import eu.ecodex.dss.model.SignatureCheckers;
import eu.ecodex.dss.service.impl.dss.DSSSignatureChecker;
import eu.europa.esig.dss.spi.x509.CertificateSource;
import eu.europa.esig.dss.spi.x509.KeyStoreCertificateSource;
import eu.europa.esig.dss.validation.CertificateVerifier;
import eu.europa.esig.dss.validation.executor.signature.DefaultSignatureProcessExecutor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;


public class ValidConfig_SignatureCheckers {
    public static SignatureCheckers getSignatureCheckers() {
        return new SignatureCheckers(
                asicsSignatureChecker(),
                xmlTokenSignatureChecker(),
                pdfTokenSignatureChecker()
        );
    }

    private static DSSSignatureChecker<ECodexContainer.TokenPdfTypeECodex> pdfTokenSignatureChecker() {
        return DSSSignatureChecker.builder()
                                  .withSignatureCheckerName("TokenPdfSignatureChecker")
                                  .withCertificateVerifier(eCodexContainerCertificateVerifier())
                                  .withValidationConstraints(signatureValidationConstraintsXml())
                                  .withProcessExecutor(new DefaultSignatureProcessExecutor())
                                  .build(new ECodexContainer.TokenPdfTypeECodex())
                ;
    }

    private static DSSSignatureChecker<ECodexContainer.TokenXmlTypesECodex> xmlTokenSignatureChecker() {
        return DSSSignatureChecker.builder()
                                  .withSignatureCheckerName("TokenPdfSignatureChecker")
                                  .withCertificateVerifier(eCodexContainerCertificateVerifier())
                                  .withValidationConstraints(signatureValidationConstraintsXml())
                                  .withProcessExecutor(new DefaultSignatureProcessExecutor())
                                  .build(new ECodexContainer.TokenXmlTypesECodex())
                ;
    }

    private static DSSSignatureChecker<ECodexContainer.AsicDocumentTypeECodex> asicsSignatureChecker() {
        return DSSSignatureChecker.builder()
                                  .withCertificateVerifier(eCodexContainerCertificateVerifier())
                                  .withSignatureCheckerName("TokenPdfSignatureChecker")
                                  .withValidationConstraints(signatureValidationConstraintsXml())
                                  .withProcessExecutor(new DefaultSignatureProcessExecutor())
                                  .withConnectorCertificateSource(connectorCertificateSource())
                                  .build(new ECodexContainer.AsicDocumentTypeECodex())
                ;
    }

    private static CertificateSource connectorCertificateSource() {
        try {
            return new KeyStoreCertificateSource(
                    new ClassPathResource("/keystores/signature_store.jks").getInputStream(),
                    "JKS",
                    "teststore"
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Resource signatureValidationConstraintsXml() {
        return new ClassPathResource("/102853/container_constraint.xml");
    }

    private static CertificateVerifier eCodexContainerCertificateVerifier() {
        try {
            return ValidConfig_CertificateVerifier.get_NoProxy();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
