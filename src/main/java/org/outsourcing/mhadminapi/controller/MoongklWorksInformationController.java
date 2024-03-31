package org.outsourcing.mhadminapi.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.outsourcing.mhadminapi.auth.UserPrincipal;
import org.outsourcing.mhadminapi.dto.MessageDto;
import org.outsourcing.mhadminapi.dto.MoongklWorksInformationDto;
import org.outsourcing.mhadminapi.dto.NotificationDto;
import org.outsourcing.mhadminapi.service.MoongklWorksInformationService;
import org.outsourcing.mhadminapi.sqs.SqsSender;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/moongkl-works-information")
public class MoongklWorksInformationController {

    private final SqsSender sqsSender;
    private final MoongklWorksInformationService moongklWorksInformationService;

    @PreAuthorize("hasAuthority('MASTER')")
    @PutMapping("/terms")
    public ResponseEntity<MoongklWorksInformationDto.UpdateTermsResponse> updateTerms(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestBody MoongklWorksInformationDto.UpdateTermsRequest request) {

        log.info(userPrincipal.getAdmin().getEmail() + " update terms");

        Map<String, String> message = Map.of("terms", request.getTerms());

        MessageDto messageDto = MessageDto.builder()
                .from("mh-admin-api")
                .topic("update terms")
                .message(message)
                .build();
        //추후 글자수 제한 추가
        sqsSender.sendToSQS(messageDto);

        MoongklWorksInformationDto.UpdateTermsResponse response = moongklWorksInformationService.updateTerms(request);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/terms")
    public ResponseEntity<MoongklWorksInformationDto.GetTermsResponse> getTerms() {

        MoongklWorksInformationDto.GetTermsResponse response = moongklWorksInformationService.getTerms();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("hasAuthority('MASTER')")
    @PutMapping("/about-us")
    public ResponseEntity<MoongklWorksInformationDto.UpdateAboutUsResponse> updateAboutUs(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestBody MoongklWorksInformationDto.UpdateAboutUsRequest request) {

        log.info(userPrincipal.getAdmin().getEmail() + " update about us");

        Map<String, String> message = Map.of("aboutUs", request.getAboutUs());

        MessageDto messageDto = MessageDto.builder()
                .from("mh-admin-api")
                .topic("update about us")
                .message(message)
                .build();

        sqsSender.sendToSQS(messageDto);

        MoongklWorksInformationDto.UpdateAboutUsResponse response = moongklWorksInformationService.updateAboutUs(request);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/about-us")
    public ResponseEntity<MoongklWorksInformationDto.GetAboutUsResponse> getAboutUs() {

        MoongklWorksInformationDto.GetAboutUsResponse response = moongklWorksInformationService.getAboutUs();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("hasAuthority('MASTER')")
    @PutMapping("/company-location")
    public ResponseEntity<MoongklWorksInformationDto.UpdateCompanyLocationResponse> updateCompanyLocation(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestBody MoongklWorksInformationDto.UpdateCompanyLocationRequest request) {

        log.info(userPrincipal.getAdmin().getEmail() + " update company location");

        Map<String, String> message = Map.of("companyLocation", request.getCompanyLocation());

        MessageDto messageDto = MessageDto.builder()
                .from("mh-admin-api")
                .topic("update company location")
                .message(message)
                .build();

        sqsSender.sendToSQS(messageDto);

        MoongklWorksInformationDto.UpdateCompanyLocationResponse response = moongklWorksInformationService.updateCompanyLocation(request);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/company-location")
    public ResponseEntity<MoongklWorksInformationDto.GetCompanyLocationResponse> getCompanyLocation() {

        MoongklWorksInformationDto.GetCompanyLocationResponse response = moongklWorksInformationService.getCompanyLocation();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("hasAuthority('MASTER')")
    @PostMapping("/notification")
    public ResponseEntity<NotificationDto.CreateResponse> createNotification(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestBody NotificationDto.CreateRequest request) {

        log.info(userPrincipal.getAdmin().getEmail() + " create notification");

        NotificationDto.CreateResponse response = moongklWorksInformationService.createNotification(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasAuthority('MASTER')")
    @PutMapping("/notification")
    public ResponseEntity<NotificationDto.UpdateResponse> updateNotification(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestBody NotificationDto.UpdateRequest request) {

        log.info(userPrincipal.getAdmin().getEmail() + " update notification");

        NotificationDto.UpdateResponse response = moongklWorksInformationService.updateNotification(request);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/notification")
    public ResponseEntity<Page<NotificationDto.GetResponse>> getNotification(@RequestParam int page, @RequestParam int size) {

        Page<NotificationDto.GetResponse> response = moongklWorksInformationService.getNotification(page, size);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
