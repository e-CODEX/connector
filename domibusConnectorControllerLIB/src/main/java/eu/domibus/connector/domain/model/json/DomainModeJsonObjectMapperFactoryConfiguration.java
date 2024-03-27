package eu.domibus.connector.domain.model.json;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import eu.domibus.connector.common.annotations.DomainModelJsonObjectMapper;
import eu.domibus.connector.domain.model.LargeFileReference;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class DomainModeJsonObjectMapperFactoryConfiguration {
    @Bean(name = DomainModelJsonObjectMapper.VALUE)
    public ObjectMapper getMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        SimpleModule largeFileReferenceModule = new SimpleModule();
        largeFileReferenceModule.addSerializer(
                LargeFileReference.class,
                new LargeFileReferenceSerializer(LargeFileReference.class)
        );
        largeFileReferenceModule.addDeserializer(
                LargeFileReference.class,
                new LargeFileDeserializer(LargeFileReference.class)
        );

        mapper.registerModule(largeFileReferenceModule);
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }
}
