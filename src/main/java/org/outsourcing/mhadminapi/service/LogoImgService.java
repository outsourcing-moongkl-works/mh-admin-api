package org.outsourcing.mhadminapi.service;

import lombok.extern.slf4j.Slf4j;
import org.outsourcing.mhadminapi.dto.EnterpriseDto;
import org.outsourcing.mhadminapi.entity.Enterprise;
import org.outsourcing.mhadminapi.exception.EnterpriseErrorResult;
import org.outsourcing.mhadminapi.exception.EnterpriseException;
import org.outsourcing.mhadminapi.repository.EnterpriseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.UUID;


@Slf4j
@Service
public class LogoImgService{
    private final S3Client amazonS3Client;
    private final EnterpriseRepository enterpriseRepository;
    @Autowired
    public LogoImgService(S3Client amazonS3Client, EnterpriseRepository enterpriseRepository) {
        this.amazonS3Client = amazonS3Client;
        this.enterpriseRepository = enterpriseRepository;
    }

    @Value("${spring.cloud.aws.s3.bucket}")
    private String BUCKET;
    @Value("${spring.cloud.aws.cloudfront.distribution-domain}")
    private String DISTRIBUTION_DOMAIN;
    @Value("${spring.cloud.aws.s3.root-directory}")
    private String ROOT_DIRECTORY;
    @Value("${spring.cloud.aws.s3.enterprise-logo-directory}")
    private String LOGOIMG_DIRECTORY;

    @Transactional
    public EnterpriseDto.LogoImgUrl uploadLogoImg(UUID enterpriseId, MultipartFile logoImg) {

        String s3FilePath = enterpriseId + "/" + LOGOIMG_DIRECTORY;

        Enterprise enterprise = enterpriseRepository.findById(enterpriseId).orElseThrow(() -> new EnterpriseException(EnterpriseErrorResult.ENTERPRISE_NOT_FOUND));

        EnterpriseDto.LogoImgUrl logoImgUrlDto;

        if(logoImg == null || logoImg.isEmpty()) {
            logoImgUrlDto = EnterpriseDto.LogoImgUrl.builder()
                    .cloudfrontUrl("")
                    .s3Url("")
                    .build();

            return logoImgUrlDto;
        }


        String fileExtension = logoImg.getOriginalFilename().substring(logoImg.getOriginalFilename().lastIndexOf("."));

        String key = ROOT_DIRECTORY + "/" + s3FilePath + "/" + UUID.randomUUID() + fileExtension;

        if (logoImg.getSize() > 52428800) {
            throw new EnterpriseException(EnterpriseErrorResult.INVALID_LOGOIMG_SIZE);
        }

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(BUCKET)
                    .key(key)
                    .build();

            amazonS3Client.putObject(putObjectRequest, RequestBody.fromInputStream(logoImg.getInputStream(), logoImg.getSize()));
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new EnterpriseException(EnterpriseErrorResult.LOGOIMG_UPLOAD_FAILED);
        }

        // S3 URL 생성 방식 수정
        String s3Url = "https://" + BUCKET + ".s3.amazonaws.com/" + key;
        // CloudFront URL 생성
        String cloudFrontUrl = "https://" + DISTRIBUTION_DOMAIN + "/" + key;

        logoImgUrlDto = EnterpriseDto.LogoImgUrl.builder()
                .s3Url(s3Url)
                .cloudfrontUrl(cloudFrontUrl)
                .build();

        return logoImgUrlDto;
    }

}
