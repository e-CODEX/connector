package eu.domibus.connector.persistence.model;

/**
 * just a marker reference to this package,
 * can be used by spring annotations
 * <p>
 * the P prefix at the classes in this package stands for Persistence
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
public interface PDomibusConnectorPersistenceModel {
    String SEQ_STORE_TABLE_NAME = "DOMIBUS_CONNECTOR_SEQ_STORE";
    String SEQ_NAME_COLUMN_NAME = "SEQ_NAME";
    String SEQ_VALUE_COLUMN_NAME = "SEQ_VALUE";
    int INITIAL_VALUE = 1000;
    int ALLOCATION_SIZE = 1;
    int ALLOCATION_SIZE_BULK = 50;
}
