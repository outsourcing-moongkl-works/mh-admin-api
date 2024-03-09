package org.outsourcing.mhadminapi.controller;

import lombok.extern.slf4j.Slf4j;
import org.outsourcing.mhadminapi.dto.MessageDto;
import org.outsourcing.mhadminapi.sqs.SqsSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
public class TestController {

    @Autowired
    private SqsSender sqsSender;

    @PostMapping("/send")
    public void send() {
        Map<String, String> messageContent = new HashMap<>();

        messageContent.put("key1", "value1");
        messageContent.put("key2", "value2");

        MessageDto messageDto = MessageDto.builder()
                .from("mhadminapi")
                .topic("create enterprise skin")
                .message(messageContent)
                .build();
        sqsSender.sendToSQS(messageDto);
    }
}
