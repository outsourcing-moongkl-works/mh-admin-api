package org.outsourcing.mhadminapi.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import jakarta.persistence.*;
import org.outsourcing.mhadminapi.dto.UserSkinDto;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_skins")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
public class UserSkin {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(nullable = false)
    private String storyCloudfrontUrl;

    @Column(nullable = false)
    private String skinCloudfrontUrl;

    @Column(nullable = false)
    private String country;

    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "user_skin_fk_user_id"))
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private Boolean isPublic;

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

//    public static UserSkin convertUserSkinDtoToEntity(UserSkinDto uploadedUserSkinUrl) {
//        final UserSkin userSkin = UserSkin.builder()
//                .id(uploadedUserSkinUrl.getId())
//                .storyCloudfrontUrl(uploadedUserSkinUrl.getStoryCloudfrontUrl())
//                .skinCloudfrontUrl(uploadedUserSkinUrl.getSkinCloudfrontUrl())
//                .country(uploadedUserSkinUrl.getCountry())
//                .createdAt(uploadedUserSkinUrl.getCreatedAt())
//                .build();
//        return userSkin;
//    }

    public void updateUser(User user) {
        this.user = user;
        user.addUserSkin(this);
    }

    public void updateIsPublic() {
        this.isPublic = !this.isPublic;
    }
}

