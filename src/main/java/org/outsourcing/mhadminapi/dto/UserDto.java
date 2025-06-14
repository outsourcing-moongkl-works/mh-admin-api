package org.outsourcing.mhadminapi.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CreateRequest {
        private String email;
        private String password;
        private String nickname;
        private String phoneNumber;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ReadResponse {
        private UUID userId;
        private String email;
        private String password;
        private String gender;
        private String country;
        private String phoneNumber;
        private PausingStatus pausingStatus;

        public ReadResponse(UUID userId, String email, String password, String gender, String country, String phoneNumber) {
            this.userId = userId;
            this.email = email;
            this.password = password;
            this.gender = gender;
            this.country = country;
            this.phoneNumber = phoneNumber;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DeleteResponse{
        private LocalDateTime deletedAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ReadUserSkinResponse{
        private UUID userId;
        private UUID postId;
        private String email;
        private String skinCloudfrontUrl;
        private String storyCloudfrontUrl;
        private String country;
        private Boolean isPublic;
        private LocalDateTime createdAt;

        public ReadUserSkinResponse(UUID userId, UUID postId, String email, String skinCloudfrontUrl, String storyCloudfrontUrl, String country, Boolean isPublic, LocalDateTime createdAt) {
            this.userId = userId;
            this.postId = postId;
            this.email = email;
            this.skinCloudfrontUrl = skinCloudfrontUrl;
            this.storyCloudfrontUrl = storyCloudfrontUrl;
            this.country = country;
            this.isPublic = isPublic;
            this.createdAt = createdAt;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DeleteUserPostResponse{

        private LocalDateTime deletedAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PauseResponse {
        private LocalDateTime pausedAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PauseRequest {
        private String userId;
        private int days;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DeleteUserPostRequest {
        private String postId;
        private String userId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DeleteUserRequest {
        private String userId;
    }
}
