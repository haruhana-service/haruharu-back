package org.kwakmunsu.haruhana.infrastructure.s3;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kwakmunsu.haruhana.domain.storage.enums.FileContentType;
import org.kwakmunsu.haruhana.domain.storage.enums.UploadType;
import org.kwakmunsu.haruhana.global.support.error.ErrorType;
import org.kwakmunsu.haruhana.global.support.error.HaruHanaException;
import org.kwakmunsu.haruhana.global.support.image.StorageProvider;
import org.kwakmunsu.haruhana.infrastructure.s3.dto.PresignedUrlResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Slf4j
@RequiredArgsConstructor
@Component
public class S3Provider implements StorageProvider {

    private static final int DEFAULT_UPLOAD_EXPIRATION_MINUTES = 3;

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Override
    public PresignedUrlResponse generatePresignedUploadUrl(
            UploadType uploadType,
            FileContentType fileContentType
    ) {
        try {
            String key = generateS3Key(uploadType, fileContentType);

            // Presigned 요청 구성
            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(DEFAULT_UPLOAD_EXPIRATION_MINUTES))
                    .putObjectRequest(p -> {
                        p.bucket(bucket);
                        p.key(key);
                        p.contentType(fileContentType.getMimeType());
                    })
                    .build();

            PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);
            String presignedUrl = presignedRequest.url().toString();

            log.info("[S3Provider] Upload Presigned URL 생성 완료 - objectKey: {}", key);

            return PresignedUrlResponse.builder()
                    .presignedUrl(presignedUrl)
                    .objectKey(key)
                    .build();

        } catch (Exception e) {
            log.error("[S3Provider] Upload Presigned URL 생성 실패", e);
            throw new HaruHanaException(ErrorType.S3_PRESIGNED_URL_ERROR);
        }
    }

    @Override
    public void ensureObjectExists(String objectKey) {
        try {
            s3Client.headObject(r -> r
                    .bucket(bucket)
                    .key(objectKey)
            );
        } catch (Exception e) {
            throw new HaruHanaException(ErrorType.NOT_FOUND_FILE);
        }
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void deleteObjectAsync(String oldKey) {
        try {
            s3Client.deleteObject(r -> r
                    .bucket(bucket)
                    .key(oldKey)
            );
        } catch (Exception e) {
            log.error("[S3Provider] S3 객체 삭제 실패 - objectKey: {}", oldKey, e);
        }
    }

    /**
     * S3 objectKey 생성: uploads/{yyyy-mm-dd}/{uploadType}/{UUID}.{extension}
     */
    private String generateS3Key(UploadType uploadType, FileContentType fileContentType) {
        String uuid = UUID.randomUUID().toString().substring(0, 16);
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

        return "uploads/%s/%s/%s.%s".formatted(
                today,
                uploadType.name().toLowerCase(),
                uuid,
                fileContentType.getExtension()
        );
    }

}