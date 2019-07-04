package com.example.weixin.controller;

import java.io.StringReader;

import javax.xml.bind.JAXB;

import com.example.commons.domain.InMessage;
import com.example.weixin.service.MessageTypeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/lzw/weixin/receiver")
public class MessageReceiverController {

	private static final Logger LOG = LoggerFactory.getLogger(MessageReceiverController.class);

	@Autowired
	@Qualifier("inMessageTemplate")
	private RedisTemplate<String, InMessage> inMessageTemplate;

	@GetMapping 
	public String echo(//
			@RequestParam("signature") String signature, 
			@RequestParam("timestamp") String timestamp, 
			@RequestParam("nonce") String nonce, 
			@RequestParam("echostr") String echostr
	) {
		return echostr;
	}

	@PostMapping
	public String onMessage(@RequestParam("signature") String signature, 
			@RequestParam("timestamp") String timestamp, 
			@RequestParam("nonce") String nonce, 
			@RequestBody String xml) {
		LOG.debug("收到用户发送给公众号的信息: \n-----------------------------------------\n"
				+ "{}\n-----------------------------------------\n", xml);


		String type = xml.substring(xml.indexOf("<MsgType><![CDATA[") + 18);
		type = type.substring(0, type.indexOf("]]></MsgType>"));

		Class<InMessage> cla = MessageTypeMapper.getClass(type);

		InMessage inMessage = JAXB.unmarshal(new StringReader(xml), cla);

		LOG.debug("转换得到的消息对象 \n{}\n", inMessage.toString());

		inMessageTemplate.convertAndSend("lzw_" + inMessage.getMsgType(), inMessage);


		return "success";
	}
}
