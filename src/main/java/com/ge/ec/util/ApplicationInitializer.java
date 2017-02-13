/**
 * @author Avneesh Srivastava
 * @email avneesh.srivastava@ge.com
 *
 */
package com.ge.ec.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.embedded.AnnotationConfigEmbeddedWebApplicationContext;
import org.springframework.cloud.Cloud;
import org.springframework.cloud.CloudException;
import org.springframework.cloud.CloudFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.core.env.ConfigurableEnvironment;

public class ApplicationInitializer implements ApplicationContextInitializer<AnnotationConfigEmbeddedWebApplicationContext> {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public void initialize(
			AnnotationConfigEmbeddedWebApplicationContext applicationContext) {
		Cloud cloud = getCloud();
		ConfigurableEnvironment appEnvironment = applicationContext
				.getEnvironment();
		if (cloud != null) {
			log.info("Cloud Environment");
			appEnvironment.addActiveProfile("cloud");
		}else{
			log.info("Local Environment");
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
