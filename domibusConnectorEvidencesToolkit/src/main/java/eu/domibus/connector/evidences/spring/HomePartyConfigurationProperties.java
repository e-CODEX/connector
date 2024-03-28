package eu.domibus.connector.evidences.spring;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;


@Valid
public class HomePartyConfigurationProperties {
    @NotBlank
    private String name;
    @NotBlank
    private String endpointAddress;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEndpointAddress() {
        return endpointAddress;
    }

    public void setEndpointAddress(String endpointAddress) {
        this.endpointAddress = endpointAddress;
    }
}
