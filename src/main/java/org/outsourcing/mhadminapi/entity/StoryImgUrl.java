package org.outsourcing.mhadminapi.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.outsourcing.mhadminapi.dto.EnterpriseDto;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "story_img_urls")
@NoArgsConstructor
@Getter
@EntityListeners(AuditingEntityListener.class)
public class StoryImgUrl {

    @Id
    @Column(name = "id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID id;

    @Column
    private String s3Url;

    @Column
    private String cloudfrontUrl;

    @OneToOne(mappedBy = "storyImgUrl", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Story story;

    @PrePersist
    public void prePersist() {
        if (this.id == null)
            this.id = UUID.randomUUID();
    }

    @Builder
    public StoryImgUrl(UUID id, String s3Url, String cloudfrontUrl, Story story) {
        this.id = id;
        this.s3Url = s3Url;
        this.cloudfrontUrl = cloudfrontUrl;
        this.story = story;
    }

    public static StoryImgUrl convertStoryImgUrlDtoToEntity(EnterpriseDto.StoryUrl uploadedStoryImgUrl) {
        return StoryImgUrl.builder()
                .s3Url(uploadedStoryImgUrl.getS3Url())
                .cloudfrontUrl(uploadedStoryImgUrl.getCloudfrontUrl())
                .build();
    }

    public void setStory(Story story) {
        this.story = story;
    }

}
