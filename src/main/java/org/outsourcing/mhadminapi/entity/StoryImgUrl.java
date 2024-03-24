package org.outsourcing.mhadminapi.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.outsourcing.mhadminapi.dto.StoryImgUrlDto;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;

@Entity
@Table(name = "story_img_urls")
@NoArgsConstructor
@Getter
@EntityListeners(AuditingEntityListener.class)
public class StoryImgUrl {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID id;

    @Column
    private String s3Url;

    @Column
    private String cloudfrontUrl;

    @Column(name = "is_public", nullable = false, length = 10)
    private String isPublic;

    @JoinColumn(name = "enterprise_id", foreignKey = @ForeignKey(name = "story_img_url_fk_enterprise_id"))
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Enterprise enterprise;

    @Builder
    public StoryImgUrl(UUID id, String s3Url, String cloudfrontUrl, String isPublic, Enterprise enterprise) {
        this.id = id;
        this.s3Url = s3Url;
        this.cloudfrontUrl = cloudfrontUrl;
        this.isPublic = isPublic;
        this.enterprise = enterprise;
    }

    public static StoryImgUrl convertStoryImgUrlDtoToEntity(StoryImgUrlDto uploadedStoryImgUrl) {
        return StoryImgUrl.builder()
                .s3Url(uploadedStoryImgUrl.getS3Url())
                .cloudfrontUrl(uploadedStoryImgUrl.getCloudfrontUrl())
                .build();
    }

    public void setEnterprise(Enterprise enterprise) {
        this.enterprise = enterprise;
    }
}
