package org.outsourcing.mhadminapi.repository;

import org.outsourcing.mhadminapi.dto.EnquiryDto;
import org.outsourcing.mhadminapi.entity.Enquiry;
import org.outsourcing.mhadminapi.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface EnquiryRepository extends JpaRepository<Enquiry, UUID>, PagingAndSortingRepository<Enquiry, UUID> {

    @Query("SELECT new org.outsourcing.mhadminapi.dto.EnquiryDto$GetResponse " +
            "(e.id, e.email, e.title, e.content, e.createdAt) " +
            "FROM Enquiry e ORDER BY e.createdAt DESC")
    Page<EnquiryDto.GetResponse> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
