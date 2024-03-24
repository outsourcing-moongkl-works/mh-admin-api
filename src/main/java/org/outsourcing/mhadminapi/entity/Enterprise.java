package org.outsourcing.mhadminapi.entity;

import jakarta.persistence.*;
import lombok.Builder;
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
@Table(name = "enterprises")
@NoArgsConstructor
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Enterprise{

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID id;

    @Column(name = "email", nullable = false, length = 50, unique = true)
    private String email;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 20)
    private Role role;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    //사업자번호
    @Column(name = "business_number", nullable = false, length = 50)
    private String businessNumber;

    //법인번호
    @Column(name = "corporate_number", nullable = false, length = 50)
    private String corporateNumber;

    @Column(name = "address", nullable = false, length = 50)
    private String address;

    @Column(name = "is_approved", nullable = false, length = 10)
    private String isApproved;

    @JoinColumn(name = "logo_img_url_id", foreignKey = @ForeignKey(name = "enterprise_fk_logo_img_url_id"))
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private LogoImgUrl logoImgUrl;

    @JoinColumn(name = "manager_id", foreignKey = @ForeignKey(name = "enterprise_fk_manager_id"))
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Manager manager;

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

    @Builder
    public Enterprise(String email, String password, Role role, String name, String businessNumber, String corporateNumber, String address, String isApproved, LogoImgUrl logoImgUrl, Manager manager, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.name = name;
        this.businessNumber = businessNumber;
        this.corporateNumber = corporateNumber;
        this.address = address;
        this.isApproved = isApproved;
        this.logoImgUrl = logoImgUrl;
        this.manager = manager;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void setLogoImgUrl(LogoImgUrl logoImgUrl) {
        this.logoImgUrl = logoImgUrl;
    }

}
