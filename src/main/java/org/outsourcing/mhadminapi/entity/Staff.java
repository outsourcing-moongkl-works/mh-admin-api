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
@Table(name = "staffs")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Staff {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "staff_id", nullable = false, length = 50)
    private String staffId;

    @Column(name = "password", length = 100)
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

}
