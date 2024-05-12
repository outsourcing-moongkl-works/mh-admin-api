package org.outsourcing.mhadminapi.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import jakarta.persistence.*;
import org.outsourcing.mhadminapi.dto.UserSkinDto;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_skins")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
public class UserSkin {

    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "story_cloudfront_url", nullable = false)
    private String storyCloudfrontUrl;

    @Column(name = "skin_cloudfront_url", nullable = false)
    private String skinCloudfrontUrl;

    @Column(nullable = false)
    private String country;

    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "user_skin_fk_user_id"))
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @CreatedDate
    @Column(name = "created_at", length = 20)
    private LocalDateTime createdAt;

    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean isPublic;

    @PrePersist
    public void prePersist() {
        if (this.id == null)
            this.id = UUID.randomUUID();
    }

    @Builder
    public UserSkin(UUID id, String storyCloudfrontUrl, String skinCloudfrontUrl, String country, LocalDateTime createdAt, User user, Boolean isPublic){
        this.id = id;
        this.storyCloudfrontUrl = storyCloudfrontUrl;
        this.skinCloudfrontUrl = skinCloudfrontUrl;
        this.country = country;
        this.createdAt = createdAt;
        this.user = user;
        this.isPublic = isPublic;
    }

    public void updateUser(User user) {
        this.user = user;
        user.addUserSkin(this);
    }

    public void updateIsPublic() {
        this.isPublic = !this.isPublic;
    }
}

