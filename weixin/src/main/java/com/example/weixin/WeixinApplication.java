package com.example.weixin;

import com.example.commons.domain.InMessage;
import com.example.commons.service.JsonRedisSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootApplication
public class WeixinApplication {

	@Bean
	public RedisTemplate<String, InMessage> inMessageTemplate(//
			@Autowired RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<String, InMessage> template = new RedisTemplate<>();
		template.setConnectionFactory(redisConnectionFactory);
		template.setValueSerializer(new JsonRedisSerializer());
		return template;
	}

	public static void main(String[] args) {
		SpringApplication.run(WeixinApplication.class, args);
	}

}
