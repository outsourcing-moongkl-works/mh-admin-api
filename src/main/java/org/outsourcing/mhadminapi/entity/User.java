package org.outsourcing.mhadminapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.outsourcing.mhadminapi.vo.Role;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
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

    @Column(name = "user_email", nullable = false, length = 50)
    private String userEmail;

    @Column(name= "nickname", length = 30)
    private String nickname;

    @Column(name = "gender", length = 10)
    private String gender;

    @Column(name = "country", length = 50)
    private String country;

    @Column(name = "phone_number", length = 100)
    private String phoneNumber;

    @CreatedDate
    @Column(name = "created_at", length = 20)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", length = 20)
    private LocalDateTime updatedAt;

    @PrePersist
    public void setUserId() {
        if (id == null) {
            id = UUID.randomUUID();
        }
    }


}
