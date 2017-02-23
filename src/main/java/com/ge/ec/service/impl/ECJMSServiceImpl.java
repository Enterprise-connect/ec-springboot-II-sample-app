package com.ge.ec.service.impl;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.ge.ec.util.MQReceiver;
@Component
public class ECJMSServiceImpl {
	private final static String QUEUE_NAME = "ec-queue";
	private final static String EXCHANGE_TOPIC = "ec-queue-exchange";
	private final Logger log = Logger.getLogger(this.getClass().getName());
	@Autowired
	RabbitTemplate rabbitTemplate;
	
	@Autowired
	MQReceiver mqReceiver; 
	public void sendMessage() throws Exception{
		log.info("Sending Message");
		rabbitTemplate.convertAndSend(QUEUE_NAME, "Sample Message from EC Agent.");
		mqReceiver.getLatch().await(10000, TimeUnit.MILLISECONDS);
	}
	@Bean
	Queue queue() {
		log.info("Defined QUEUE: "+QUEUE_NAME);
		return new Queue(QUEUE_NAME, false);
	}

	@Bean
	TopicExchange exchange() {
		log.info("Exchange Topic: "+EXCHANGE_TOPIC);
		return new TopicExchange(EXCHANGE_TOPIC);
	}

	@Bean
	Binding binding(Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(QUEUE_NAME);
	}

	@Bean
	SimpleMessageListenerContainer container(org.springframework.amqp.rabbit.connection.ConnectionFactory connectionFactory,
			MessageListenerAdapter listenerAdapter) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(QUEUE_NAME);
		container.setMessageListener(listenerAdapter);
		container.setAcknowledgeMode(AcknowledgeMode.AUTO);
		return container;
	}

	@Bean
	MessageListenerAdapter listenerAdapter(MQReceiver receiver) {
		//The adapter takes an input of your message listener and "receiveMessage" is the methodName
		return new MessageListenerAdapter(receiver, "receiveMessage");
	}
}
