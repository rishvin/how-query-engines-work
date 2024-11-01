// Copyright 2020 Andy Grove
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package io.andygrove.kquery.jdbc;

import static org.codehaus.groovy.runtime.DefaultGroovyMethods.println;
import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

/**
 * JDBC Driver unit tests.
 */
public class DriverTest {

  final io.andygrove.kquery.jdbc.Driver driver = new io.andygrove.kquery.jdbc.Driver();

  @Test
  public void acceptsValidUrl() throws SQLException {
    assertTrue(driver.acceptsURL("jdbc:arrow://localhost:50051"));
  }

  @Test
  public void rejectsInvalidUrl() throws SQLException {
    assertFalse(driver.acceptsURL("jdbc:mysql://localhost:50051"));
  }

  @Test
  public void rejectsNullUrl() throws SQLException {
    assertFalse(driver.acceptsURL(null));
  }

  /**
   * Note that this is a manual integration test that requires the Rust flight-server example to be running.
   */
  @Test
  public void   executeQuery() throws SQLException {
    DriverManager.registerDriver(driver);
    try (Connection conn = DriverManager.getConnection("jdbc:arrow://0.0.0.0:50051", new Properties())) {
      try (Statement stmt = conn.createStatement()) {
        try (ResultSet rs = stmt.executeQuery("SELECT VendorID, total_amount FROM csv.`./yellow_tripdata_2019-01-tiny.csv`")) {

          ResultSetMetaData md = rs.getMetaData();
            assertEquals(2, md.getColumnCount());
            assertEquals("VendorID", md.getColumnName(1));
            assertEquals(Types.OTHER, md.getColumnType(1));
            assertEquals("total_amount", md.getColumnName(2));
            assertEquals(Types.OTHER, md.getColumnType(2));

          List<String> ids = new ArrayList<>();
          List<String> amounts = new ArrayList<>();
          while (rs.next()) {
            ids.add(rs.getString(1));
            amounts.add(rs.getString(2));
          }

          assertEquals(ImmutableList.of("1", "2", "3", "4", "5", "6", "7", "8"), ids);
          assertEquals(ImmutableList.of("9.95", "16.3", "5.8", "7.55", "55.55", "13.31", "55.55", "9.05"), amounts);
        }
      }
    }
  }
}
