/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.utils.service;

import lombok.Getter;
import lombok.Setter;

/**
 * ExtendsMyTestProperties3 extends the MyTestProperties3 class and adds two additional properties:
 * prop3 and prop4.
 */
@Getter
@Setter
public class ExtendsMyTestProperties3 extends MyTestProperties3 {
    private String prop3;
    private String prop4;
}
