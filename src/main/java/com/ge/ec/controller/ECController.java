/**
 * @author Avneesh Srivastava
 * @email avneesh.srivastava@ge.com
 *
 */
package com.ge.ec.controller;

import java.io.IOException;
import java.sql.DriverManager;
import java.util.List;

import javax.mail.MessagingException;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ge.ec.ftp.ECFTPClient;
import com.ge.ec.ftp.ECFTPServer;
import com.ge.ec.mapper.CustomRowMapper;
import com.ge.ec.service.DatabaseTestService;
import com.ge.ec.service.ECService;
import com.ge.ec.service.impl.ECJMSServiceImpl;
import com.ge.ec.util.MailSender;

@RestController
class ECController{
	@Autowired
	ECService ecService;

	@Autowired
	DatabaseTestService dbService;

	@Autowired
	MailSender mailSender; 
	
	@Autowired
	ECFTPServer ftpServer;
	
	@Autowired
	ECFTPClient ftpClient;
	
	@Autowired
	ECJMSServiceImpl jmsService;
	
	@RequestMapping("/jmsService")
	String jmsService(){
		if(ecService.isECStarted()){
			try {
				jmsService.sendMessage();
				return "Message sent.";
			} catch (Exception e) {
				System.out.println(String.format("Could Not Send Message. Error:: %s", e.getMessage()));
				return "Failed To Send";
			}
		}else{
			return "EC Client Not Started Yet.";
		}
	}
	
	@ResponseStatus(HttpStatus.CREATED)
	@RequestMapping("/sendMail")
	String sendMail(){
		if(ecService.isECStarted()){
			try {
				mailSender.sendMail("avneesh.srivastava@capgemini.com", "avneesh.srivastava@ge.com", "Test Mail", "Test Mail");
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
			dbDetails.put("url","jdbc:postgresql://localhost:4500/database");
			dbDetails.put("username","username");
			dbDetails.put("password","password");
			try{
				String timestampFromDb = ("Timestamp: "+ dbService.getPostgreConnection(dbDetails).queryForObject("select now()", String.class));
				return String.format("Database Connection Successful. Timestamp from DB: %s", timestampFromDb);
			}catch(Exception ex){
				System.out.println("Exception In checkPostgreDB()"+ ex.getMessage());
				return String.format("Database Connection Failed, %s", ex.getMessage());
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
			dbDetails.put("url","jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)(HOST=localhost)(PORT=4500))(LOAD_BALANCE=OFF)(FAILOVER=ON)(CONNECT_DATA=(SERVER=DEDICATED)(SERVICE_NAME=stgbtm.cloud.ge.com)(FAILOVER_MODE=(TYPE=SELECT)(METHOD=BASIC)(RETRIES=0)(DELAY=1))))");
			dbDetails.put("username","sys as sysdba");
			dbDetails.put("password","Welcome1");
			try{
				System.out.println("DUAL Result: "+dbService.getOracleConnection(dbDetails).query("SELECT owner, table_name FROM dba_tables where rownum <= 10", new CustomRowMapper() ));
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
			dbDetails.put("username","SSO502647803");
			dbDetails.put("password","DbrGates$$032");
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
	
	@RequestMapping("/createftpserver")
	String ftpserver() throws IOException {
		return (ftpServer.createFtpServer())?"FTP Server Connected":"FTP Server Not Connected.";
	}
	@RequestMapping("/createftpclient")
	String readFtpserver() throws IOException {
		ftpClient.connectToServer();
		return (ftpClient.clientStatus())?"FTP Client Connected":"FTP Client Not Connected.";
	}
	@RequestMapping("/disconnectftpclient")
	String disconnectFTPClient() throws IOException {
		ftpClient.disconnectClient();
		return (ftpClient.clientStatus())?"FTP Client Connected":"FTP Client Not Connected.";
	}
	@RequestMapping("/readfilelist")
	List<String> getAllFileNamesFromServer() throws IOException {
		ftpClient.getDirectoryStructure();
		return null;
	}

}