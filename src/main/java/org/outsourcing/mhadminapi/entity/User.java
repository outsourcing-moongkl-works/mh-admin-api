package org.outsourcing.mhadminapi.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;

import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@NoArgsConstructor
@Getter
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @JdbcTypeCode(Types.BINARY)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "email", nullable = false, length = 50)
    private String email;

    @Column(name = "password", length = 100)
    private String password;

    @Column(name = "gender", length = 10)
    private String gender;

    @Column(name = "country", length = 50)
    private String country;

    @Column(name = "phone_number", length = 100)
    private String phoneNumber;

    @CreatedDate
    @Column(name = "created_at", length = 20)
    private LocalDateTime createdAt;

//    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<UserSkin> userSkins;

    @PrePersist
    public void setUserId() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (this.createdAt == null)
            this.createdAt = LocalDateTime.now();
    }

//    public void addUserSkin(UserSkin userSkin) {
//        this.userSkins.add(userSkin);
//    }

    @Builder(toBuilder = true)
    public User(UUID id,
                String email,
                String password,
                String phoneNumber,
                String gender,
                String country,
                LocalDateTime createdAt) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.country = country;
        this.phoneNumber = phoneNumber;
        this.createdAt = createdAt;
    }
}

