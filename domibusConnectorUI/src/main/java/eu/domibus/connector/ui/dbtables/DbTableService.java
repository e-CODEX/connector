package eu.domibus.connector.ui.dbtables;

import com.vaadin.flow.data.provider.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.TupleElement;
import javax.sql.DataSource;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RolesAllowed("ADMIN")
public class DbTableService {

    private static final Logger LOGGER = LogManager.getLogger(DbTableService.class);
    private final DataSource ds;
    private final DbTableServiceConfigurationProperties config;

    private final EntityManager entityManager;
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public DbTableService(EntityManager entityManager,
                          DataSource ds, DbTableServiceConfigurationProperties config) {
        this.config = config;
        this.entityManager = entityManager;
        this.ds = ds;
        this.jdbcTemplate = new NamedParameterJdbcTemplate(ds);
    }
    public List<String> getTables() {
        return config.getTables();
    }

    public List<ColumnDefinition> getColumns(String table) {
        return new ArrayList<>(getTableDefinition(table)
                .columnDefinitionMap
                .values());
    }

    public TableDefinition getTableDefinition(String tableName) {
        TableDefinition tableDefinition = new TableDefinition(tableName);
        //read columns
        try (Connection conn = ds.getConnection();
             ResultSet rs = conn
                .getMetaData()
                .getColumns(null, null, tableName, null);
        ){

            while (rs.next()) {
                ColumnDefinition cd = new ColumnDefinition();
                String columnName = rs.getString(4); //get column name
                cd.setColumnName(columnName);
                cd.setDataType(rs.getString(5)); //java.sql.Type
                cd.setOrdinalPosition(rs.getInt(17)); //ORDINAL_POSITION
                tableDefinition.columnDefinitionMap.put(columnName, cd);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //read primary keys
        try (Connection conn = ds.getConnection();
             ResultSet rs = conn
                     .getMetaData()
                     .getPrimaryKeys(null, null, tableName);
        ){

            while (rs.next()) {
                String columnName = rs.getString(4);
//                int keySequence = rs.getInt(5);
//                String pkName = rs.getString(6);
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


    public void deleteColumn(ColumnRow columnRow) {
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();

        TableDefinition tableDefinition = columnRow.getTableDefinition();



        //set key values
        columnRow.getPrimaryKey()
                .entrySet()
                .forEach(entry -> {
                    parameterSource.addValue("key_" + entry.getKey(), entry.getValue());
                });
        String sql = String.format("DELETE %s WHERE %s", tableDefinition.getTableName(), tableDefinition.getPrimaryKeyQuery());

        jdbcTemplate.update(sql, parameterSource);
    }

    public void updateColumn(ColumnRow item) {
        TableDefinition tableDefinition = item.getTableDefinition();
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();

        //create set part
        String setPart = tableDefinition.getColumnDefinitionMap()
                .keySet()
                .stream()
                .map(c -> String.format("%s=:%s", c, c))
                .collect(Collectors.joining(", "));

        //set set values
        tableDefinition.getColumnDefinitionMap()
                .keySet()
                .forEach(column -> {
                    parameterSource.addValue(column, item.getCell(column));
                });


        //define primary key query part
        String primaryKeyQueryPart = tableDefinition.getPrimaryKeyQuery();

        //set key values
        item.getPrimaryKey()
                .entrySet()
                .forEach(entry -> {
                    parameterSource.addValue("key_" + entry.getKey(), entry.getValue());
                });


        String query = String.format("UPDATE %s SET %s WHERE %s", tableDefinition.getTableName(),
                setPart,
                primaryKeyQueryPart);
        LOGGER.debug("Created update Query: [{}]", query);


        jdbcTemplate.update(query, parameterSource);


    }

    public void createColumn(ColumnRow item) {
        TableDefinition tableDefinition = item.getTableDefinition();
        MapSqlParameterSource parameterSource = new MapSqlParameterSource();

        String columnList = tableDefinition.getColumnDefinitionMap()
                .keySet()
                .stream()
                .collect(Collectors.joining(", "));

        String paramList = tableDefinition.getColumnDefinitionMap()
                .keySet()
                .stream()
                .map(s -> ":"+s)
                .collect(Collectors.joining(", "));

        tableDefinition.getColumnDefinitionMap()
                .keySet()
                .forEach(column -> {
                    parameterSource.addValue(column, item.getCell(column));
                });

        String insertQuery = String.format("INSERT INTO %s (%s) VALUES (%s)", tableDefinition.getTableName(), columnList, paramList);

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

            List<Tuple> resultList = entityManager.createNativeQuery("SELECT * FROM " + tableDefinition.getTableName(), Tuple.class)
                    .setFirstResult(offset)
                    .setMaxResults(limit)
                    .getResultList();

            return resultList.stream()
                    .map( tuple -> {
                        final ColumnRow columnRow = new ColumnRow(tableDefinition);
                        tableDefinition.getColumnDefinitionMap().entrySet()
                                        .forEach(entry -> {
                                            columnRow.setCell(entry.getKey(), tuple.get(entry.getKey()));
                                            if (entry.getValue().isPrimaryKey()) {
                                                columnRow.getPrimaryKey().put(entry.getKey(), tuple.get(entry.getKey()));
                                            }
                                        });
                        return columnRow;
                    });
        }

        @Override
        protected int sizeInBackEnd(Query<ColumnRow, Object> query) {

            Tuple singleResult = (Tuple) entityManager.createNativeQuery("SELECT count(*) FROM " + tableDefinition.getTableName(), Tuple.class).getSingleResult();
            BigInteger bigInteger = singleResult.get(0, BigInteger.class);
            return bigInteger.intValue();

        }
    }


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

        public TableDefinition getTableDefinition() {
            return tableDefinition;
        }

        private Map<String, Object> getPrimaryKey() {
            return primaryKey;
        }
    }

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

        public String getPrimaryKeyQuery() {
            return primaryKey.stream()
                    .map(s ->  String.format(" %s = :key_%s", s, s))
                    .collect(Collectors.joining(" AND "));
        }

        public String getTableName() {
            return tableName;
        }

        public Map<String, ColumnDefinition> getColumnDefinitionMap() {
            return columnDefinitionMap;
        }

        public List<String> getPrimaryKey() {
            return primaryKey;
        }

        public int getColumnCount() {
            return this.columnDefinitionMap.size();
        }
    }

    public static class ColumnDefinition {
        private String columnName;
        private String dataType;
        private Integer ordinalPosition;
        private boolean primaryKey;

        public String getColumnName() {
            return columnName;
        }

        public void setColumnName(String columnName) {
            this.columnName = columnName;
        }

        public String getDataType() {
            return dataType;
        }

        public void setDataType(String dataType) {
            this.dataType = dataType;
        }

        public Integer getOrdinalPosition() {
            return ordinalPosition;
        }

        public void setOrdinalPosition(Integer ordinalPosition) {
            this.ordinalPosition = ordinalPosition;
        }

        public void setPrimaryKey(boolean b) {
            this.primaryKey = b;
        }

        public boolean isPrimaryKey() {
            return primaryKey;
        }
    }


}
