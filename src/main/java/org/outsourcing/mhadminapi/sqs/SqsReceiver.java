package org.outsourcing.mhadminapi.sqs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.annotation.SqsListener;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.outsourcing.mhadminapi.dto.MessageDto;
import org.outsourcing.mhadminapi.dto.ResponseDto;
import org.outsourcing.mhadminapi.entity.Enquiry;
import org.outsourcing.mhadminapi.entity.Story;
import org.outsourcing.mhadminapi.entity.User;
import org.outsourcing.mhadminapi.entity.UserSkin;
import org.outsourcing.mhadminapi.exception.SqsErrorResult;
import org.outsourcing.mhadminapi.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.model.UserErrorException;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service @RequiredArgsConstructor
public class SqsReceiver {

    private final UserRepository userRepository;
    private final UserSkinRepository userSkinRepository;
    private final EnquiryRepository enquiryRepository;
    private final ObjectMapper objectMapper;
    private final StoryRepository storyRepository;

    @Transactional
    //@SqsListener("MhAppSaying")
    @SqsListener(value = "${spring.cloud.aws.sqs.queue-name}")
    public void receiveMessage(final String message) throws JsonProcessingException {
        MessageDto messageDto = objectMapper.readValue(message, MessageDto.class);

        if(!"mh-app-api".equals(messageDto.getFrom())){
            log.error("Invalid sender: " + messageDto.getFrom());
//            SqsErrorResult sqsErrorResult = SqsErrorResult.INVALID_SENDER;
//
//            ResponseDto responseDto = ResponseDto.error(sqsErrorResult.getMessage());
            return;
        }
        switch (messageDto.getTopic()){
            case "create user":
                createUser(messageDto);
                break;

                //app의 post id 넘어옴
            case "create user skin":
                createUserSkin(messageDto);
                break;
            case "withdraw user":
                withdrawUser(messageDto);
                break;

                //app의 post id 넘어옴
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
            case "change post ispublic":
                changePostIsPublic(messageDto);
                break;

        }
    }

    @Transactional
    public void changePostIsPublic(MessageDto messageDto) {

        log.info("changePostIsPublic: " + messageDto.getMessage().get("id"));

        Optional<UserSkin> userSkin = userSkinRepository.findUserSkinById(UUID.fromString(messageDto.getMessage().get("id")));
        if(userSkin.isEmpty()){
            log.info("UserSkin does not exist: " + messageDto.getMessage().get("id"));
            return;
        }

        userSkin.get().updateIsPublic();

        userSkinRepository.save(userSkin.get());
    }

    @Transactional
    public void createEnquiry(MessageDto messageDto) {

        log.info("createEnquiry: " + messageDto.getMessage().get("email"));

        Enquiry enquiry = Enquiry.builder()
                .email(messageDto.getMessage().get("email"))
                .title(messageDto.getMessage().get("title"))
                .content(messageDto.getMessage().get("content"))
                .createdAt(LocalDateTime.parse(messageDto.getMessage().get("createdAt")))
                .isReplied(false)
                .build();
        enquiry.setEnquiryId();

        enquiryRepository.save(enquiry);
    }

    @Transactional
    public void increaseUseCount(MessageDto messageDto) {

        log.info("increaseUseCount: " + messageDto.getMessage().get("id"));

        Optional<Story> story = storyRepository.findById(UUID.fromString(messageDto.getMessage().get("id")));

        if(story.isEmpty()){
            log.info("Story does not exist: " + messageDto.getMessage().get("id"));
            return;
        }

        story.get().increaseUseCount();
        storyRepository.save(story.get());
    }

    @Transactional
    public void increaseShareCount(MessageDto messageDto) {

        log.info("increaseShareCount: " + messageDto.getMessage().get("id"));

        Optional<Story> story = storyRepository.findById(UUID.fromString(messageDto.getMessage().get("id")));

        if(story.isEmpty()){
            log.info("Story does not exist: " + messageDto.getMessage().get("id"));
            return;
        }

        story.get().increaseShareCount();
        storyRepository.save(story.get());
    }

    @Transactional
    public void increaseViewCount(MessageDto messageDto) {

        log.info("increaseViewCount: " + messageDto.getMessage().get("id"));

        Optional<Story> story = storyRepository.findById(UUID.fromString(messageDto.getMessage().get("id")));

        if(story.isEmpty()){
            log.info("Story does not exist: " + messageDto.getMessage().get("id"));
            return;
        }

        story.get().increaseViewCount();
        storyRepository.save(story.get());
    }

    @Transactional
    public void createUser(MessageDto messageDto) {
        String idStr = messageDto.getMessage().get("id");
        String email = messageDto.getMessage().get("email");

        if (idStr == null || email == null) {
            log.error("Invalid message data: ID or email is missing");
            return; // 또는 예외를 던지거나, 오류 메시지를 반환할 수 있습니다.
        }

        UUID id = UUID.fromString(idStr);

        log.info("createUser: " + messageDto.getMessage().get("email") + ", " + id);

        if(userRepository.existsByEmail(messageDto.getMessage().get("email"))){
            log.info("User already exists: " + messageDto.getMessage().get("email"));
            return;
        }

        User user = User.builder()
                .id(id)
                .email(messageDto.getMessage().get("email"))
                .password(messageDto.getMessage().get("password"))
                .phoneNumber(messageDto.getMessage().get("phoneNumber"))
                .gender(messageDto.getMessage().get("gender"))
                .country(messageDto.getMessage().get("country"))
                .createdAt(LocalDateTime.parse(messageDto.getMessage().get("createdAt")))
                .build();

        userRepository.save(user);
    }
    @Transactional
    public void createUserSkin(MessageDto messageDto) {

        log.info("createUserSkin: " + messageDto.getMessage().get("id"));

        if(userSkinRepository.existsById(UUID.fromString(messageDto.getMessage().get("id")))){
            log.info("UserSkin already exists: " + messageDto.getMessage().get("id"));
            return;
        }

        //userSkin 만드는데 필요한 모든 정보가 messageDto에 있어야 함
        if(messageDto.getMessage().get("storyCloudfrontUrl") == null || messageDto.getMessage().get("skinCloudfrontUrl") == null
                || messageDto.getMessage().get("country") == null || messageDto.getMessage().get("isPublic") == null
                || messageDto.getMessage().get("userId") == null){
            log.error("Invalid message data: some data is missing");
            return;
        }

        UserSkin userSkin = UserSkin.builder()
                .id(UUID.fromString(messageDto.getMessage().get("id")))
                .storyCloudfrontUrl(messageDto.getMessage().get("storyCloudfrontUrl"))
                .skinCloudfrontUrl(messageDto.getMessage().get("skinCloudfrontUrl"))
                .country(messageDto.getMessage().get("country"))
                .isPublic(Boolean.parseBoolean(messageDto.getMessage().get("isPublic")))
                .build();

        //Optional<User> user = userRepository.findById(UUID.fromString(messageDto.getMessage().get("userId"))).get();

        User user = userRepository.findById(UUID.fromString(messageDto.getMessage().get("userId")))
                .orElseThrow(() -> new RuntimeException("User not found"));
        log.info("User: " + user.getEmail());

        userSkin.updateUser(user);

        userSkinRepository.save(userSkin);
    }
    @Transactional
    void deleteUserSkin(MessageDto messageDto) {

        log.info("deleteUserSkin: " + messageDto.getMessage().get("id"));

        if(!userSkinRepository.existsById(UUID.fromString(messageDto.getMessage().get("id")))){
            log.info("UserSkin does not exist: " + messageDto.getMessage().get("id"));
            return;
        }

        userSkinRepository.deleteById(UUID.fromString(messageDto.getMessage().get("id")));
    }

    @Transactional
    void withdrawUser(MessageDto messageDto) {

        log.info("withdrawUser: " + messageDto.getMessage().get("id"));

        if(!userRepository.existsById(UUID.fromString(messageDto.getMessage().get("id")))){
            log.info("User does not exist: " + messageDto.getMessage().get("id"));
            return;
        }

        userSkinRepository.deleteAllByUser(UUID.fromString(messageDto.getMessage().get("id")));
        userRepository.deleteById(UUID.fromString(messageDto.getMessage().get("id")));
    }
}
