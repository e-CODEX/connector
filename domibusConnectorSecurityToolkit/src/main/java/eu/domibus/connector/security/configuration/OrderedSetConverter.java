/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
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
