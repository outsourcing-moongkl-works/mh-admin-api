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
import java.util.Optional;
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
        //find Terms by '4000c0f7-0c97-4bd7-a200-0de1392f1df0'
        Optional<Terms> terms = termsRepository.findById(UUID.fromString("4000c0f7-0c97-4bd7-a200-0de1392f1df0"));

        if (terms.isPresent()) {
            terms.get().updateTerms(request.getTerms());
            termsRepository.save(terms.get());
        }

        MoongklWorksInformationDto.UpdateTermsResponse response = MoongklWorksInformationDto.UpdateTermsResponse.builder()
                .updatedAt(terms.get().getUpdatedAt())
                .build();

        return response;
    }

    public MoongklWorksInformationDto.GetTermsResponse getTerms() {
        //find Terms by '4000c0f7-0c97-4bd7-a200-0de1392f1df0'
        Optional<Terms> terms = termsRepository.findById(UUID.fromString("4000c0f7-0c97-4bd7-a200-0de1392f1df0"));

        MoongklWorksInformationDto.GetTermsResponse response = MoongklWorksInformationDto.GetTermsResponse.builder()
                .terms(terms.get().getTerms())
                .build();

        return response;
    }

    @Transactional
    public MoongklWorksInformationDto.UpdateAboutUsResponse updateAboutUs(MoongklWorksInformationDto.UpdateAboutUsRequest request) {
        //find AboutUs by '648c4bf4-3c90-492a-bb23-600dae7a4d70'
        Optional<AboutUs> aboutUs = aboutUsRepository.findById(UUID.fromString("648c4bf4-3c90-492a-bb23-600dae7a4d70"));

        aboutUs.get().updateAboutUs(request.getAboutUs());
        aboutUsRepository.save(aboutUs.get());

        MoongklWorksInformationDto.UpdateAboutUsResponse response = MoongklWorksInformationDto.UpdateAboutUsResponse.builder()
                .updatedAt(aboutUs.get().getUpdatedAt())
                .build();

        return response;
    }

    public MoongklWorksInformationDto.GetAboutUsResponse getAboutUs() {
        //find AboutUs by '648c4bf4-3c90-492a-bb23-600dae7a4d70'
        Optional<AboutUs> aboutUs = aboutUsRepository.findById(UUID.fromString("648c4bf4-3c90-492a-bb23-600dae7a4d70"));

        MoongklWorksInformationDto.GetAboutUsResponse response = MoongklWorksInformationDto.GetAboutUsResponse.builder()
                .aboutUs(aboutUs.get().getAboutUs())
                .build();

        return response;
    }

    @Transactional
    public MoongklWorksInformationDto.UpdateCompanyLocationResponse updateCompanyLocation(MoongklWorksInformationDto.UpdateCompanyLocationRequest request) {
        //find CompanyLocation by '7ddde530-4d8a-429f-bb19-405f4e74057a'
        Optional<CompanyLocation> companyLocation = companyLocationRepository.findById(UUID.fromString("7ddde530-4d8a-429f-bb19-405f4e74057a"));

        companyLocation.get().updateCompanyLocation(request.getCompanyLocation());
        companyLocationRepository.save(companyLocation.get());

        MoongklWorksInformationDto.UpdateCompanyLocationResponse response = MoongklWorksInformationDto.UpdateCompanyLocationResponse.builder()
                .updatedAt(companyLocation.get().getUpdatedAt())
                .build();

        return response;
    }

    public MoongklWorksInformationDto.GetCompanyLocationResponse getCompanyLocation() {
        //find CompanyLocation by '7ddde530-4d8a-429f-bb19-405f4e74057a'
        Optional<CompanyLocation> companyLocation = companyLocationRepository.findById(UUID.fromString("7ddde530-4d8a-429f-bb19-405f4e74057a"));

        MoongklWorksInformationDto.GetCompanyLocationResponse response = MoongklWorksInformationDto.GetCompanyLocationResponse.builder()
                .companyLocation(companyLocation.get().getCompanyLocation())
                .build();

        return response;
    }

    @Transactional
    public NotificationDto.CreateResponse createNotification(NotificationDto.CreateRequest request) {
        Notification notification = Notification.builder()
                .adminId(UUID.fromString(request.getAdminId()))
                .title(request.getTitle())
                .content(request.getContent())
                .build();

        notificationRepository.save(notification);

        NotificationDto.CreateResponse response = NotificationDto.CreateResponse.builder()
                .createdAt(notification.getCreatedAt())
                .build();

        Map<String, String> messageMap = new LinkedHashMap<>();
        messageMap.put("notificationId", notification.getId().toString());
        messageMap.put("title", notification.getTitle());
        messageMap.put("content", notification.getContent());

        MessageDto messageDto = sqsSender.createMessageDtoFromRequest("create notification", messageMap);
        sqsSender.sendToSQS(messageDto);

        return response;
    }

    @Transactional
    public NotificationDto.UpdateResponse updateNotification(NotificationDto.UpdateRequest request) {

        //Notification notification = notificationRepository.findById(request.getNotificationId());
        //update Notification
        Optional<Notification> notification = notificationRepository.findById(UUID.fromString(request.getNotificationId()));

        notification.get().updateNotification(request.getTitle(), request.getContent());

        notificationRepository.save(notification.get());

        NotificationDto.UpdateResponse response = NotificationDto.UpdateResponse.builder()
                .updatedAt(notification.get().getUpdatedAt())
                .build();

        Map<String, String> messageMap = new LinkedHashMap<>();
        messageMap.put("notificationId", request.getNotificationId().toString());
        messageMap.put("title", request.getTitle());
        messageMap.put("content", request.getContent());

        MessageDto messageDto = sqsSender.createMessageDtoFromRequest("update notification", messageMap);
        sqsSender.sendToSQS(messageDto);

        return response;
    }

    public Page<NotificationDto.GetResponse> getNotification(int page, int size) {
        Sort sortBy = Sort.by(Sort.Direction.DESC, "createdAt");
        final Pageable pageable = PageRequest.of(page, size, sortBy);

        Page<NotificationDto.GetResponse> response = notificationRepository.findAllByOrderByCreatedAtDesc(pageable).map(notification -> NotificationDto.GetResponse.builder()
                .adminId(notification.getAdminId())
                .notificationId(notification.getNotificationId())
                .title(notification.getTitle())
                .content(notification.getContent())
                .createdAt(notification.getCreatedAt())
                .updatedAt(notification.getUpdatedAt())
                .build());

        return response;
    }

}
