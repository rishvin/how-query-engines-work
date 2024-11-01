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

import java.sql.Connection;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

import org.slf4j.LoggerFactory;

/**
 * Driver.
 */
public class Driver implements java.sql.Driver {

  private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Driver.class);

  /** JDBC connection string prefix. */
  private static final String PREFIX = "jdbc:arrow://";

  @Override
  public Connection connect(String url, Properties properties) throws SQLException {
    logger.info("connect() url={}", url);
    //TODO this needs much more work to parse full URLs but this is enough to get end to end tests running
    String c = url.substring(PREFIX.length());
    int i = c.indexOf(':');
    if (i == -1) {
      return new io.andygrove.kquery.jdbc.FlightConnection(c, 50051);
    } else {
      return new io.andygrove.kquery.jdbc.FlightConnection(c.substring(0,i), Integer.parseInt(c.substring(i + 1)));
    }
  }

  @Override
  public boolean acceptsURL(String url) throws SQLException {
    return url != null && url.startsWith(PREFIX);
  }

  @Override
  public DriverPropertyInfo[] getPropertyInfo(String s, Properties properties) throws SQLException {
    return new DriverPropertyInfo[0];
  }

  @Override
  public int getMajorVersion() {
    return 0;
  }

  @Override
  public int getMinorVersion() {
    return 16;
  }

  @Override
  public boolean jdbcCompliant() {
    return false;
  }

  @Override
  public Logger getParentLogger() throws SQLFeatureNotSupportedException {
    throw new SQLFeatureNotSupportedException();
  }
}
