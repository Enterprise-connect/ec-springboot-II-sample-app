package com.ge.ec.ftp.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpReply;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.ftplet.FtpSession;
import org.apache.ftpserver.ftplet.Ftplet;
import org.apache.ftpserver.ftplet.FtpletContext;
import org.apache.ftpserver.ftplet.FtpletResult;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.PasswordEncryptor;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.WritePermission;
import org.springframework.stereotype.Service;

import com.ge.ec.ftp.ECFTPServer;
import com.ge.ec.ftp.FTPMeta;
@Service
public class FTPServerImpl implements ECFTPServer,FTPMeta{
	private final Logger logger = Logger.getLogger(this.getClass().getName());
	FtpServer server;
	boolean isServerCreated = false;
	@Override
	public boolean createFtpServer() {
		if(server==null){
			FtpServerFactory serverFactory = new FtpServerFactory();
			ListenerFactory factory = new ListenerFactory();
			factory.setServerAddress(SERVER_ADDRESS);
			/*Set The Port Of The Listener*/
			factory.setPort(SERVER_PORT);
			/*Change Default Timeout*/
			factory.setIdleTimeout(SERVER_CONNECTION_TIMEOUT);
			serverFactory.addListener("default", factory.createListener());
			logger.info(this.getClass().getName()+": getFTPServer(): Adding Users Now");
			PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
			File userRecords = new File("users.properties");
			if(!userRecords.exists()){
				try {
					userRecords.createNewFile();
				} catch (IOException e) {
					logger.severe(this.getClass().getName()+": getFTPServer(): Error In Creating New Users File");
					logger.severe("ERROR TRACE: "+org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
				}
			}
			userManagerFactory.setFile(userRecords);
			userManagerFactory.setPasswordEncryptor(new PasswordEncryptor()
			{
				/*Converting Passwords To Readable Format*/
				@Override
				public String encrypt(String password) {
					return password;
				}

				@Override
				public boolean matches(String passwordToCheck, String storedPassword) {
					return passwordToCheck.equals(storedPassword);
				}
			});
			/*Adding A Base User To Access FTP*/
			BaseUser baseUser = new BaseUser();
			baseUser.setName(BASE_USERNAME);
			baseUser.setPassword(BASE_PASSWORD);
			baseUser.setHomeDirectory("./");
			List<Authority> authorities = new ArrayList<Authority>();
			authorities.add(new WritePermission());
			baseUser.setAuthorities(authorities);
			UserManager userManager = userManagerFactory.createUserManager();
			try
			{
				userManager.save(baseUser);
			}
			catch (Exception e)
			{
				logger.severe(this.getClass().getName()+": getFTPServer(): Error In Saving Base User To File");
				logger.severe("ERROR TRACE: "+org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
			}

			serverFactory.setUserManager(userManager);
			Map<String, Ftplet> apacheFtpLets = new HashMap<String, Ftplet>();
			apacheFtpLets.put("miaFtplet", new Ftplet()
			{

				@Override
				public void init(FtpletContext ftpletContext) throws FtpException {
					logger.info(this.getClass().getName()+": getFTPServer(): Initializing FTP Server");
				}

				@Override
				public void destroy() {
					logger.info(this.getClass().getName()+": getFTPServer(): Destroying FTP Server");
				}

				@Override
				public FtpletResult beforeCommand(FtpSession session, FtpRequest request) throws FtpException, IOException
				{
					logger.info(this.getClass().getName()+": getFTPServer(): Command Received, going to execute. "+request.getCommand());
					/*Let It Execute Default Action*/
					return FtpletResult.DEFAULT;
				}

				@Override
				public FtpletResult afterCommand(FtpSession session, FtpRequest request, FtpReply reply) throws FtpException, IOException
				{
					logger.info(this.getClass().getName()+": getFTPServer(): Command Execution Completed. "+request.getCommand());
					return FtpletResult.DEFAULT;
				}

				@Override
				public FtpletResult onConnect(FtpSession session) throws FtpException, IOException
				{
					logger.info(this.getClass().getName()+": getFTPServer(): FTP Connection Request Made, Connected. Session ID: "+session.getSessionId());
					isServerCreated = true;
					return FtpletResult.DEFAULT;
				}

				@Override
				public FtpletResult onDisconnect(FtpSession session) throws FtpException, IOException
				{
					logger.info(this.getClass().getName()+": getFTPServer(): FTP Client Disconnected. Session ID: "+session.getSessionId());
					isServerCreated = false;
					return FtpletResult.DEFAULT;
				}
			});
			serverFactory.setFtplets(apacheFtpLets);
			try{
				logger.info(this.getClass().getName()+": getFTPServer(): Starting FTP Server. PORT:" + factory.getPort());
				server = serverFactory.createServer();
				server.start();
				isServerCreated = true;
			}
			catch(Exception e){
				isServerCreated = false;
				logger.severe(this.getClass().getName()+": getFTPServer(): Failed to create FTP Server.");
				logger.severe("ERROR TRACE: "+org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
			}
		}
		return isServerCreated;
	}

	@Override
	public	boolean isServerCreated(){
		return isServerCreated;
	}

}
