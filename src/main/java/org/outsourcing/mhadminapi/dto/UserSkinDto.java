package org.outsourcing.mhadminapi.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
public class UserSkinDto {
    private String storyCloudfrontUrl;
    private String skinCloudfrontUrl;
    private String country;
}
