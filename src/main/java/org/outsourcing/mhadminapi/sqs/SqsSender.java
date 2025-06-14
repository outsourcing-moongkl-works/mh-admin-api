package org.outsourcing.mhadminapi.sqs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.operations.SendResult;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.extern.slf4j.Slf4j;
import org.outsourcing.mhadminapi.dto.MessageDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class SqsSender {

    @Value("${spring.cloud.aws.sqs.queue-url}")
    private String queueUrl;

    private final SqsTemplate template;

    public SqsSender(SqsAsyncClient sqsAsyncClient) {
        this.template = SqsTemplate.newTemplate(sqsAsyncClient);
    }

    public SendResult<String> sendToSQS(MessageDto messageDto) {
        String jsonMessageBody;
        log.info("Sending message");
        try {
            jsonMessageBody = new ObjectMapper().writeValueAsString(messageDto); // 객체를 JSON 문자열로 변환
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting messageDto to JSON", e);
        }

        return template.send(to -> to
                .queue(queueUrl)
                .payload(jsonMessageBody));
    }

    public MessageDto createMessageDtoFromRequest(String topic, Map<String, String> message) {
        return MessageDto.builder()
                .from("mh-admin-api")
                .topic(topic)
                .message(message)
                .build();
    }
}
