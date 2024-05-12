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
@Table(name = "terms")
@NoArgsConstructor
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Terms {

    @Id
    @Column(name = "id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID id;

    @Column(name = "terms", nullable = false, columnDefinition = "TEXT")
    private String terms;

    @CreatedDate
    @Column(name = "created_at", length = 20)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", length = 20)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (this.id == null)
            this.id = UUID.randomUUID();
        if (this.createdAt == null)
            this.createdAt = LocalDateTime.now();
    }

    @Builder
    public Terms(String terms, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.terms = terms;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void updateTerms(String terms) {
        this.terms = terms;
    }
}
