/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
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
