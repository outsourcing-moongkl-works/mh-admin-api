package org.outsourcing.mhadminapi.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.outsourcing.mhadminapi.dto.MessageDto;
import org.outsourcing.mhadminapi.dto.MoongklWorksInformationDto;
import org.outsourcing.mhadminapi.dto.NotificationDto;
import org.outsourcing.mhadminapi.entity.AboutUs;
import org.outsourcing.mhadminapi.entity.CompanyLocation;
import org.outsourcing.mhadminapi.entity.Notification;
import org.outsourcing.mhadminapi.entity.Terms;
import org.outsourcing.mhadminapi.repository.AboutUsRepository;
import org.outsourcing.mhadminapi.repository.CompanyLocationRepository;
import org.outsourcing.mhadminapi.repository.NotificationRepository;
import org.outsourcing.mhadminapi.repository.TermsRepository;
import org.outsourcing.mhadminapi.sqs.SqsSender;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class MoongklWorksInformationService{
    private final CompanyLocationRepository companyLocationRepository;
    private final NotificationRepository notificationRepository;
    private final TermsRepository termsRepository;
    private final AboutUsRepository aboutUsRepository;
    private final SqsSender sqsSender;
    @Transactional
    public MoongklWorksInformationDto.UpdateTermsResponse updateTerms(MoongklWorksInformationDto.UpdateTermsRequest request) {
        Terms terms = termsRepository.findById(UUID.fromString("4000c0f7-0c97-4bd7-a200-0de1392f1df0"))
                .orElseThrow(() -> new RuntimeException("Terms not found"));

        terms.updateTerms(request.getTerms());
        termsRepository.save(terms);

        MoongklWorksInformationDto.UpdateTermsResponse response = MoongklWorksInformationDto.UpdateTermsResponse.builder()
                .updatedAt(terms.getUpdatedAt())
                .build();

        return response;
    }

    public MoongklWorksInformationDto.GetTermsResponse getTerms() {
        Terms terms = termsRepository.findById(UUID.fromString("4000c0f7-0c97-4bd7-a200-0de1392f1df0"))
                .orElseThrow(() -> new RuntimeException("Terms not found"));

        MoongklWorksInformationDto.GetTermsResponse response = MoongklWorksInformationDto.GetTermsResponse.builder()
                .terms(terms.getTerms())
                .build();

        return response;
    }

    @Transactional
    public MoongklWorksInformationDto.UpdateAboutUsResponse updateAboutUs(MoongklWorksInformationDto.UpdateAboutUsRequest request) {
        AboutUs aboutUs = aboutUsRepository.findById(UUID.fromString("648c4bf4-3c90-492a-bb23-600dae7a4d70"))
                .orElseThrow(() -> new RuntimeException("AboutUs not found"));

        aboutUs.updateAboutUs(request.getAboutUs());
        aboutUsRepository.save(aboutUs);

        MoongklWorksInformationDto.UpdateAboutUsResponse response = MoongklWorksInformationDto.UpdateAboutUsResponse.builder()
                .updatedAt(aboutUs.getUpdatedAt())
                .build();

        return response;
    }

    public MoongklWorksInformationDto.GetAboutUsResponse getAboutUs() {
        AboutUs aboutUs = aboutUsRepository.findById(UUID.fromString("648c4bf4-3c90-492a-bb23-600dae7a4d70"))
                .orElseThrow(() -> new RuntimeException("AboutUs not found"));

        MoongklWorksInformationDto.GetAboutUsResponse response = MoongklWorksInformationDto.GetAboutUsResponse.builder()
                .aboutUs(aboutUs.getAboutUs())
                .build();

        return response;
    }

    @Transactional
    public MoongklWorksInformationDto.UpdateCompanyLocationResponse updateCompanyLocation(MoongklWorksInformationDto.UpdateCompanyLocationRequest request) {
        CompanyLocation companyLocation = companyLocationRepository.findById(UUID.fromString("7ddde530-4d8a-429f-bb19-405f4e74057a"))
                .orElseThrow(() -> new RuntimeException("CompanyLocation not found"));

        companyLocation.updateCompanyLocation(request.getCompanyLocation());
        companyLocationRepository.save(companyLocation);

        MoongklWorksInformationDto.UpdateCompanyLocationResponse response = MoongklWorksInformationDto.UpdateCompanyLocationResponse.builder()
                .updatedAt(companyLocation.getUpdatedAt())
                .build();

        return response;
    }

    public MoongklWorksInformationDto.GetCompanyLocationResponse getCompanyLocation() {
        CompanyLocation companyLocation = companyLocationRepository.findById(UUID.fromString("7ddde530-4d8a-429f-bb19-405f4e74057a"))
                .orElseThrow(() -> new RuntimeException("CompanyLocation not found"));

        MoongklWorksInformationDto.GetCompanyLocationResponse response = MoongklWorksInformationDto.GetCompanyLocationResponse.builder()
                .companyLocation(companyLocation.getCompanyLocation())
                .build();

        return response;
    }

    @Transactional
    public NotificationDto.CreateResponse createNotification(NotificationDto.CreateRequest request, UUID adminId) {
        Notification notification = Notification.builder()
                .adminId(adminId.toString())
                .title(request.getTitle())
                .content(request.getContent())
                .build();

        notificationRepository.save(notification);

        NotificationDto.CreateResponse response = NotificationDto.CreateResponse.builder()
                .notificationId(notification.getId().toString())
                .adminId(adminId.toString())
                .createdAt(notification.getCreatedAt())
                .build();

        Map<String, String> messageMap = new LinkedHashMap<>();
        messageMap.put("id", notification.getId().toString());
        messageMap.put("title", notification.getTitle());
        messageMap.put("content", notification.getContent());

        MessageDto messageDto = sqsSender.createMessageDtoFromRequest("create notification", messageMap);
        sqsSender.sendToSQS(messageDto);

        return response;
    }

    @Transactional
    public NotificationDto.UpdateResponse updateNotification(NotificationDto.UpdateRequest request) {
        Notification notification = notificationRepository.findById(UUID.fromString(request.getNotificationId()))
                .orElseThrow(() -> new RuntimeException("Notification not found: " + request.getNotificationId()));

        notification.updateNotification(request.getTitle(), request.getContent());

        notificationRepository.save(notification);

        NotificationDto.UpdateResponse response = NotificationDto.UpdateResponse.builder()
                .updatedAt(notification.getUpdatedAt())
                .build();

        Map<String, String> messageMap = new LinkedHashMap<>();
        messageMap.put("id", request.getNotificationId());
        messageMap.put("title", request.getTitle());
        messageMap.put("content", request.getContent());

        MessageDto messageDto = sqsSender.createMessageDtoFromRequest("update notification", messageMap);
        sqsSender.sendToSQS(messageDto);

        return response;
    }

    public Page<NotificationDto.GetResponse> getNotification(int page, int size) {
        Sort sortBy = Sort.by(Sort.Direction.DESC, "createdAt");
        final Pageable pageable = PageRequest.of(page, size, sortBy);

        return notificationRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

}
