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
@Table(name = "company_location")
@NoArgsConstructor
@Getter
@EntityListeners(AuditingEntityListener.class)
public class CompanyLocation {
    @Id
    @JdbcTypeCode(Types.BINARY)
    @Column(name = "id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID id;

    @Column(name = "company_location", nullable = false, length = 500)
    private String companyLocation;

    @CreatedDate
    @Column(name = "created_at", length = 20)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", length = 20)
    private LocalDateTime updatedAt;

    @PrePersist
    public void setCompanyLocationId() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (this.createdAt == null)
            this.createdAt = LocalDateTime.now();
    }

    @Builder
    public CompanyLocation(String companyLocation, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.companyLocation = companyLocation;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void updateCompanyLocation(String companyLocation) {
        this.companyLocation = companyLocation;
    }
}
