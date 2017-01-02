package com.ge.ec;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;

import com.ge.ec.util.ApplicationInitializer;



@SpringBootApplication
public class ECClientDemoApplication {

	public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext applicationContext = new SpringApplicationBuilder(ECClientDemoApplication.class)
				.initializers(new ApplicationInitializer())
				.run(args);
		applicationContext.addApplicationListener(new ApplicationListener<ContextClosedEvent>() {

			@Override
			public void onApplicationEvent(ContextClosedEvent event) {
				System.out.println("On Application Shutdown");
			}
		});
	}

	public SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		application.initializers(new ApplicationInitializer());
		application.sources(ECClientDemoApplication.class);
		return application;
	}
}

