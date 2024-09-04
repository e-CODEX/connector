/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.persistence.testutil;

/**
 * The MysqlTestDatabaseFactory class is a concrete implementation of the TestDatabaseFactory
 * interface. It provides functionality for creating a new MySQL database for testing purposes.
 */
public class MysqlTestDatabaseFactory extends AbstractTestDatabaseFactory
    implements TestDatabaseFactory {
    @Override
    public String getDatabaseType() {
        return "mysql";
    }

    @Override
    public String getName() {
        return "Native Mysql";
    }

    @Override
    protected String getDriverClassName() {
        return "com.mysql.jdbc.Driver";
    }
}
