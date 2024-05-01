package org.outsourcing.mhadminapi.repository;

import org.outsourcing.mhadminapi.dto.EnquiryDto;
import org.outsourcing.mhadminapi.entity.Enquiry;
import org.outsourcing.mhadminapi.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface EnquiryRepository extends JpaRepository<Enquiry, UUID>, PagingAndSortingRepository<Enquiry, UUID> {

    @Query("SELECT new org.outsourcing.mhadminapi.dto.EnquiryDto$ReadResponse " +
            "(e.id, e.email, e.title, e.content, e.createdAt, e.isReplied) " +
            "FROM Enquiry e ORDER BY e.createdAt DESC")
    Page<EnquiryDto.ReadResponse> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
