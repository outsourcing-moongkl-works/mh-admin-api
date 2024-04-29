package org.outsourcing.mhadminapi.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class UserSkinDto {
    private UUID id;
    private String storyCloudfrontUrl;
    private String skinCloudfrontUrl;
    private String country;
    private LocalDateTime createdAt;
    private boolean isPublic;
}
