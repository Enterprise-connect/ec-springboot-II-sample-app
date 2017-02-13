/**
 * 
 */
package com.ge.ec.mapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.json.JSONObject;
import org.springframework.jdbc.core.RowMapper;

public class CustomRowMapper implements RowMapper<JSONObject>{

	@Override
	public JSONObject mapRow(ResultSet resultSet, int arg1)
			throws SQLException {
		ResultSetMetaData meta = resultSet.getMetaData();
		JSONObject results = new JSONObject();
		for(int index=1; index<=meta.getColumnCount(); index++){
			results.put(meta.getColumnName(index), resultSet.getObject(index));
		}
		return results;
	}


}
