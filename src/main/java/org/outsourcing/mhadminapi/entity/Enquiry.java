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
@Table(name = "enquiries")
@NoArgsConstructor
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Enquiry { //문의사항
    @Id
    @Column(name = "id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID id;

    @Column(name = "email", nullable = false, length = 20)
    private String email;

    @Column(name = "title", nullable = false, length = 50)
    private String title;

    @Column(name = "content", nullable = false, length = 10000)
    private String content;

    @CreatedDate
    @Column(name = "created_at", length = 20)
    private LocalDateTime createdAt;

    @Column(name = "is_replied", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean isReplied;

    @PrePersist
    public void setEnquiryId() {
        if (id == null) {
            id = UUID.randomUUID();
        }
    }
    @Builder
    public Enquiry(String email, String title, String content, LocalDateTime createdAt, Boolean isReplied) {
        this.email = email;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.isReplied = isReplied;
    }

    public void updateReplyStatus() {
        this.isReplied = true;
    }
}
