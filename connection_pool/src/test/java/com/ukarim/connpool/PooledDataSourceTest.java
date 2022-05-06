package com.ukarim.connpool;

import org.junit.jupiter.api.Test;

class PooledDataSourceTest {

    private static final int TIMEOUT_SEC = 30;
    private static final int POOL_SIZE = 10;
    private static final String JDBC_URL = "jdbc:h2:mem:test_db";
    private static final String USER = "sa";
    private static final String PASS = "";

    @Test
    void checkConnectionPool() throws Exception {
        var dataSource = new PooledDataSource(JDBC_URL, USER, PASS, POOL_SIZE, TIMEOUT_SEC);
        try (var conn = dataSource.getConnection();
                var stmt = conn.prepareStatement("select current_timestamp")
        ) {
            var resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                System.out.printf("CURRENT TIMESTAMP: %s\n", resultSet.getTimestamp(1));
            }
        }
    }
}
