/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.evidences.spring;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * Configuration properties for a home party in a home party config file.
 */
@Getter
@Setter
@Valid
public class HomePartyConfigurationProperties {
    @NotBlank
    private String name;
    @NotBlank
    private String endpointAddress;
}
