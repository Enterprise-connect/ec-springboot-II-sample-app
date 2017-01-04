/**
 * @author Avneesh Srivastava
 * @email avneesh.srivastava@ge.com
 *
 */
package com.ge.ec.controller;

import java.io.IOException;
import java.sql.DriverManager;
import javax.mail.MessagingException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.ge.ec.service.DatabaseTestService;
import com.ge.ec.service.ECService;
import com.ge.ec.util.MailSender;

@RestController
class ECController{
	@Autowired
	ECService ecService;

	@Autowired
	DatabaseTestService dbService;

	@Autowired
	MailSender mailSender; 

	@ResponseStatus(HttpStatus.CREATED)
	@RequestMapping("/sendMail")
	String sendMail(){
		if(ecService.isECStarted()){
			try {
				mailSender.sendMail("to@domain.com", "from@domain.com", "Test Mail", "Test Mail");
				return "Mail Sent";
			} catch (MessagingException e) {
				System.out.println(String.format("Could Not Send Mail. Error:: %s", e.getMessage()));
				return "Failed To Send";
			}
		}else{
			return "EC Client Not Started Yet.";
		}
	}
	//Postgre Database
	@RequestMapping("/postgredatabase")
	String checkPostgreDB(){
		if(ecService.isECStarted()){
			JSONObject dbDetails = new JSONObject();
			dbDetails.put("driver","org.postgresql.Driver");
			dbDetails.put("url","jdbc:postgresql://localhost:7990/Brl");
			dbDetails.put("username","postgres");
			dbDetails.put("password","@12Igate");
			try{
				System.out.println("Timestamp: "+ dbService.getPostgreConnection(dbDetails).queryForObject("select now()", String.class));
				return "Database Connection Successful";
			}catch(Exception ex){
				System.out.println("Exception In checkPostgreDB()"+ ex.getMessage());
				return "Database Connection Failed";
			}
		}else{
			return "EC Client Not Started";
		}
	}

	//Oracle Database
	@RequestMapping("/oracletestdatabase")
	String checkOracleDB(){
		if(ecService.isECStarted()){
			// alter system set PROCESSES=300 
			String s="";
			JSONObject dbDetails = new JSONObject();
			dbDetails.put("driver","oracle.jdbc.driver.OracleDriver");
			dbDetails.put("url","jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)(HOST=localhost)(PORT=7990))(LOAD_BALANCE=OFF)(FAILOVER=ON)(CONNECT_DATA=(SERVER=DEDICATED)(SERVICE_NAME=pnnpsp04)(FAILOVER_MODE=(TYPE=SELECT)(METHOD=BASIC)(RETRIES=0)(DELAY=1))))");
			dbDetails.put("username","username");
			dbDetails.put("password","password");
			try{
				System.out.println("DUAL Result: "+dbService.getOracleConnection(dbDetails).queryForObject("select (2+5) from DUAL", Long.class));
				s="Database Connection Successful";

			}catch(Exception ex){
				s="Database Connection Failed "+ex.getMessage();
				System.out.println("Exception In checkOracleDB()"+ ex.getMessage());
			}
			return s;
		}else{
			return "EC Client Not Started";
		}
	}

	//MS SQL Server
	@RequestMapping("/sqlserverdb")
	String checkSqlServerDB(){
		if(ecService.isECStarted()){
			String s="";
			JSONObject dbDetails = new JSONObject();
			dbDetails.put("driver","com.microsoft.sqlserver.jdbc.SQLServerDriver");
			dbDetails.put("url","jdbc:sqlserver://localhost:7990;databaseName=GTAE");
			dbDetails.put("username","username");
			dbDetails.put("password","password");
			try{
				DriverManager.registerDriver(new com.microsoft.sqlserver.jdbc.SQLServerDriver());
				System.out.println("DUAL Result: "+dbService.getSQLServerConnection(dbDetails).queryForObject("select (2+5)", Long.class));
				s = "Database Connection Successful";
			}catch(Exception ex){
				s = "Database Connection Falied: "+ex.getMessage();
				System.out.println("Exception In checkSqlServerDB()"+ ex.getMessage());
			}
			return s;
		}else{
			return "EC Client Not Started";
		}
	}

	@RequestMapping("/kill")
	String kill() throws IOException {
		String response = "";
		switch(ecService.killEC()){
		case 0:{
			response = "Successful Termination";
		}
		break;
		case 1:{
			response = "Failed To Terminate";
		}
		break;
		case -1:{
			response = "Could Not Terminate, Got Exception.";
		}
		break;
		case -2:{
			response = "Client has not been started.";
		}
		break;
		default:{
			response = "Default Case: Unknown Response";
		}
		break;
		}
		return response;
	}

	@RequestMapping("/status")
	String status() throws IOException {
		return (ecService.isECStarted())?"EC Client is ok.":"EC Client was terminated.";
	}

}