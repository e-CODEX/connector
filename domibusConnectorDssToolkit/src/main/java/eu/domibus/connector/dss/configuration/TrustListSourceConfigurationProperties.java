package eu.domibus.connector.dss.configuration;

import eu.domibus.connector.lib.spring.configuration.StoreConfigurationProperties;
import org.springframework.core.style.ToStringCreator;

import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class TrustListSourceConfigurationProperties {

    private Duration tlSync = Duration.ofDays(1);

    private List<TlSourceConfig> tlSources = new ArrayList<>();

    private List<LotlSourceConfig> lotlSources = new ArrayList<>();

    public List<TlSourceConfig> getTlSources() {
        return tlSources;
    }

    public void setTlSources(List<TlSourceConfig> tlSources) {
        this.tlSources = tlSources;
    }

    public List<LotlSourceConfig> getLotlSources() {
        return lotlSources;
    }

    public void setLotlSources(List<LotlSourceConfig> lotlSources) {
        this.lotlSources = lotlSources;
    }

    public Duration getTlSync() {
        return tlSync;
    }

    public void setTlSync(Duration tlSync) {
        this.tlSync = tlSync;
    }

    public static class TlSourceConfig {
        private String tlUrl = "";
        private StoreConfigurationProperties signingCerts;

        public String getTlUrl() {
            return tlUrl;
        }

        public void setTlUrl(String tlUrl) {
            this.tlUrl = tlUrl;
        }

        public StoreConfigurationProperties getSigningCerts() {
            return signingCerts;
        }

        public void setSigningCerts(StoreConfigurationProperties signingCerts) {
            this.signingCerts = signingCerts;
        }

        public String toString() {
            return new ToStringCreator(this)
                    .append("tlUrl", tlUrl)
                    .append("signingCerts", signingCerts)
                    .toString();
        }
    }

    public static class LotlSourceConfig {

        private String lotlUrl = "https://ec.europa.eu/tools/lotl/eu-lotl.xml";
        private String signingCertificatesAnnouncementUri = "https://eur-lex.europa.eu/legal-content/EN/TXT/?uri=uriserv:OJ.C_.2019.276.01.0001.01.ENG";
        StoreConfigurationProperties signingCerts;
        boolean pivotSupport = true;

        public String getLotlUrl() {
            return lotlUrl;
        }

        public void setLotlUrl(String lotlUrl) {
            this.lotlUrl = lotlUrl;
        }

        public String getSigningCertificatesAnnouncementUri() {
            return signingCertificatesAnnouncementUri;
        }

        public void setSigningCertificatesAnnouncementUri(String signingCertificatesAnnouncementUri) {
            this.signingCertificatesAnnouncementUri = signingCertificatesAnnouncementUri;
        }

        public StoreConfigurationProperties getSigningCerts() {
            return signingCerts;
        }

        public void setSigningCerts(StoreConfigurationProperties signingCerts) {
            this.signingCerts = signingCerts;
        }

        public boolean isPivotSupport() {
            return pivotSupport;
        }

        public void setPivotSupport(boolean pivotSupport) {
            this.pivotSupport = pivotSupport;
        }

        public String toString() {
            return new ToStringCreator(this)
                    .append("lotlUrl", lotlUrl)
                    .append("signingCertificatesAnnouncementUri", signingCertificatesAnnouncementUri)
                    .append("pivotSupport", pivotSupport)
                    .toString();
        }
    }

    public static class FileLoaderProperties {
        private Path cacheDirectory;
        private Duration cacheExpirationTime;
        private boolean offline = false;

        public Path getCacheDirectory() {
            return cacheDirectory;
        }

        public void setCacheDirectory(Path cacheDirectory) {
            this.cacheDirectory = cacheDirectory;
        }

        public Duration getCacheExpirationTime() {
            return cacheExpirationTime;
        }

        public void setCacheExpirationTime(Duration cacheExpirationTime) {
            this.cacheExpirationTime = cacheExpirationTime;
        }

        public boolean isOffline() {
            return offline;
        }

        public void setOffline(boolean offline) {
            this.offline = offline;
        }
    }


}
