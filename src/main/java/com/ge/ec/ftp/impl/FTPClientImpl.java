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
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.stereotype.Service;

import com.ge.ec.ftp.ECFTPClient;
import com.ge.ec.ftp.FTPMeta;
@Service
public class FTPClientImpl implements ECFTPClient,FTPMeta{

	private final Logger logger = Logger.getLogger(this.getClass().getName());
	FTPClient client;
	boolean isClientConnected = false;
	String targetFolderName;
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

	public void listDirectory(FTPClient ftpClient, String parentDir, String currentDir, int level, String targetDir) throws IOException {
		System.out.println("TargetDirectory----->"+targetDir);
		if(!currentDir.equalsIgnoreCase(targetFolderName)){
			String dirToList = parentDir;
			if (!currentDir.equals("")) {
				dirToList += "/" + currentDir;
				targetDir += "/" + currentDir;
				File path = new File(targetDir);
				/*If it doesn't exist then create new folder
				 */		
				if (!path.exists()) {
					if (path.mkdir()) {
						logger.info(targetDir+ " :Directory is created!");
					}
				}
			}
			System.out.println("----->"+dirToList);
			FTPFile[] subFiles = ftpClient.listFiles(dirToList);
			if (subFiles != null && subFiles.length > 0) {
				for (FTPFile aFile : subFiles) {
					String currentFileName = aFile.getName();
					if (currentFileName.equals(".")
							|| currentFileName.equals("..")) {
						// skip parent directory and directory itself
						continue;
					}
					if (aFile.isDirectory()) {
						logger.info(this.getClass().getName()+": listDirectory(): Obtained Folder: "+aFile.getName());
						listDirectory(ftpClient, dirToList, currentFileName, level + 1, targetDir);
					} else {
						if(aFile!=null && aFile.isFile()){
							try{
								logger.info(this.getClass().getName()+": listDirectory(): Copying file: "+aFile.getName());
								InputStream fis = client.retrieveFileStream(dirToList+"/"+aFile.getName());
								String fileName = targetDir+"/"+aFile.getName();
								logger.info(this.getClass().getName()+": Processing File: "+aFile.getName());
								OutputStream fos = new BufferedOutputStream(new FileOutputStream(fileName));
								if(fis!=null && fos!=null){
									byte[] bytesArray = new byte[1024];
									int bytesRead = -1;
									while ((bytesRead = fis.read(bytesArray)) != -1) {
										fos.write(bytesArray, 0, bytesRead);
									}
									boolean success = client.completePendingCommand();
									if (success) {
										logger.info(this.getClass().getName()+": "+aFile.getName()+" copied successfully.");
									}
									fis.close();
									fos.close();
								}
							}catch(Exception e){
								logger.severe(this.getClass().getName()+": recursiveCopy(): Failed to get list of files.");
								logger.severe("ERROR TRACE: "+org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));	
							}
						}
					}
				}
			}
		}
	}
	@Override
	public void getDirectoryStructure(){
		if(client.isConnected()){
			int replyCode = client.getReplyCode();
			if (!FTPReply.isPositiveCompletion(replyCode)) {
				logger.severe(this.getClass().getSimpleName()+": getDirectoryStructure(): Connect failed");
				return;
			}
			try {
				String targetDir = ""+System.currentTimeMillis();
				File path = new File(targetDir);
				targetFolderName = targetDir;
				/*If it doesn't exist then create new folder
				 */		
				if (!path.exists()) {
					if (path.mkdir()) {
						logger.info(targetDir+ " :Directory is created!");
					} else {
						/*Delete old folder and create new one*/
						path.delete();
						path.mkdir();
						logger.severe(targetDir+" :Failed to create directory!");
					}
				}
				listDirectory(client, "./", "", 0, targetDir);
			} catch (IOException e) {
				logger.severe(this.getClass().getName()+": getAllFileNamesFromServer(): Failed to get list of files.");
				logger.severe("ERROR TRACE: "+org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));	
			}
		}
	}
	@Override
	public int recursiveCopy(FTPClient client, FTPFile file, String folderName, int file_copied_count , String rootFolder){
		try {
			logger.info(this.getClass().getSimpleName()+": Trying to change working directory.");
			client.changeWorkingDirectory(rootFolder);
		} catch (IOException e) {
			logger.severe(this.getClass().getName()+": getAllFileNamesFromServer(): Failed to change working directory.");
			logger.severe("ERROR TRACE: "+org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
		}
		try{
			File newDirectory = new File(folderName+file.getName());
			/*If it doesn't exist then create new folder
			 */		if (!newDirectory.exists()) {
				 if (newDirectory.mkdir()) {
					 logger.info("Directory is created!");
				 }
			 }
			 for(FTPFile ftpFileName: client.listFiles(file.getName())){
				 if (SystemUtils.IS_OS_WINDOWS){
						client.configure(new FTPClientConfig(FTPClientConfig.SYST_NT));
					}else if(SystemUtils.IS_OS_MAC){
						client.configure(new FTPClientConfig(FTPClientConfig.SYST_MACOS_PETER));
					}else if (SystemUtils.IS_OS_LINUX){
						client.configure(new FTPClientConfig(FTPClientConfig.SYST_UNIX));
					}
				 if(ftpFileName.getSize()>0 && ftpFileName!=null && ftpFileName.isFile()){
					 InputStream fis = client.retrieveFileStream(ftpFileName.getName());
					 String fileName = newDirectory.getAbsolutePath()+"/"+ftpFileName.getName();
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
							 logger.info(this.getClass().getName()+": "+ftpFileName.getName()+" copied successfully.");
						 }
						 fos.close();
						 fis.close();
					 }
				 }
				 if(ftpFileName.isDirectory()){
					 String newFolderName = newDirectory.getAbsolutePath() + "/";
					 System.out.println("------->Recursive Copy"+(ftpFileName.getName()));
					 file_copied_count = recursiveCopy(client, ftpFileName, newFolderName , file_copied_count, (ftpFileName.getName()));
				 }
			 }
			 logger.info(this.getClass().getName()+": recursiveCopy(): Found a folder, not copying. : "+file.getName());
		}catch(Exception e){
			logger.severe(this.getClass().getName()+": recursiveCopy(): Failed to get list of files.");
			logger.severe("ERROR TRACE: "+org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));	
		}
		return file_copied_count;
	}
	@Override
	public List<String> getAllFileNamesFromServer() {
		/*try {
			logger.info(this.getClass().getSimpleName()+": Trying to change working directory.");
			client.changeWorkingDirectory("./CopyThese");
		} catch (IOException e) {
			logger.severe(this.getClass().getName()+": getAllFileNamesFromServer(): Failed to change working directory.");
			logger.severe("ERROR TRACE: "+org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
		}*/
		/*Create Folder */
		String folderName = ""+System.currentTimeMillis();

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
				if(file.isDirectory() && !file.getName().equalsIgnoreCase(folderName)){
					System.out.println("------->Normal Copy"+(file.getName()));
					file_copied_count = recursiveCopy(client, file, String.format("./%s/", folderName), file_copied_count ,file.getName());
				}
				if(file.getSize()>0 && file!=null){
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
