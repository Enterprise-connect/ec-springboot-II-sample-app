package com.ge.ec.controller;

import java.io.IOException;
import java.sql.Connection;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ge.ec.ECClientImpl;
import com.ge.ec.libs.ECClient;

@RestController
class ECController implements DisposableBean {
	@Autowired
	private ECClient ec;

	@RequestMapping("/launch")
	String launch() throws IOException {
		String ver=ec.version();
		boolean _ref1=ec.launch();
		return _ref1?"EC Client ("+ver+") is at your service.":"EC Client failed to launch.";
	}

	@RequestMapping("/testdatabase")
	String checKDBConn(){
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		String driverClassName = "org.postgresql.Driver";
		String url = "jdbc:postgresql://localhost:7990/Brl";
		String dbUsername = "postgres";
		String dbPassword = "@12Igate";
		dataSource.setDriverClassName(driverClassName);
		dataSource.setUrl(url);
		dataSource.setUsername(dbUsername);
		dataSource.setPassword(dbPassword);
		try{
			JdbcTemplate jdbcTemplate = new JdbcTemplate();
			jdbcTemplate.setDataSource(dataSource);
			System.out.println("Total Row Count In BL_ACTIVITY_INFO: "+jdbcTemplate.queryForObject("select now()", String.class));
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return "Checking";
	}

	@RequestMapping("/oracletestdatabase")
	String checkOracleDB(){
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		// alter system set PROCESSES=300 
		String driverClassName = "oracle.jdbc.driver.OracleDriver";
		String url = "jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)(HOST=localhost)(PORT=7990))(LOAD_BALANCE=yes)(FAILOVER=yes)(CONNECT_DATA=(SERVER=DEDICATED)(SERVICE_NAME=pnnpsp04)(FAILOVER_MODE=(TYPE=SELECT)(METHOD=BASIC)(RETRIES=0)(DELAY=1))))";		//
		String dbUsername = "username";
		String dbPassword = "password";
		dataSource.setDriverClassName(driverClassName);
		dataSource.setUrl(url);
		dataSource.setUsername(dbUsername);
		dataSource.setPassword(dbPassword);
		String s="true";
		try{
			//OracleDataSource oracleDataSource = new OracleDataSource();
			//oracleDataSource.setUser(dbUsername);
			//oracleDataSource.setPassword(dbPassword);
			//oracleDataSource.setURL(url);
			//oracleDataSource.setImplicitCachingEnabled(true);
			//oracleDataSource.setFastConnectionFailoverEnabled(true);
			JdbcTemplate jdbcTemplate = new JdbcTemplate();
			jdbcTemplate.setDataSource(dataSource);
			System.out.println("DUAL Result: "+jdbcTemplate.queryForObject("select (2+5) from DUAL", Long.class));
			
		}catch(Exception ex){
			s="false";
			System.out.println(ex.getMessage());
		}
		return s;
	}

	@RequestMapping("/kill")
	String kill() throws IOException {
		String ver=ec.version();
		boolean terminationFlag = ec.terminate();
		if(terminationFlag){
			return "EC Client ("+ver+") has been terminated.";
		}else{
			return "EC Client ("+ver+") could not be terminated.";
		}
	}

	@RequestMapping("/status")
	String status() throws IOException {
		ECClientImpl ec=new ECClientImpl();
		return (ec.isAlive())?"EC Client is ok.":"EC Client was terminated.";
	}

	@Override
	public void destroy() throws Exception {
		if(ec.isAlive()){
			System.out.println("Terminating Client");
			System.out.println(ec.terminate());
		}
	}

}