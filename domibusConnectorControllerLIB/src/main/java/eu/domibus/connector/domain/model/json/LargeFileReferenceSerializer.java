package eu.domibus.connector.domain.model.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import eu.domibus.connector.domain.model.LargeFileReference;

import java.io.IOException;


public class LargeFileReferenceSerializer extends StdSerializer<LargeFileReference> {
    public static final String STORAGE_ID_REFERENCE_FIELD_NAME = "reference";
    public static final String STORAGE_PROVIDER_FIELD_NAME = "provider";
    public static final String NAME_FIELD_NAME = "name";
    public static final String MIME_TYPE_FIELD_NAME = "mimeType";
    public static final String TEXT_FIELD_NAME = "text";
    public static final String SIZE_FIELD_NAME = "size";

    protected LargeFileReferenceSerializer(Class<LargeFileReference> t) {
        super(t);
    }

    public LargeFileReferenceSerializer(JavaType type) {
        super(type);
    }

    public LargeFileReferenceSerializer(StdSerializer<?> src) {
        super(src);
    }

    @Override
    public void serialize(LargeFileReference value, JsonGenerator jgen, SerializerProvider provider) throws
            IOException {
        jgen.writeStartObject();
        jgen.writeStringField(STORAGE_ID_REFERENCE_FIELD_NAME, value.getStorageIdReference());
        jgen.writeStringField(STORAGE_PROVIDER_FIELD_NAME, value.getStorageProviderName());
        jgen.writeStringField(NAME_FIELD_NAME, value.getName());
        jgen.writeStringField(MIME_TYPE_FIELD_NAME, value.getContentType());
        jgen.writeStringField(TEXT_FIELD_NAME, value.getText());
        jgen.writeNumberField(SIZE_FIELD_NAME, value.getSize());
        jgen.writeEndObject();
    }
}

