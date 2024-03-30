package org.outsourcing.mhadminapi.sqs;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.outsourcing.mhadminapi.dto.MessageDto;
import org.outsourcing.mhadminapi.entity.User;
import org.outsourcing.mhadminapi.entity.UserSkin;
import org.outsourcing.mhadminapi.repository.UserRepository;
import org.outsourcing.mhadminapi.repository.UserSkinRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service @RequiredArgsConstructor
public class SqsReceiver {

    private final UserRepository userRepository;
    private final UserSkinRepository userSkinRepository;

    private final ObjectMapper objectMapper;
    @SqsListener("MhAppSaying")
    public void receiveMessage(MessageDto messageDto) {
        log.info("messageDto received {}", messageDto);

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
        }

    }
    private void createUser(MessageDto messageDto) {
        //messageDto.getMessage();
        /*
        {
            "topic": "create user",
            "message": {
                "id": "user1",
                "email": "user1",
                "password": "

            },
            "from": "mh-admin-api"
         */
        /*
        UUID id,
                String email,
                String password,
                String phoneNumber,
                String gender,
                String country,
                LocalDateTime createdAt
         */

        User user = User.builder()
                .id(UUID.fromString(messageDto.getMessage().get("id")))
                .email(messageDto.getMessage().get("email"))
                .password(messageDto.getMessage().get("password"))
                .phoneNumber(messageDto.getMessage().get("phoneNumber"))
                .gender(messageDto.getMessage().get("gender"))
                .country(messageDto.getMessage().get("country"))
                .build();
        userRepository.save(user);
    }
    private void createUserSkin(MessageDto messageDto) {
        /*
            public UserSkin(UUID id, String storyCloudfrontUrl, String skinCloudfrontUrl, String country, LocalDateTime createdAt, User user){
        this.id = id;
        this.userId =;
        this.storyCloudfrontUrl = storyCloudfrontUrl;
        this.skinCloudfrontUrl = skinCloudfrontUrl;
        this.country = country;
        this.createdAt = createdAt;
    }
         */
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
