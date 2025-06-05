package org.outsourcing.mhadminapi.entity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.outsourcing.mhadminapi.dto.EnterpriseDto;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.sql.Types;
import java.util.UUID;

@Entity
@Table(name = "logo_img_urls")
@NoArgsConstructor
@Getter
@EntityListeners(AuditingEntityListener.class)
public class LogoImgUrl {
    @Id
    @JdbcTypeCode(Types.BINARY)
    @Column(name = "id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID id;

    @Column
    private String s3Url;

    @Column
    private String cloudfrontUrl;

    @OneToOne(mappedBy = "logoImgUrl", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Enterprise enterprise;

    @PrePersist
    public void setLogoImgUrlId() {
        if (id == null) {
            id = UUID.randomUUID();
        }
    }

    @Builder
    public LogoImgUrl(UUID id, String s3Url, String cloudfrontUrl, Enterprise enterprise) {
        this.id = id;
        this.s3Url = s3Url;
        this.cloudfrontUrl = cloudfrontUrl;
        this.enterprise = enterprise;
    }

    public static LogoImgUrl convertLogoImgUrlDtoToEntity(EnterpriseDto.LogoImgUrl uploadedLogoImgUrl) {
        return LogoImgUrl.builder()
                .s3Url(uploadedLogoImgUrl.getS3Url())
                .cloudfrontUrl(uploadedLogoImgUrl.getCloudfrontUrl())
                .build();
    }

    public void setEnterprise(Enterprise enterprise) {
        this.enterprise = enterprise;
    }
}
