package org.outsourcing.mhadminapi.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LogoImgUrlDto {
    private String s3Url;
    private String cloudfrontUrl;
}
