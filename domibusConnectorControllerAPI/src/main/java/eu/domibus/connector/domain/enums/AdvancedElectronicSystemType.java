/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.domain.enums;

/**
 * Represents the types of advanced electronic systems.
 *
 * <p>Enum Values:
 * <ul>
 * <li>SIGNATURE_BASED: Represents a system that is based on electronic signatures.</li>
 * <li>AUTHENTICATION_BASED: Represents a system that is based on authentication.</li>
 * </ul>
 */
public enum AdvancedElectronicSystemType {
    SIGNATURE_BASED,
    AUTHENTICATION_BASED;
}
