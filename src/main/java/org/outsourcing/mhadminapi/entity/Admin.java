package org.outsourcing.mhadminapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.outsourcing.mhadminapi.vo.Role;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "admins")
@NoArgsConstructor
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Admin implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @JdbcTypeCode(Types.BINARY)
    @Column(name = "id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID id;

    @Column(name = "email", nullable = false, length = 50, unique = true)
    private String email;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 20)
    private Role role;

    @CreatedDate
    @Column(name = "created_at", length = 20)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", length = 20)
    private LocalDateTime updatedAt;

    @PrePersist
    public void setAdminId() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (this.createdAt == null)
            this.createdAt = LocalDateTime.now();
    }

    @Builder
    public Admin(String email, String password, Role role, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = UUID.randomUUID();
        this.email = email;
        this.password = password;
        this.role = role;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
