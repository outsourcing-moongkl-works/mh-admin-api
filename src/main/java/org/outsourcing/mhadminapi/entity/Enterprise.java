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

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "enterprises")
@NoArgsConstructor
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Enterprise implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID id;

    @Column(name = "login_id", nullable = false, length = 50, unique = true)
    private String loginId;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 20)
    private Role role;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "business_number", nullable = false, length = 50)
    private String businessNumber;

    @Column(name = "corporate_number", nullable = false, length = 50)
    private String corporateNumber;

    @Column(name = "country", nullable = false, length = 50)
    private String country;

    @Column(name = "address", nullable = false, length = 50)
    private String address;

    @Column(name = "is_approved", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean isApproved;

    @JoinColumn(name = "logo_img_url_id", foreignKey = @ForeignKey(name = "enterprise_fk_logo_img_url_id"))
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private LogoImgUrl logoImgUrl;

    @Column(name = "manager_name", nullable = false, length = 50)
    private String managerName;

    @Column(name = "manager_phone", nullable = false, length = 50)
    private String managerPhone;

    @Column(name = "manager_email", nullable = false, length = 50)
    private String managerEmail;

    @CreatedDate
    @Column(name = "created_at", length = 20)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", length = 20)
    private LocalDateTime updatedAt;

    @PrePersist
    public void setEnterpriseId() {
        if (id == null) {
            id = UUID.randomUUID();
        }
    }

    @Builder
    public Enterprise(String loginId, String password, Role role, String name, String businessNumber, String corporateNumber, String address, Boolean isApproved, LogoImgUrl logoImgUrl, String managerEmail, String managerName, String managerPhone, LocalDateTime createdAt, LocalDateTime updatedAt, String country) {
        this.loginId = loginId;
        this.password = password;
        this.role = role;
        this.name = name;
        this.businessNumber = businessNumber;
        this.corporateNumber = corporateNumber;
        this.address = address;
        this.managerEmail = managerEmail;
        this.managerName = managerName;
        this.managerPhone = managerPhone;
        this.isApproved = isApproved;
        this.logoImgUrl = logoImgUrl;
        this.country = country;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void updateLogoImgUrl(LogoImgUrl logoImgUrl) {
        this.logoImgUrl = logoImgUrl;
        logoImgUrl.setEnterprise(this);
    }
    public void updateIsApproved(Boolean isApproved) {
        this.isApproved = isApproved;
    }

}
