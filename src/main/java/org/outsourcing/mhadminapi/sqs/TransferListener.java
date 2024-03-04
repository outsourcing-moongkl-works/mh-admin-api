package org.outsourcing.mhadminapi.sqs;

import io.awspring.cloud.sqs.annotation.SqsListener;
import org.springframework.stereotype.Component;

@Component
public class TransferListener {

    @SqsListener("${spring.cloud.aws.sqs.queue-name}")
    public void messageListener(String message) {
        System.out.println("Listener: " + message);
    }

}