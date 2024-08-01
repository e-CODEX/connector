/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.domain.model.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.TextNode;
import eu.domibus.connector.domain.model.LargeFileReference;
import java.io.IOException;

/**
 * The LargeFileDeserializer class is a custom deserializer for deserializing LargeFileReference
 * objects from JSON.
 */
public class LargeFileDeserializer extends StdDeserializer<LargeFileReference> {
    protected LargeFileDeserializer(Class<?> vc) {
        super(vc);
    }

    protected LargeFileDeserializer(JavaType valueType) {
        super(valueType);
    }

    protected LargeFileDeserializer(StdDeserializer<?> src) {
        super(src);
    }

    @Override
    public LargeFileReference deserialize(JsonParser p, DeserializationContext ctxt)
        throws IOException {
        var largeFileReference = new LargeFileReference();
        TreeNode treeNode = p.getCodec().readTree(p);
        largeFileReference.setStorageIdReference(((TextNode) treeNode.get(
            LargeFileReferenceSerializer.STORAGE_ID_REFERENCE_FIELD_NAME)
                                                 ).asText());
        largeFileReference.setStorageProviderName(((TextNode) treeNode.get(
            LargeFileReferenceSerializer.STORAGE_PROVIDER_FIELD_NAME)
                                                  ).asText());
        largeFileReference.setMimetype(
            getStringOrNull(LargeFileReferenceSerializer.MIME_TYPE_FIELD_NAME, treeNode));
        largeFileReference.setName(
            getStringOrNull(LargeFileReferenceSerializer.NAME_FIELD_NAME, treeNode));
        largeFileReference.setText(
            getStringOrNull(LargeFileReferenceSerializer.TEXT_FIELD_NAME, treeNode));
        largeFileReference.setSize(
            ((IntNode) treeNode.get(LargeFileReferenceSerializer.SIZE_FIELD_NAME)).asLong());
        return largeFileReference;
    }

    private String getStringOrNull(String fieldName, TreeNode treeNode) {
        String val = null;
        var treeNode1 = treeNode.get(fieldName);
        if (treeNode1 instanceof TextNode textNode) {
            val = textNode.asText();
        }
        return val;
    }
}
