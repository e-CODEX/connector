/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.backend.persistence;

/**
 * The PersistencePackage interface represents a package that contains data related to persistence.
 * It serves as a marker interface for classes that are involved in persistence operations.
 *
 * <p>Implementing classes should provide the necessary functionality to perform persistence
 * operations such as saving and retrieving data from a persistence store (e.g., a database, file
 * system, etc.).
 *
 * <p>This interface does not define any specific methods as it is meant to be a base interface for
 * different persistence package implementations.
 *
 * <p>Classes that implement this interface should provide detailed documentation about their
 * specific functionality and usage.
 */
public interface PersistencePackage {
}
