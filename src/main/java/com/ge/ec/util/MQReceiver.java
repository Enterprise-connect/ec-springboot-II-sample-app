package com.ge.ec.util;

import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

import org.springframework.stereotype.Component;

@Component
public class MQReceiver {
    private CountDownLatch latch = new CountDownLatch(1);
    private final Logger log = Logger.getLogger(this.getClass().getName());
    public void receiveMessage(String message) {
    	log.info("Received <" + message + ">");
        latch.countDown();
    }
    
    public void receiveMessage(Object message) {
    	log.info("Received <" + message + ">");
        latch.countDown();
    }

    public CountDownLatch getLatch() {
        return latch;
    }

}