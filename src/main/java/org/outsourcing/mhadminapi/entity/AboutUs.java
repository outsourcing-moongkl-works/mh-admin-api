package org.outsourcing.mhadminapi.entity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.sql.Types;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "about_us")
@NoArgsConstructor
@Getter
@EntityListeners(AuditingEntityListener.class)
public class AboutUs {
    @Id
    @JdbcTypeCode(Types.BINARY)
    @Column(name = "id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID id;

    @Column(name = "about_us", nullable = false, columnDefinition = "TEXT")
    private String aboutUs;

    @CreatedDate
    @Column(name = "created_at", length = 20)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", length = 20)
    private LocalDateTime updatedAt;

    @PrePersist
    public void setAboutUsId() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (this.createdAt == null)
            this.createdAt = LocalDateTime.now();
    }

    @Builder
    public AboutUs(String aboutUs, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.aboutUs = aboutUs;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void updateAboutUs(String aboutUs) {
        this.aboutUs = aboutUs;
    }
}
