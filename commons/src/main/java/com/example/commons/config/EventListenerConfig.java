package com.example.commons.config;

import java.util.ArrayList;
import java.util.List;

import com.example.commons.domain.InMessage;
import com.example.commons.domain.ResponseToken;
import com.example.commons.domain.event.EventInMessage;
import com.example.commons.service.JsonRedisSerializer;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.Topic;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

public interface EventListenerConfig extends//

		CommandLineRunner, //

		DisposableBean {


	public final Object stopMonitor = new Object();

	@Override
	public default void run(String... args) throws Exception {
		new Thread(() -> {
			synchronized (stopMonitor) {
				try {

					stopMonitor.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	@Override
	public default void destroy() throws Exception {

		synchronized (stopMonitor) {
			stopMonitor.notify();
		}
	}


	@Bean
	public default RedisTemplate<String, InMessage> inMessageTemplate(//
			@Autowired RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<String, InMessage> template = new RedisTemplate<>();
		template.setConnectionFactory(redisConnectionFactory);


		template.setValueSerializer(new JsonRedisSerializer());

		return template;
	}

	@Bean
	public default RedisTemplate<String, ResponseToken> tokenRedisTemplate(//
			@Autowired RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<String, ResponseToken> template = new RedisTemplate<>();
		template.setConnectionFactory(redisConnectionFactory);
		template.setValueSerializer(new JsonRedisSerializer());

		return template;
	}

	@Bean
	public default MessageListenerAdapter messageListener(
			@Autowired RedisTemplate<String, InMessage> inMessageTemplate) {
		MessageListenerAdapter adapter = new MessageListenerAdapter();

		adapter.setSerializer(inMessageTemplate.getValueSerializer());


		adapter.setDelegate(this);

		adapter.setDefaultListenerMethod("handle");

		return adapter;
	}

	@Bean
	public default RedisMessageListenerContainer messageListenerContainer(//
			@Autowired RedisConnectionFactory redisConnectionFactory, //
			@Autowired MessageListener l) {

		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(redisConnectionFactory);

		List<Topic> topics = new ArrayList<>();

		topics.add(new ChannelTopic("lzw_event"));
		container.addMessageListener(l, topics);

		return container;
	}

	public void handle(EventInMessage msg);
}
