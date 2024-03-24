package org.outsourcing.mhadminapi.entity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "managers")
@NoArgsConstructor
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Manager {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "phone_number", nullable = false, length = 50)
    private String phoneNumber;

    @Column(name = "email", nullable = false, length = 50)
    private String email;

    @CreatedDate
    @Column(name = "created_at", length = 20)
    private LocalDateTime createdAt;



    @PrePersist
    public void setManagerId() {
        if (id == null) {
            id = UUID.randomUUID();
        }
    }

    @Builder
    public Manager(String name, String phoneNumber, String email, LocalDateTime createdAt) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.createdAt = createdAt;
    }

    public void setEnterprise
}
