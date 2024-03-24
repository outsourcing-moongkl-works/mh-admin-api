package org.outsourcing.mhadminapi.entity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.outsourcing.mhadminapi.dto.LogoImgUrlDto;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "logo_img_urls")
@NoArgsConstructor
@Getter
@EntityListeners(AuditingEntityListener.class)
public class LogoImgUrl {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID id;

    @Column
    private String s3Url;

    @Column
    private String cloudfrontUrl;

    @Builder
    public LogoImgUrl(UUID id, String s3Url, String cloudfrontUrl) {
        this.id = id;
        this.s3Url = s3Url;
        this.cloudfrontUrl = cloudfrontUrl;
    }

    public static LogoImgUrl convertLogoImgUrlDtoToEntity(LogoImgUrlDto uploadedLogoImgUrl) {
        return LogoImgUrl.builder()
                .s3Url(uploadedLogoImgUrl.getS3Url())
                .cloudfrontUrl(uploadedLogoImgUrl.getCloudfrontUrl())
                .build();
    }
}
