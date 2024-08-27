/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.controller.transport;

import eu.domibus.connector.domain.model.DomibusConnectorTransportStep;

/**
 * The DCTransportRetryService interface provides methods to initiate the retry of a transport
 * step and check if a transport step can be retried.
 */
public interface DCTransportRetryService {

    /**
     * Trigger the retry of the transport step.
     *
     * @param step the transport step to retry
     */
    void retryTransport(DomibusConnectorTransportStep step);

    /**
     * A message can only be retried if it is an evidence message OR
     *  if it is a business message which has not been rejected or confirmed yet.
     *
     * @param step the transport step to retry
     * @return is the transport step retryable
     */
    boolean isRetryAble(DomibusConnectorTransportStep step);

}
