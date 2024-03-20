package eu.domibus.connector.domain.model;

import java.io.*;
import java.time.ZonedDateTime;
import java.util.Objects;

import javax.activation.DataSource;
import javax.validation.constraints.NotNull;

import org.springframework.core.style.ToStringCreator;
import org.springframework.lang.Nullable;

/**
 * Represents a reference to a storage system for big files
 *
 */
public class LargeFileReference implements DataSource, Serializable {

    private String storageProviderName = "";

    private String storageIdReference = "";

    private String name = "";

    private String mimetype = "";

    private Long size = -1l;

    private String text = "";

    private ZonedDateTime creationDate;

    public LargeFileReference() {}

    public LargeFileReference(@NotNull String storageIdReference) {
        if (storageIdReference == null) {
            throw new IllegalArgumentException("StorageIdReference cannot be null!");
        }
        this.storageIdReference = storageIdReference;
    }

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

    public void setStorageIdReference(String storageIdReference) {
        this.storageIdReference = storageIdReference;
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
     * Is readable if a valid input stream can be returned
     * @return - if it's readable
     */
    public boolean isReadable() { return false; }
    
    /**
     * @return is true if a writeable output stream can be returned!
     */
    public boolean isWriteable() { return false; }
    
    @Override
    public String getContentType() {
        return this.mimetype;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setMimetype(String contentType) {
        this.mimetype = contentType;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStorageProviderName() {
        return storageProviderName;
    }

    public void setStorageProviderName(String storageProviderName) {
        this.storageProviderName = storageProviderName;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ZonedDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(ZonedDateTime creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public String toString() {
        ToStringCreator builder = new ToStringCreator(this);
        builder.append("storageReference", this.getStorageIdReference());
        builder.append("provider", this.getStorageProviderName());
        return builder.toString();        
    }

    @Override
    public int hashCode() {
        int hash = 7;
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
        if (!Objects.equals(this.storageIdReference, other.storageIdReference)) {
            return false;
        }
        return true;
    }



}
