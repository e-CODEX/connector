/*
 * Copyright 2025 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.persistence.service;

/**
 * Service interface for handling operations related to the database.
 */
public interface DCDatabaseService {
    /**
     * Cleans processed messages and related data from the database.
     *
     * <p>
     * This method is typically used to remove processes messages
     * and related data from the database.
     */
    void clean();
}
