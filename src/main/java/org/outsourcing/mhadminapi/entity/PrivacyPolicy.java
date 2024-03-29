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
@Table(name = "privacy_policy")
@NoArgsConstructor
@Getter
@EntityListeners(AuditingEntityListener.class)
public class PrivacyPolicy{
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID id;

    @Column(name = "privacy_policy", nullable = false, length = 10000)
    private String privacyPolicy;

    @CreatedDate
    @Column(name = "created_at", length = 20)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", length = 20)
    private LocalDateTime updatedAt;

    @PrePersist
    public void setPrivacyPolicyId() {
        if (id == null) {
            id = UUID.randomUUID();
        }
    }

    @Builder
    public PrivacyPolicy(String privacyPolicy, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.privacyPolicy = privacyPolicy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void updatePrivacyPolicy(String privacyPolicy) {
        this.privacyPolicy = privacyPolicy;
    }
}
