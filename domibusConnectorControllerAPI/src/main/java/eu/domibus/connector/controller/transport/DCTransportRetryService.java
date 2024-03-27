package eu.domibus.connector.controller.transport;

import eu.domibus.connector.domain.model.DomibusConnectorTransportStep;

public interface DCTransportRetryService {
    /**
     * trigger the retry of the transport step
     * @param step the step to retry
     */
    void retryTransport(DomibusConnectorTransportStep step);

    /**
     * A message can only be retried if it is an evidence message OR
     *  if it is a business message which has not been rejected or confirmed yet!
     * @param step the transport step to retry
     * @return is the transport step retryable
     */
    boolean isRetryAble(DomibusConnectorTransportStep step);
}
