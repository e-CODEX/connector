/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
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
