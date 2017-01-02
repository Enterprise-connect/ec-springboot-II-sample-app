package com.ge.ec.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.embedded.AnnotationConfigEmbeddedWebApplicationContext;
import org.springframework.cloud.Cloud;
import org.springframework.cloud.CloudException;
import org.springframework.cloud.CloudFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.core.env.ConfigurableEnvironment;


/**
 * @author Danish Khan
 *
 */

public class ApplicationInitializer implements ApplicationContextInitializer<AnnotationConfigEmbeddedWebApplicationContext> {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Override
	public void initialize(
			AnnotationConfigEmbeddedWebApplicationContext applicationContext) {
		String proxyHost = "sjc1intproxy01.crd.ge.com";
		String proxyPort = "8080";
		Cloud cloud = getCloud();
		ConfigurableEnvironment appEnvironment = applicationContext
				.getEnvironment();
		if (cloud != null) {
			log.info("Cloud Environment");
			appEnvironment.addActiveProfile("cloud");
		}else{
			log.info("Local Environment: Setting Proxy");
			System.setProperty("http.proxyHost", proxyHost); 
			System.setProperty("http.proxyPort", proxyPort); 
			System.setProperty("https.proxyHost", proxyHost); 
			System.setProperty("https.proxyPort", proxyPort);
		}
	}

	private Cloud getCloud() {
		try {
			CloudFactory cloudFactory = new CloudFactory();
			return cloudFactory.getCloud();
		} catch (CloudException ce) {
			return null;
		}
	}

}
