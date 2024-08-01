/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.dss.configuration;

import eu.domibus.connector.lib.spring.configuration.StoreConfigurationProperties;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.core.style.ToStringCreator;

/**
 * The TrustListSourceConfigurationProperties class represents the configuration properties for the
 * trust list source. It contains properties for the synchronization duration, trust list sources,
 * and LOTL (List of Trusted Lists) sources.
 */
@Getter
@Setter
public class TrustListSourceConfigurationProperties {
    private Duration tlSync = Duration.ofDays(1);
    private List<TlSourceConfig> tlSources = new ArrayList<>();
    private List<LotlSourceConfig> lotlSources = new ArrayList<>();

    /**
     * The TlSourceConfig class represents the configuration properties for a trust list source. It
     * contains properties for the Trust List URL and the signing certificates.
     */
    @Getter
    @Setter
    public static class TlSourceConfig {
        private String tlUrl = "";
        private StoreConfigurationProperties signingCerts;

        @Override
        public String toString() {
            return new ToStringCreator(this)
                .append("tlUrl", tlUrl)
                .append("signingCerts", signingCerts)
                .toString();
        }
    }

    /**
     * The LotlSourceConfig class represents the configuration properties for a LOTL (List of
     * Trusted Lists) source. It contains properties for the LOTL URL, the signing certificates
     * announcement URI, the signing certificates, and the pivot support flag.
     */
    @Getter
    @Setter
    public static class LotlSourceConfig {
        private String lotlUrl = "https://ec.europa.eu/tools/lotl/eu-lotl.xml";
        private String signingCertificatesAnnouncementUri =
            "https://eur-lex.europa.eu/legal-content/EN/TXT/?uri=uriserv:OJ.C_.2019.276.01.0001.01.ENG";
        StoreConfigurationProperties signingCerts;
        boolean pivotSupport = true;

        @Override
        public String toString() {
            return new ToStringCreator(this)
                .append("lotlUrl", lotlUrl)
                .append("signingCertificatesAnnouncementUri", signingCertificatesAnnouncementUri)
                .append("pivotSupport", pivotSupport)
                .toString();
        }
    }

    /**
     * The FileLoaderProperties class represents the properties for file loading.
     * It contains properties for the cache directory, cache expiration time, and offline mode.
     */
    @Getter
    @Setter
    public static class FileLoaderProperties {
        private Path cacheDirectory;
        private Duration cacheExpirationTime;
        private boolean offline = false;
    }
}
