/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.persistence.service;

import eu.ecodex.connector.domain.model.DomibusConnectorAction;
import eu.ecodex.connector.domain.model.DomibusConnectorBusinessDomain;
import eu.ecodex.connector.domain.model.DomibusConnectorPModeSet;
import eu.ecodex.connector.domain.model.DomibusConnectorParty;
import eu.ecodex.connector.domain.model.DomibusConnectorService;
import eu.ecodex.connector.persistence.service.exceptions.IncorrectResultSizeException;
import java.util.List;
import java.util.Optional;

/**
 * This service offers access to the current p mode configuration of a message lane and also
 * provides methods to change the current p-mode set of a message lane.
 */
public interface DomibusConnectorPModeService {
    /**
     * Will check if the current p-Mode set for this message lane contains the requested action.
     * Only the action name will be looked up if any matching action is found it will be returned.
     * null value acts as a wildcard
     *
     * @param lane   - the MessageLaneConfiguration which is asked
     * @param action - the action
     * @return a list of services
     */
    List<DomibusConnectorAction> findByExample(DomibusConnectorBusinessDomain.BusinessDomainId lane,
                                               DomibusConnectorAction action);

    /**
     * Will check if the current p-Mode set for this message lane contains the requested service. Ff
     * any matching service is found it will be returned within the list a null value acts as
     * wildcard
     *
     * @param lane                    - the MessageLaneConfiguration which is asked
     * @param domibusConnectorService the service
     * @return a list of matching services
     */
    List<DomibusConnectorService> findByExample(
        DomibusConnectorBusinessDomain.BusinessDomainId lane,
        DomibusConnectorService domibusConnectorService);

    /**
     * Will check if the current p-Mode set for this message lane contains the requested party. If a
     * DomibusConnectorParty property is null it will be ignored on the search This means if only
     * the partyId is set on the provided party, then only the partyId will be compared.
     *
     * @param lane                  - the MessageLaneConfiguration which is asked
     * @param domibusConnectorParty the DomibusConnectorParty from the configuration where all
     *                              properties are filled
     * @return A list of matching DomibusConnectorParties
     */
    List<DomibusConnectorParty> findByExample(DomibusConnectorBusinessDomain.BusinessDomainId lane,
                                              DomibusConnectorParty domibusConnectorParty)
        throws IncorrectResultSizeException;

    /**
     * Will check if the current p-Mode set for this message lane contains the requested action.
     * Only the action name will be looked up if any matching action is found it will be returned
     *
     * @param lane   - the MessageLaneConfiguration which is asked
     * @param action - the action
     * @return the domibusConnectorService empty Optional if no service was found the
     *      domibusConnectorService where all attributes are filled
     */
    Optional<DomibusConnectorAction> getConfiguredSingle(
        DomibusConnectorBusinessDomain.BusinessDomainId lane, DomibusConnectorAction action);

    /**
     * Will check if the current p-Mode set for this message lane contains the requested service.
     * Only the attribute service will be looked up if any matching service is found it will be
     * returned
     *
     * @param lane                    - the MessageLaneConfiguration which is asked
     * @param domibusConnectorService the service
     * @return the domibusConnectorService empty Optional if no service was found the
     *      domibusConnectorService where all attributes are filled
     */
    Optional<DomibusConnectorService> getConfiguredSingle(
        DomibusConnectorBusinessDomain.BusinessDomainId lane,
        DomibusConnectorService domibusConnectorService);

    /**
     * Will check if the current p-Mode set for this message lane contains the requested party. If a
     * DomibusConnectorParty property is null it will be ignored on the search This means if only
     * the partyId is set on the provided party, then only the partyId will be compared
     *
     * @param lane                  - the MessageLaneConfiguration which is asked
     * @param domibusConnectorParty the DomibusConnectorParty from the configuration where all
     *                              properties are filled
     * @return the DomibusConnectorParty empty Optional if no matching party was found the
     *      domibusConnectorService where all attributes are filled
     * @throws IncorrectResultSizeException if more than one Party was found
     */
    Optional<DomibusConnectorParty> getConfiguredSingle(
        DomibusConnectorBusinessDomain.BusinessDomainId lane,
        DomibusConnectorParty domibusConnectorParty) throws IncorrectResultSizeException;

    /**
     * Updates the PMode configuration set with the given connectorPModeSet.
     *
     * @param connectorPModeSet - this PModeSet will become the new current pModeSet
     */
    void updatePModeConfigurationSet(DomibusConnectorPModeSet connectorPModeSet);

    /**
     * Retrieves the current PModeSet for the specified message lane.
     *
     * @param lane - the MessageLaneConfiguration which is changed
     * @return the current PModeSet of the given MessageLane
     */
    Optional<DomibusConnectorPModeSet> getCurrentPModeSet(
        DomibusConnectorBusinessDomain.BusinessDomainId lane);

    void updateActivePModeSetDescription(DomibusConnectorPModeSet connectorPModeSet);

    List<DomibusConnectorPModeSet> getInactivePModeSets(
        DomibusConnectorBusinessDomain.BusinessDomainId lane);
}
