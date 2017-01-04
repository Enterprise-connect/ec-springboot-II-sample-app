/**
 * @author Avneesh Srivastava
 * @email avneesh.srivastava@ge.com
 *
 */
package com.ge.ec.service;

import org.json.JSONObject;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public interface DatabaseTestService {
	JdbcTemplate getPostgreConnection(JSONObject dbDetails);
	JdbcTemplate getOracleConnection(JSONObject dbDetails);
	JdbcTemplate getSQLServerConnection(JSONObject dbDetails);
	DriverManagerDataSource checkDataSource(DriverManagerDataSource ds, JSONObject dbDetails);
}
