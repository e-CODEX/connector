package eu.domibus.connector.domain.model;

import org.springframework.core.style.ToStringCreator;

import java.io.Serializable;


/**
 * @author riederb
 * @version 1.0
 */
public class DomibusConnectorAction implements Serializable {
    private Long dbKey;
    private String action;

    /**
     * Default constructor, needed for frameworks
     * to serialize and deserialize objects of this class
     */
    public DomibusConnectorAction() {
    }

    /**
     * @param action action
     */
    public DomibusConnectorAction(final String action) {
        this.action = action;
    }

    public String getAction() {
        return this.action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Long getDbKey() {
        return dbKey;
    }

    public void setDbKey(Long dbKey) {
        this.dbKey = dbKey;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((action == null) ? 0 : action.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DomibusConnectorAction other = (DomibusConnectorAction) obj;
        if (action == null) {
            if (other.action != null)
                return false;
        } else if (!action.equals(other.action))
            return false;
        return true;
    }

    @Override
    public String toString() {
        ToStringCreator builder = new ToStringCreator(this);
        builder.append("action", this.action);
        return builder.toString();
    }
}
