package org.outsourcing.mhadminapi.dto;


import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

public class EnquiryDto {
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class ReplyRequest{
        private String enquiryId;
        private String title;
        private String content;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class ReplyResponse{
        private String email;
        private LocalDateTime createdAt;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class ReadResponse{
        private UUID enquiryId;
        private String email;
        private String title;
        private String content;
        private Boolean isReplied;
        private LocalDateTime createdAt;

        ReadResponse(UUID enquiryId, String email, String title, String content, Boolean isReplied, LocalDateTime createdAt) {
            this.enquiryId = enquiryId;
            this.email = email;
            this.title = title;
            this.content = content;
            this.isReplied = isReplied;
            this.createdAt = createdAt;
        }
    }
}
