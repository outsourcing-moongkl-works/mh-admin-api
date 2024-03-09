package org.outsourcing.mhadminapi.sqs;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.extern.slf4j.Slf4j;
import org.outsourcing.mhadminapi.dto.MessageDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SqsReceiver {

    @Autowired
    private ObjectMapper objectMapper;
/*
    @SqsListener(value = "MhAppSaying", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void receiveStringMessage(final String message) {
        try{
            MessageDto messageDto = objectMapper.readValue(message, MessageDto.class);
            log.info("messageDto received {}", messageDto);

            switch (messageDto.getTopic()){
                case "create enterprise skin":
                    createEnterpriseSkin(messageDto);
                    break;
            }
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        }  catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

 */
    @SqsListener("MhAppSaying")
    public void receiveMessage(MessageDto messageDto) {
        log.info("messageDto received {}", messageDto);

        switch (messageDto.getTopic()){
            case "create enterprise skin":
                createEnterpriseSkin(messageDto);
                break;
        }
    }
    private void createEnterpriseSkin(MessageDto messageDto) {
        //gogogogogogogogo
        log.info("create enterprise skin");
    }
}
