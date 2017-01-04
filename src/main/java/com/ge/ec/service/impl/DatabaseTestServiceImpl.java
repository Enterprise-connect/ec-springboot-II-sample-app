/**
 * @author Avneesh Srivastava
 * @email avneesh.srivastava@ge.com
 *
 */
package com.ge.ec.service.impl;

import java.util.logging.Logger;
import org.json.JSONObject;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Service;
import com.ge.ec.service.DatabaseTestService;
@Service
public class DatabaseTestServiceImpl implements DatabaseTestService {

	private DriverManagerDataSource oracleDS;
	private DriverManagerDataSource postgreDS;
	private DriverManagerDataSource sqlServerDS;
	private JdbcTemplate jdbcTemplate;
	private final Logger log = Logger.getLogger(this.getClass().getName());
	@Override
	public DriverManagerDataSource checkDataSource(DriverManagerDataSource ds, JSONObject dbDetails){
		if(ds==null){
			log.info("Datasource is null, setting datasource properties");
			ds = new DriverManagerDataSource();
			ds.setDriverClassName(dbDetails.getString("driver"));
			ds.setUrl(dbDetails.getString("url"));
			ds.setUsername(dbDetails.getString("username"));
			ds.setPassword(dbDetails.getString("password"));
		}else{
			log.info("Datasource is not null, returning datasource.");
		}
		return ds;
	}
	@Override
	public JdbcTemplate getPostgreConnection(JSONObject dbDetails) {
		postgreDS = checkDataSource(postgreDS, dbDetails);
		jdbcTemplate = new JdbcTemplate(postgreDS);
		return jdbcTemplate;
	}

	@Override
	public JdbcTemplate getOracleConnection(JSONObject dbDetails) {
		oracleDS = checkDataSource(oracleDS, dbDetails);
		jdbcTemplate = new JdbcTemplate(oracleDS);
		return jdbcTemplate;
	}

	@Override
	public JdbcTemplate getSQLServerConnection(JSONObject dbDetails) {
		sqlServerDS = checkDataSource(sqlServerDS, dbDetails);
		jdbcTemplate = new JdbcTemplate(sqlServerDS);
		return jdbcTemplate;
	}

}
