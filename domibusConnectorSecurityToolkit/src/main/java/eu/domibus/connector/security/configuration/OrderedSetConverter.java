/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.security.configuration;

import eu.domibus.connector.common.annotations.ConnectorPropertyConverter;
import eu.domibus.connector.domain.enums.AdvancedElectronicSystemType;
import java.util.stream.Collectors;
import org.apache.commons.collections4.set.ListOrderedSet;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Converts a ListOrderedSet of AdvancedElectronicSystemType objects to a string representation.
 *
 * <p>This class is a component and a connector property converter. It is used to convert the
 * ListOrderedSet&lt;AdvancedElectronicSystemType&gt; type to a String type. The conversion is done
 * by joining the string representation of each element in the set with a comma delimiter.
 *
 * <p>The class implements the Converter interface with the generic types of
 * ListOrderedSet&lt;AdvancedElectronicSystemType&gt; as the source type and String as the target
 * type. The convert method is overridden to perform the conversion.
 *
 * @see Converter
 * @see ListOrderedSet
 */
@Component
@ConnectorPropertyConverter
public class OrderedSetConverter
    implements Converter<ListOrderedSet<AdvancedElectronicSystemType>, String> {
    @Override
    public String convert(ListOrderedSet<AdvancedElectronicSystemType> source) {
        return source.stream().map(Enum::toString).collect(Collectors.joining(","));
    }
}
