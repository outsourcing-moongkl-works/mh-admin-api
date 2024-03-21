package org.outsourcing.mhadminapi.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import jakarta.persistence.*;
import org.outsourcing.mhadminapi.dto.UserSkinDto;

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

    @Builder
    public UserSkin(UUID id, String storyCloudfrontUrl, String skinCloudfrontUrl, String country){
        this.id = id;
        this.storyCloudfrontUrl = storyCloudfrontUrl;
        this.skinCloudfrontUrl = skinCloudfrontUrl;
        this.country = country;
    }

    public static UserSkin convertUserSkinDtoToEntity(UserSkinDto uploadedUserSkinUrl) {
        final UserSkin userSkin = UserSkin.builder()
                .storyCloudfrontUrl(uploadedUserSkinUrl.getStoryCloudfrontUrl())
                .skinCloudfrontUrl(uploadedUserSkinUrl.getSkinCloudfrontUrl())
                .country(uploadedUserSkinUrl.getCountry())
                .build();
        return userSkin;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

