/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.model;

import jakarta.activation.DataSource;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.style.ToStringCreator;
import org.springframework.lang.Nullable;

/**
 * Represents a reference to a storage system for big files.
 */
@Data
@NoArgsConstructor
public class LargeFileReference implements DataSource, Serializable {
    private String storageProviderName = "";
    private String storageIdReference = "";
    private String name = "";
    private String mimetype = "";
    private Long size = -1L;
    private String text = "";
    private ZonedDateTime creationDate;

    /**
     * Represents a large file reference.
     *
     * @param storageIdReference The unique identifier for the storage of the file.
     */
    public LargeFileReference(@NotNull String storageIdReference) {
        if (storageIdReference == null) {
            throw new IllegalArgumentException("StorageIdReference cannot be null!");
        }
        this.storageIdReference = storageIdReference;
    }

    /**
     * Creates a new LargeFileReference object by copying the fields from another LargeFileReference
     * object.
     *
     * @param ref The reference to copy fields from
     */
    public LargeFileReference(LargeFileReference ref) {
        this.storageIdReference = ref.storageIdReference;
        this.storageProviderName = ref.storageProviderName;
        this.name = ref.name;
        this.mimetype = ref.mimetype;
        this.size = ref.size;
        this.text = ref.text;
        this.creationDate = ref.creationDate;
    }

    public @Nullable
    String getStorageIdReference() {
        return storageIdReference;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        throw new IOException("not initialized yet!");
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        throw new IOException("not initialized yet!");
    }

    /**
     * Is readable if a valid input stream can be returned.
     *
     * @return - if it's readable
     */
    public boolean isReadable() {
        return false;
    }

    /**
     * Determines if the file is writeable.
     *
     * @return true if the file is writeable, false otherwise.
     */
    public boolean isWriteable() {
        return false;
    }

    @Override
    public String getContentType() {
        return this.mimetype;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        var builder = new ToStringCreator(this);
        builder.append("storageReference", this.getStorageIdReference());
        builder.append("provider", this.getStorageProviderName());
        return builder.toString();
    }

    @Override
    public int hashCode() {
        var hash = 7;
        hash = 37 * hash + Objects.hashCode(this.storageIdReference);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LargeFileReference other = (LargeFileReference) obj;
        return Objects.equals(this.storageIdReference, other.storageIdReference);
    }
}
