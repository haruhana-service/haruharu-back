package org.kwakmunsu.haruhana.global.support.image;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.kwakmunsu.haruhana.IntegrationTestSupport;
import org.kwakmunsu.haruhana.domain.storage.enums.FileContentType;
import org.kwakmunsu.haruhana.domain.storage.enums.UploadType;
import org.kwakmunsu.haruhana.infrastructure.s3.dto.PresignedUrlResponse;

@Slf4j
@RequiredArgsConstructor
class StorageProviderIntegrationTest extends IntegrationTestSupport {

    final StorageProvider storageProvider;

    @Test
    void 업로드용_presignedUrl을_생성한다() {
        // when
        var presignedUrlResponse = storageProvider.generatePresignedUploadUrl(UploadType.PROFILE_IMAGE, FileContentType.GIF);

        // then
        assertThat(presignedUrlResponse).extracting(
                PresignedUrlResponse::presignedUrl,
                PresignedUrlResponse::objectKey
        ).doesNotContainNull();

        log.info("presignedUrlResponse={}", presignedUrlResponse.toString());
    }

    @Test
    void 읽기용_presignedUrl을_생성한다() {
        // when
        var uploadPresignedUrlResponse = storageProvider.generatePresignedUploadUrl(UploadType.PROFILE_IMAGE, FileContentType.GIF);

        var readPresignedUrl = storageProvider.generatePresignedReadUrl(uploadPresignedUrlResponse.objectKey());

        // then
        assertThat(readPresignedUrl).isNotNull();

        log.info("readPresignedUrl={}", readPresignedUrl);
    }

}