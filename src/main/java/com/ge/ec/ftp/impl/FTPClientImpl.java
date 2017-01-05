package com.ge.ec.ftp.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.lang.SystemUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.stereotype.Service;

import com.ge.ec.ftp.ECFTPClient;
import com.ge.ec.ftp.FTPMeta;
@Service
public class FTPClientImpl implements ECFTPClient,FTPMeta{

	private final Logger logger = Logger.getLogger(this.getClass().getName());
	FTPClient client;
	boolean isClientConnected = false;

	@Override
	public boolean connectToServer() throws IOException {
		if(client==null || isClientConnected==false){
			client = new FTPClient();
			try 
			{
				client.connect(CLIENT_ADDRESS,CLIENT_PORT);
				isClientConnected = client.login(BASE_USERNAME, BASE_PASSWORD);
				/*Change FTP Directory To Current Directory*/
				client.changeWorkingDirectory("./");
				if (isClientConnected == true) 
				{
					logger.info(this.getClass().getName()+": readFtp(): Successfully connected to FTP Server.");
				}
				else 
				{
					logger.severe(this.getClass().getName()+": readFtp(): Connection to FTP Server Failed.");
				}

			}
			catch (FTPConnectionClosedException e) 
			{
				logger.severe(this.getClass().getName()+": readFtp(): FTP Client Closed Connection Without Request.");
				logger.severe("ERROR TRACE: "+org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
			}
		}
		return isClientConnected;
	}

	@Override
	public boolean disconnectClient() {
		try 
		{
			logger.info(this.getClass().getName()+": disconnectClient(): Requesting FTP Client Shutdown.");
			client.disconnect();
			isClientConnected = false;
		}
		catch (Exception e) 
		{
			logger.severe(this.getClass().getName()+": disconnectClient(): Ungraceful FTP Client Shutdown.");
			logger.severe("ERROR TRACE: "+org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));			
		}
		return isClientConnected;
	}

	@Override
	public boolean clientStatus() {
		return isClientConnected;
	}

	@Override
	public List<String> getAllFileNamesFromServer() {
		/*Check if client is connected*/
		/*if(client.isConnected()){
			logger.info(this.getClass().getName()+": getAllFileNamesFromServer(): Client Connected.");
		}else{
			If not, try to reconnect
			logger.info(this.getClass().getName()+": getAllFileNamesFromServer(): Client Not Connected, Trying to connect.");
			try {
				Connection Attempt
				connectToServer();
				isClientConnected = true;
			} catch (Exception e) {
				Connection Failure
				isClientConnected = false;
				logger.severe(this.getClass().getName()+": getAllFileNamesFromServer(): Failed to connect to server.");
				logger.severe("ERROR TRACE: "+org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));	
			}
		}*/
		/*Create Folder */
		String folderName = ""+System.currentTimeMillis();
		File path = new File(folderName);
		/*If it doesn't exist then create new folder
		 */		if (!path.exists()) {
			 if (path.mkdir()) {
				 logger.info("Directory is created!");
			 } else {
				 /*Delete old folder and create new one*/
				 path.delete();
				 path.mkdir();
				 logger.info("Failed to create directory!");
			 }
		 }
		 List<String> fileNames = new LinkedList<String>();
		 int file_copied_count = 0;
		 try{
			 connectToServer();
			 logger.info("Client Status : "+client.getStatus());
			 /*Fetching All Files on FTP Server*/
			 FTPFile[] files = client.listFiles();
			 logger.info(this.getClass().getName()+": getAllFileNamesFromServer(): Listing all files on FTP Server.");
			 for (FTPFile file : files) 
			 {
				 if (SystemUtils.IS_OS_WINDOWS){
					 client.configure(new FTPClientConfig(FTPClientConfig.SYST_NT));
				 }else if(SystemUtils.IS_OS_MAC){
					 client.configure(new FTPClientConfig(FTPClientConfig.SYST_MACOS_PETER));
				 }else if (SystemUtils.IS_OS_LINUX){
					 client.configure(new FTPClientConfig(FTPClientConfig.SYST_UNIX));
				 }
				 fileNames.add(String.format("%s(File-Size: %s)", file.getName(), file.getSize()));
				 if(file.getSize()>0 && file!=null){
					 if(file.isDirectory()){
						 logger.info(this.getClass().getName()+": getAllFileNamesFromServer(): Found a folder, not copying. : "+file.getName());
					 }else{
						 logger.info(this.getClass().getName()+": getAllFileNamesFromServer(): Copying file: "+file.getName());
						 InputStream fis = client.retrieveFileStream(file.getName());
						 String fileName = String.format("./%s/", folderName)+file.getName();
						 logger.info(this.getClass().getName()+": Processing File: "+fileName);
						 OutputStream fos = new BufferedOutputStream(new FileOutputStream(fileName));
						 if(fis!=null && fos!=null){
							 byte[] bytesArray = new byte[1024];
							 int bytesRead = -1;
							 while ((bytesRead = fis.read(bytesArray)) != -1) {
								 fos.write(bytesArray, 0, bytesRead);
							 }
							 boolean success = client.completePendingCommand();
							 if (success) {
								 file_copied_count++;
								 logger.info(this.getClass().getName()+": "+file.getName()+" copied successfully.");
							}
							 fos.close();
							 fis.close();
						 }
					 }
				 }
			 }
			 Thread.sleep(1500L);
			 client.logout();
			 client.disconnect();
			 isClientConnected=false;
			 fileNames.add(file_copied_count+" files copied.");
		 }catch(Exception e){
			 logger.severe(this.getClass().getName()+": getAllFileNamesFromServer(): Failed to get list of files.");
			 logger.severe("ERROR TRACE: "+org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));	
		 }
		 return fileNames;
	}

}
