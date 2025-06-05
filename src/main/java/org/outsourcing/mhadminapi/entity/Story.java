package org.outsourcing.mhadminapi.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.sql.Types;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "stories")
@NoArgsConstructor
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Story {

    @Id
    @JdbcTypeCode(Types.BINARY)
    @Column(name = "id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID id;

    @ColumnDefault("0")
    @Column(name = "use_count", nullable = false)
    private long useCount;

    @ColumnDefault("0")
    @Column(name = "share_count", nullable = false)
    private long shareCount;

    @ColumnDefault("0")
    @Column(name = "view_count", nullable = false)
    private long viewCount;

    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean isPublic; //중지 여부

    @JoinColumn(name = "enterprise_id", foreignKey = @ForeignKey(name = "story_img_url_fk_enterprise_id"))
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private Enterprise enterprise;

    @JoinColumn(name = "story_img_url_id", foreignKey = @ForeignKey(name = "story_fk_story_img_url_id"))
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private StoryImgUrl storyImgUrl;

    @CreatedDate
    @Column(name = "created_at", length = 20)
    private LocalDateTime createdAt;

    @PrePersist
    public void setStoryId() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (this.createdAt == null)
            this.createdAt = LocalDateTime.now();
    }

    @Builder
    public Story(Enterprise enterprise, long useCount, long shareCount, long viewCount, Boolean isPublic, StoryImgUrl storyImgUrl, LocalDateTime createdAt) {
        this.enterprise = enterprise;
        this.useCount = useCount;
        this.shareCount = shareCount;
        this.viewCount = viewCount;
        this.isPublic = isPublic != null ? isPublic : true;
        this.storyImgUrl = storyImgUrl;
        this.createdAt = createdAt;
    }
    public void increaseUseCount() {
        this.useCount++;
    }
    public void increaseShareCount() {
        this.shareCount++;
    }
    public void increaseViewCount() {
        this.viewCount++;
    }

    public void setEnterprise(Enterprise enterprise) {
        this.enterprise = enterprise;
        enterprise.addStory(this);
    }
    public boolean isPublic() {
        return this.isPublic;
    }

    public void changeIsPublic() {
        this.isPublic = !this.isPublic;
    }

    public void setStoryImgUrl(StoryImgUrl storyImgUrl) {
        this.storyImgUrl = storyImgUrl;
        storyImgUrl.setStory(this);
    }

}
