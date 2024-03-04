package org.outsourcing.mhadminapi.sqs;

import io.awspring.cloud.sqs.operations.SendResult;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

@Component
@RequiredArgsConstructor
public class AmazonSQSSender {

    private final SqsTemplate template;

    @Value("${application.amazon.sqs.queue-name}")
    private String queueName;

    public AmazonSQSSender(SqsAsyncClient sqsAsyncClient) {
        this.template = SqsTemplate.newTemplate(sqsAsyncClient);
    }

    public SendResult<String> sendMessage(String queueName, String groupId, String message) {
        System.out.println("Sender: " + message);
        return template.send(to -> to
                .queue(queueName)
                .messageGroupId(groupId)
                .messageDeduplicationId(groupId)
                .payload(message));
    }

}
