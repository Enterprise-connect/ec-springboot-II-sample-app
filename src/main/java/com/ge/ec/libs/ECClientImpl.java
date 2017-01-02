/*
 * Copyright (c) 2016 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 *
 * @author chia.chang@ge.com
 *
 */

package com.ge.ec.libs;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.SystemUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ge.ec.util.CommandCreator;
@Component
public class ECClientImpl implements ECClient {

	@Autowired
	CommandCreator commandCreator;
	private static Process _process;
	@Value("${ec.agent.version}")
	private String VER;
	private static final JSONObject _settings=new JSONObject();;
	private final Logger log = Logger.getAnonymousLogger();

	@SuppressWarnings("resource")
	@Override
	public void copyAllLibraryFiles(String libraryName){
		/*Copry Library Files*/
		log.info("Copying library file for: "+libraryName);
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("files/"+libraryName).getFile());
		File newFile = new File("./"+file.getName());
		if(!newFile.exists()){
			try{
				FileChannel src = new FileInputStream(file).getChannel();
				FileChannel dest = new FileOutputStream(newFile).getChannel();
				dest.transferFrom(src, 0, src.size());
				src.close();
				dest.close();
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
	}
	public ECClientImpl(){
		/*Constructor*/
	}

	private class ThreadProvisioner implements Runnable {
		private Process _proc;
		public ThreadProvisioner(Process proc){
			_proc=proc;
		}

		@Override
		public void run() {

			try{                
				final int exCode=_proc.waitFor();
				System.out.println("Exit Code:"+exCode);                
			} catch(Throwable ex){
				Logger.getLogger(ECClientImpl.class.getName()).log(Level.SEVERE, null, ex);
			}

		}

	}
	@Override
	public void createScriptFile(String fileName, String environmentName){
		/*Creating Script Files*/
		log.info("createScriptFile(): Creating Script File");
		File batFile = new File("./"+fileName);
		if(batFile.exists()){
			log.info("createScriptFile(): File Already Exists, Deleting Old File.");
			batFile.delete();
			batFile = new File("./"+fileName);
		}
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(batFile);
			DataOutputStream dos=new DataOutputStream(fos);
			dos.writeBytes(commandCreator.commandCreate(environmentName));
			dos.close();
			fos.close();
		} catch (Exception e) {
			log.severe("createScriptFile(): Error in creating Script File: "+e.getMessage());
		} 
	}
	@Override
	public boolean launch() throws IOException {
		/*Launch The Script*/
		if (_process!=null&&_process.isAlive()){
			log.info("launch(): Process Is Already Active, Won't Re-Initialize Scripts.");
			return true;
		}
		else{
			String _ec_art="";
			if (SystemUtils.IS_OS_LINUX){
				log.info("launch(): Linux Environment");
				copyAllLibraryFiles("ecagent_linux");
				//copyAllLibraryFiles("script.sh");
				createScriptFile("script.sh", "./ecagent_linux");
				_ec_art="script.sh";
				File file = new File("./"+_ec_art);
				String btracePath = file.getAbsolutePath();
				log.info("File Path: "+btracePath);
				String cmd = "chmod +x " + btracePath;
				Runtime run = Runtime.getRuntime();
				Process pr = run.exec(cmd);
				try {
					pr.waitFor();
					file = new File("./ecagent_linux");
					btracePath = file.getAbsolutePath();
					log.info("Linux Environemnt: CHMOD: File Path for ECAgent Linux: "+btracePath);
					cmd = "chmod +x " + btracePath;
					log.info("Linux Environemnt: CHMOD: Getting Runtime");
					run = Runtime.getRuntime();
					log.info("Linux Environemnt: CHMOD: Running Process");
					pr = run.exec(cmd);
					log.info("Linux Environemnt: CHMOD: Waiting For Process To Terminate");
					pr.waitFor();
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}

			} else if(SystemUtils.IS_OS_MAC){
				log.info("launch(): MAC Environment");
				copyAllLibraryFiles("ecagent_darwin");
				//copyAllLibraryFiles("script.sh");
				createScriptFile("script.sh", "./ecagent_darwin");
				_ec_art="script.sh";
				File file = new File("./"+_ec_art);
				String btracePath = file.getAbsolutePath();
				log.info("File Path: "+btracePath);
				String cmd = "chmod +x " + btracePath;
				Runtime run = Runtime.getRuntime();
				Process pr = run.exec(cmd);
				try {
					pr.waitFor();
					file = new File("./ecagent_darwin");
					btracePath = file.getAbsolutePath();
					log.info("Mac Environemnt: CHMOD: File Path for ECAgent Darwin: "+btracePath);
					cmd = "chmod +x " + btracePath;
					log.info("Mac Environemnt: CHMOD: Getting Runtime");
					run = Runtime.getRuntime();
					log.info("Mac Environemnt: CHMOD: Running Process");
					pr = run.exec(cmd);
					log.info("Mac Environemnt: CHMOD: Waiting For Process To Terminate");
					pr.waitFor();
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
			}else if (SystemUtils.IS_OS_WINDOWS){
				log.info("launch(): Windows Environment");
				copyAllLibraryFiles("ecagent_windows.exe");
				//copyAllLibraryFiles("script.bat");
				createScriptFile("script.bat", "ecagent_windows.exe");
				_ec_art="script.bat";
			}
			try {
				Thread.sleep(500L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			_process = new ProcessBuilder().command("./"+_ec_art,"&").inheritIO().start();
			Thread _thd = new Thread(new ThreadProvisioner(_process));
			_thd.start();
			return true;
		}		
	}

	@Override
	public boolean isAlive() {
		/*Check if Process is Active*/
		boolean aliveFlag = false;
		if(_process!=null)
			aliveFlag = _process.isAlive();
		log.info("isAlive(): Check Process Status. Currently: "+aliveFlag);
		return aliveFlag;
	}

	@Override
	public boolean terminate(){
		/*Terminate The Process*/
		if(_process!=null){
			_process.destroy();
			_process=null;
		}
		return true;    

	}

	@SuppressWarnings("unchecked")
	@Override
	public void setSetting(String key, Object val){
		_settings.put(key,val);
	}

	@Override
	public String getSetting(String key) {
		Object val=_settings.get(key);
		return String.valueOf(val);
	}

	@Override
	public String version() {
		return this.VER;
	}
}