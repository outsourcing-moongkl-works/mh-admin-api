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
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.UUID;

@Slf4j
@Service
public class EnterpriseStoryService { // 클래스 이름 변경
    private final S3Client amazonS3Client;
    private final EnterpriseRepository enterpriseRepository;

    @Autowired
    public EnterpriseStoryService(S3Client amazonS3Client, EnterpriseRepository enterpriseRepository) {
        this.amazonS3Client = amazonS3Client;
        this.enterpriseRepository = enterpriseRepository;
    }

    @Value("${spring.cloud.aws.s3.bucket}")
    private String BUCKET;
    @Value("${spring.cloud.aws.cloudfront.distribution-domain}")
    private String DISTRIBUTION_DOMAIN;
    @Value("${spring.cloud.aws.s3.root-directory}")
    private String ROOT_DIRECTORY;
    @Value("${spring.cloud.aws.s3.enterprise-story-directory}") // 변수 이름 변경
    private String STORYIMG_DIRECTORY; // 변수 이름 변경

    @Transactional
    public EnterpriseDto.StoryUrl uploadStoryImg(UUID enterpriseId, MultipartFile storyImg) { // 메소드 및 매개변수 이름 변경

        String s3FilePath = enterpriseId + "/" + STORYIMG_DIRECTORY;

        Enterprise enterprise = enterpriseRepository.findById(enterpriseId).orElseThrow(() -> new EnterpriseException(EnterpriseErrorResult.ENTERPRISE_NOT_FOUND));

        EnterpriseDto.StoryUrl storyImgUrlDto; // 변수 타입 변경

        if(storyImg == null || storyImg.isEmpty()) {
            storyImgUrlDto = EnterpriseDto.StoryUrl.builder() // 생성자 변경
                    .cloudfrontUrl("")
                    .s3Url("")
                    .build();

            return storyImgUrlDto;
        }

        String fileExtension = storyImg.getOriginalFilename().substring(storyImg.getOriginalFilename().lastIndexOf("."));

        String key = ROOT_DIRECTORY + "/" + s3FilePath + "/" + UUID.randomUUID() + fileExtension;

        if (storyImg.getSize() > 52428800) {
            throw new EnterpriseException(EnterpriseErrorResult.INVALID_STORYIMG_SIZE); // 예외 메시지 변경
        }

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(BUCKET)
                    .key(key)
                    .build();

            amazonS3Client.putObject(putObjectRequest, RequestBody.fromInputStream(storyImg.getInputStream(), storyImg.getSize()));
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new EnterpriseException(EnterpriseErrorResult.STORYIMG_UPLOAD_FAILED); // 예외 메시지 변경
        }

        // S3 URL 생성 방식 수정
        String s3Url = "https://" + BUCKET + ".s3.amazonaws.com/" + key;
        // CloudFront URL 생성
        String cloudFrontUrl = "https://" + DISTRIBUTION_DOMAIN + "/" + key;

        storyImgUrlDto = EnterpriseDto.StoryUrl.builder() // 생성자 변경
                .s3Url(s3Url)
                .cloudfrontUrl(cloudFrontUrl)
                .build();

        return storyImgUrlDto;
    }

    public void deleteStory(String storyS3Url) throws EnterpriseException {
        // S3 URL에서 필요한 키 부분만 추출하는 방법을 보정
        String key = extractKeyForStoryFromUrl(storyS3Url);

        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(BUCKET)
                    .key(key)
                    .build();

            amazonS3Client.deleteObject(deleteObjectRequest);
            log.info("Successfully deleted specific story image: {}", key);
        } catch (Exception e) {
            log.error("Failed to delete specific story image: {}", e.getMessage());
            throw new EnterpriseException(EnterpriseErrorResult.STORYIMG_DELETE_FAILED);
        }
    }

    private String extractKeyForStoryFromUrl(String url) {
        // 예상되는 URL 형식: "https://s3.amazonaws.com/{bucket}/{root-directory}/{enterprise-story-directory}/file.jpg"
        // 필요한 부분: "{root-directory}/{enterprise-story-directory}/file.jpg"
        String pathWithoutBucket = url.substring(url.indexOf(BUCKET) + BUCKET.length() + 1);
        String key = pathWithoutBucket.startsWith("/") ? pathWithoutBucket.substring(1) : pathWithoutBucket;

        // 여기서 추가적인 검증을 할 수 있음. 예를 들어, key가 enterprise-story-directory로 시작하는지 확인 등
        if (!key.startsWith(ROOT_DIRECTORY + "/" + "enterprise-story")) {
            throw new IllegalArgumentException("The provided URL does not point to a story image within the expected directory.");
        }

        return key;
    }


}
