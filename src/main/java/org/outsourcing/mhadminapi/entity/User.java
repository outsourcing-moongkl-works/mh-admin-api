package org.outsourcing.mhadminapi.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "email", nullable = false, length = 50)
    private String email;

    @Column(name = "gender", length = 10)
    private String gender;

    @Column(name = "country", length = 50)
    private String country;

    @Column(name = "phone_number", length = 100)
    private String phoneNumber;

    @CreatedDate
    @Column(name = "created_at", nullable = false, length = 20)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<UserHere> userHeres;

    public void addUserSkin(UserHere userHere) {
        this.userHeres.add(userHere);
    }

    @Builder(toBuilder = true)
    public User(UUID id,
                String email,
                String phoneNumber,
                String gender,
                String country,
                LocalDateTime createdAt) {
        this.id = id;
        this.email = email;
        this.gender = gender;
        this.country = country;
        this.phoneNumber = phoneNumber;
        this.createdAt = createdAt;
        this.userHeres = new ArrayList<>();
    }
}

