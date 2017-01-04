/**
 * @author Avneesh Srivastava
 * @email avneesh.srivastava@ge.com
 *
 */
package com.ge.ec.service.impl;

import java.io.IOException;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ge.ec.libs.ECClient;
import com.ge.ec.service.ECService;
@Service
public class ECServiceImpl implements ECService {
	
	@Autowired
	ECClient ecClient;
	
	private final Logger log = Logger.getLogger(this.getClass().getName());
	
	@PostConstruct
	@Override
	public void startupECClient() {
		try{
			log.info("Trying To Launch EC Client.");
			ecClient.launch();
			log.info(String.format("%s started.", ecClient.version()));
		}catch(Exception ex){
			log.severe(String.format("%s failed to start. Error: %s", ecClient.version(), ex.getMessage()));
		}
	}
	@Override
	public boolean isECStarted(){
		if(ecClient.isAlive())
			return true;
		else
			return false;
	}
	@Override
	public int killEC(){
		if(ecClient.isAlive()){
			try {
				if(ecClient.terminate()){
					log.info("EC Client Termination Successful");
					return 0; //Sucessful Termination
					}
				else{ 
					log.info("EC Client Termination Unsuccessful");
					return 1; //Unsuccessful Termination
				}
			} catch (IOException e) {
				log.severe(String.format("EC Client Termination Failed %s", e.getMessage()));
				return -1; //Error
			}
		}
		else
			return -2; //EC Client Not Started. 
	}

}
