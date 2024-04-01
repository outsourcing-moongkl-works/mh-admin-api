package org.outsourcing.mhadminapi.sqs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.outsourcing.mhadminapi.dto.MessageDto;
import org.outsourcing.mhadminapi.entity.Enquiry;
import org.outsourcing.mhadminapi.entity.Story;
import org.outsourcing.mhadminapi.entity.User;
import org.outsourcing.mhadminapi.entity.UserSkin;
import org.outsourcing.mhadminapi.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service @RequiredArgsConstructor
public class SqsReceiver {

    private final UserRepository userRepository;
    private final UserSkinRepository userSkinRepository;
    private final EnquiryRepository enquiryRepository;
    private final ObjectMapper objectMapper;

    private final StoryRepository storyRepository;

    @SqsListener("MhAppSaying")
    public void receiveMessage(final String message) throws JsonProcessingException {
        MessageDto messageDto = objectMapper.readValue(message, MessageDto.class);

        if(!"mh-app-api".equals(messageDto.getFrom())){
            log.info("Invalid sender");
            return;
        }
        switch (messageDto.getTopic()){
            case "create user":
                createUser(messageDto);
                break;
            case "create user skin":
                createUserSkin(messageDto);
                break;
            case "withdraw user":
                withdrawUser(messageDto);
                break;
            case "delete user skin":
                deleteUserSkin(messageDto);
                break;
            case "increase view count":
                increaseViewCount(messageDto);
                break;
            case "increase share count":
                increaseShareCount(messageDto);
                break;
            case "increase use count":
                increaseUseCount(messageDto);
                break;
            case "create enquiry":
                createEnquiry(messageDto);
                break;

        }

    }

    private void createEnquiry(MessageDto messageDto) {
        Enquiry enquiry = Enquiry.builder()
                .email(messageDto.getMessage().get("email"))
                .title(messageDto.getMessage().get("title"))
                .content(messageDto.getMessage().get("content"))
                .createdAt(LocalDateTime.parse(messageDto.getMessage().get("createdAt")))
                .build();
        enquiry.setEnquiryId();

        enquiryRepository.save(enquiry);
    }

    private void increaseUseCount(MessageDto messageDto) {
        Story story = storyRepository.findById(UUID.fromString(messageDto.getMessage().get("storyId"))).get();
        story.increaseUseCount();
    }

    private void increaseShareCount(MessageDto messageDto) {
        Story story = storyRepository.findById(UUID.fromString(messageDto.getMessage().get("storyId"))).get();
        story.increaseShareCount();
    }

    private void increaseViewCount(MessageDto messageDto) {
        Story story = storyRepository.findById(UUID.fromString(messageDto.getMessage().get("storyId"))).get();
        story.increaseViewCount();
    }

    private void createUser(MessageDto messageDto) {
        User user = User.builder()
                .id(UUID.fromString(messageDto.getMessage().get("id")))
                .email(messageDto.getMessage().get("email"))
                .password(messageDto.getMessage().get("password"))
                .phoneNumber(messageDto.getMessage().get("phoneNumber"))
                .gender(messageDto.getMessage().get("gender"))
                .country(messageDto.getMessage().get("country"))
                .createdAt(LocalDateTime.parse(messageDto.getMessage().get("createdAt")))
                .build();
        userRepository.save(user);
    }
    private void createUserSkin(MessageDto messageDto) {
        UserSkin userSkin = UserSkin.builder()
                .id(UUID.fromString(messageDto.getMessage().get("id")))
                .storyCloudfrontUrl(messageDto.getMessage().get("storyCloudfrontUrl"))
                .skinCloudfrontUrl(messageDto.getMessage().get("skinCloudfrontUrl"))
                .country(messageDto.getMessage().get("country"))
                .build();

        User user = userRepository.findById(UUID.fromString(messageDto.getMessage().get("userId"))).get();
        userSkin.updateUser(user);

        userSkinRepository.save(userSkin);
    }
    private void deleteUserSkin(MessageDto messageDto) {
        userSkinRepository.deleteById(UUID.fromString(messageDto.getMessage().get("id")));
    }

    private void withdrawUser(MessageDto messageDto) {
        userRepository.deleteById(UUID.fromString(messageDto.getMessage().get("id")));
    }
}
