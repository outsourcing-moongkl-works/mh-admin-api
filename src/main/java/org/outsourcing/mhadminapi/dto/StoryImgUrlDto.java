package org.outsourcing.mhadminapi.dto;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class StoryImgUrlDto {
    private String s3Url;
    private String cloudfrontUrl;
}
