/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.ui.dbtables;

import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.Query;
import jakarta.annotation.security.RolesAllowed;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.sql.DataSource;
import lombok.Data;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

/**
 * This class provides a service for interacting with database tables. It allows users to retrieve
 * table and column information, perform CRUD operations on table columns, and fetch and update
 * data.
 */
@RolesAllowed("ADMIN")
public class DbTableService {
    private static final Logger LOGGER = LogManager.getLogger(DbTableService.class);
    private final DataSource ds;
    private final DbTableServiceConfigurationProperties config;
    private final EntityManager entityManager;
    private final NamedParameterJdbcTemplate jdbcTemplate;

    /**
     * Constructor.
     *
     * @param entityManager the entity manager used for database operations
     * @param ds            the data source for the database
     * @param config        the configuration properties for the service
     */
    public DbTableService(
        EntityManager entityManager,
        DataSource ds, DbTableServiceConfigurationProperties config) {
        this.config = config;
        this.entityManager = entityManager;
        this.ds = ds;
        this.jdbcTemplate = new NamedParameterJdbcTemplate(ds);
    }

    public List<String> getTables() {
        return config.getTables();
    }

    /**
     * Retrieves the list of column definitions for a given table.
     *
     * @param table the name of the table
     * @return the list of column definitions for the table
     */
    public List<ColumnDefinition> getColumns(String table) {
        return new ArrayList<>(getTableDefinition(table)
                                   .columnDefinitionMap
                                   .values());
    }

    /**
     * Retrieves the table definition for the specified table name.
     *
     * @param tableName the name of the table for which to retrieve the definition
     * @return the table definition for the specified table name
     * @throws RuntimeException if an SQLException occurs while retrieving the table definition
     */
    public TableDefinition getTableDefinition(String tableName) {
        var tableDefinition = new TableDefinition(tableName);
        // read columns
        try (var conn = ds.getConnection();
             ResultSet rs = conn
                 .getMetaData()
                 .getColumns(null, null, tableName, null)
        ) {

            while (rs.next()) {
                var columnDefinition = new ColumnDefinition();
                var columnName = rs.getString(4); // get column name
                columnDefinition.setColumnName(columnName);
                columnDefinition.setDataType(rs.getString(5)); // java.sql.Type
                columnDefinition.setOrdinalPosition(rs.getInt(17)); // ORDINAL_POSITION
                tableDefinition.columnDefinitionMap.put(columnName, columnDefinition);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        // read primary keys
        try (var connection = ds.getConnection();
             ResultSet rs = connection
                 .getMetaData()
                 .getPrimaryKeys(null, null, tableName)
        ) {

            while (rs.next()) {
                var columnName = rs.getString(4);
                // int keySequence = rs.getInt(5);
                // String pkName = rs.getString(6);
                tableDefinition.addPrimaryKey(columnName);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return tableDefinition;
    }

    public DataProvider<ColumnRow, Object> getDataProvider(TableDefinition tableDefinition) {
        return new DbTableDataProvider(entityManager, tableDefinition);
    }

    /**
     * Deletes a row from the database table based on the provided {@link ColumnRow} object.
     *
     * @param columnRow the {@link ColumnRow} object representing the row to be deleted
     */
    public void deleteColumn(ColumnRow columnRow) {
        var parameterSource = new MapSqlParameterSource();
        var tableDefinition = columnRow.getTableDefinition();

        // set key values
        columnRow.getPrimaryKey()
                 .entrySet()
                 .forEach(
                     entry -> parameterSource.addValue("key_" + entry.getKey(), entry.getValue()));
        var sql = String.format("DELETE %s WHERE %s", tableDefinition.getTableName(),
                                tableDefinition.getPrimaryKeyQuery()
        );

        jdbcTemplate.update(sql, parameterSource);
    }

    /**
     * Updates a row in the database table with the values provided in the given {@link ColumnRow}.
     * The update is performed based on the primary key values.
     *
     * @param item the {@link ColumnRow} containing the updated values and primary key values
     * @see ColumnRow
     */
    public void updateColumn(ColumnRow item) {
        var tableDefinition = item.getTableDefinition();
        var parameterSource = new MapSqlParameterSource();

        // create set part
        var setPart = tableDefinition.getColumnDefinitionMap()
                                     .keySet()
                                     .stream()
                                     .map(c -> String.format("%s=:%s", c, c))
                                     .collect(Collectors.joining(", "));

        // set set values
        tableDefinition.getColumnDefinitionMap()
                       .keySet()
                       .forEach(column -> parameterSource.addValue(column, item.getCell(column)));

        // define primary key query part
        String primaryKeyQueryPart = tableDefinition.getPrimaryKeyQuery();

        // set key values
        item.getPrimaryKey()
            .entrySet()
            .forEach(entry -> parameterSource.addValue("key_" + entry.getKey(), entry.getValue()));

        var query = String.format("UPDATE %s SET %s WHERE %s", tableDefinition.getTableName(),
                                  setPart,
                                  primaryKeyQueryPart
        );
        LOGGER.debug("Created update Query: [{}]", query);

        jdbcTemplate.update(query, parameterSource);
    }

    /**
     * Creates a new row in the database table with the values provided in the given
     * {@link ColumnRow}. The values are inserted into the columns specified in the
     * {@link TableDefinition} of the item.
     *
     * @param item the {@link ColumnRow} containing the values for the new row
     * @see ColumnRow
     */
    public void createColumn(ColumnRow item) {
        var tableDefinition = item.getTableDefinition();
        var parameterSource = new MapSqlParameterSource();

        var columnList = tableDefinition.getColumnDefinitionMap()
                                        .keySet()
                                        .stream()
                                        .collect(Collectors.joining(", "));

        var paramList = tableDefinition.getColumnDefinitionMap()
                                       .keySet()
                                       .stream()
                                       .map(s -> ":" + s)
                                       .collect(Collectors.joining(", "));

        tableDefinition.getColumnDefinitionMap()
                       .keySet()
                       .forEach(column -> parameterSource.addValue(column, item.getCell(column)));

        var insertQuery =
            String.format("INSERT INTO %s (%s) VALUES (%s)", tableDefinition.getTableName(),
                          columnList, paramList
            );

        jdbcTemplate.update(insertQuery, parameterSource);
    }

    private static class DbTableDataProvider extends AbstractBackEndDataProvider<ColumnRow, Object>
        implements DataProvider<ColumnRow, Object> {
        private final transient EntityManager entityManager;
        private final TableDefinition tableDefinition;

        public DbTableDataProvider(EntityManager entityManager, TableDefinition tableDefinition) {

            this.entityManager = entityManager;
            this.tableDefinition = tableDefinition;
        }

        @Override
        protected Stream<ColumnRow> fetchFromBackEnd(Query<ColumnRow, Object> query) {
            int limit = query.getLimit();
            int offset = query.getOffset();

            List<Tuple> resultList =
                entityManager.createNativeQuery(
                                 "SELECT * FROM " + tableDefinition.getTableName(), Tuple.class
                             )
                             .setFirstResult(offset)
                             .setMaxResults(limit)
                             .getResultList();

            return resultList.stream()
                             .map(tuple -> {
                                 final var columnRow = new ColumnRow(tableDefinition);
                                 tableDefinition.getColumnDefinitionMap()
                                                .entrySet()
                                                .forEach(entry -> {
                                                    columnRow.setCell(
                                                        entry.getKey(), tuple.get(entry.getKey()));
                                                    if (entry.getValue().isPrimaryKey()) {
                                                        columnRow.getPrimaryKey()
                                                                 .put(
                                                                     entry.getKey(),
                                                                     tuple.get(entry.getKey())
                                                                 );
                                                    }
                                                });
                                 return columnRow;
                             });
        }

        @Override
        protected int sizeInBackEnd(Query<ColumnRow, Object> query) {

            var singleResult = (Tuple) entityManager
                .createNativeQuery(
                    "SELECT count(*) FROM " + tableDefinition.getTableName(), Tuple.class
                )
                .getSingleResult();
            BigInteger bigInteger = singleResult.get(0, BigInteger.class);
            return bigInteger.intValue();
        }
    }

    /**
     * The {@code ColumnRow} class represents a row in a database table. It contains methods for
     * accessing and modifying the values in the row.
     */
    @Data
    public static class ColumnRow {
        private final TableDefinition tableDefinition;
        private final Map<String, Object> columns = new HashMap<>();
        private final Map<String, Object> primaryKey = new HashMap<>();

        public ColumnRow(TableDefinition tableDefinition) {
            this.tableDefinition = tableDefinition;
        }

        public Object getCell(String columnName) {
            return this.columns.getOrDefault(columnName, null);
        }

        public void setCell(String columnName, Object object) {
            this.columns.put(columnName, object);
        }
    }

    /**
     * Represents a definition of a database table.
     *
     * <p>A TableDefinition object holds information about a specific database table. It includes
     * the table name, a map of column definitions, and a list of primary key column names.
     */
    @Getter
    public static class TableDefinition {
        private final String tableName;
        private final Map<String, ColumnDefinition> columnDefinitionMap = new HashMap<>();
        private final List<String> primaryKey = new ArrayList<>();

        public TableDefinition(String tableName) {
            this.tableName = tableName;
        }

        public void addPrimaryKey(String columnName) {
            this.primaryKey.add(columnName);
            columnDefinitionMap.get(columnName).setPrimaryKey(true);
        }

        /**
         * Returns a query string that can be used to retrieve a row from the database table based
         * on the primary key values. The query string is generated by iterating through the list of
         * primary key column names and formatting them as "&lt;column name&gt; = :key_&lt;column
         * name&gt;". The column names and corresponding values are separated by "AND".
         *
         * @return a query string that can be used to retrieve a row based on the primary key values
         */
        public String getPrimaryKeyQuery() {
            return primaryKey.stream()
                             .map(s -> String.format(" %s = :key_%s", s, s))
                             .collect(Collectors.joining(" AND "));
        }

        public int getColumnCount() {
            return this.columnDefinitionMap.size();
        }
    }

    /**
     * Represents a column definition for a database table. A ColumnDefinition object holds
     * information about a specific column in a table, including the column name, data type, ordinal
     * position, and whether it is a primary key.
     */
    @Data
    public static class ColumnDefinition {
        private String columnName;
        private String dataType;
        private Integer ordinalPosition;
        private boolean primaryKey;
    }
}
