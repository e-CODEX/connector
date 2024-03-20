package eu.domibus.connector.security.configuration;

import eu.domibus.connector.common.annotations.ConnectorPropertyConverter;
import eu.domibus.connector.domain.enums.AdvancedElectronicSystemType;
import org.apache.commons.collections4.set.ListOrderedSet;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@ConnectorPropertyConverter
public class OrderedSetConverter implements Converter<ListOrderedSet<AdvancedElectronicSystemType>, String> {

    @Override
    public String convert(ListOrderedSet<AdvancedElectronicSystemType> source) {
        return source.stream().map(Enum::toString).collect(Collectors.joining(","));
    }

}
