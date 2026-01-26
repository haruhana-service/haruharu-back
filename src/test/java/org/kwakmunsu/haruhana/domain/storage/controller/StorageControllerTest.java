package org.kwakmunsu.haruhana.domain.storage.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.kwakmunsu.haruhana.ControllerTestSupport;
import org.kwakmunsu.haruhana.domain.storage.controller.dto.PresignedCreateRequest;
import org.kwakmunsu.haruhana.domain.storage.controller.dto.StorageUploadCompleteRequest;
import org.kwakmunsu.haruhana.domain.storage.enums.UploadType;
import org.kwakmunsu.haruhana.infrastructure.s3.dto.PresignedUrlResponse;
import org.kwakmunsu.haruhana.security.annotation.TestMember;
import org.springframework.http.MediaType;

class StorageControllerTest extends ControllerTestSupport {

    @TestMember
    @Test
    void presigned_url_발급_요청을_한다() throws JsonProcessingException {
        // given
        var request = new PresignedCreateRequest("fileName.png", UploadType.PROFILE_IMAGE);
        String requestJson = objectMapper.writeValueAsString(request);

        var presignedUrlResponse = new PresignedUrlResponse("http://presigned-url", "object-key");
        given(storageService.createPresignedUrl(any(), any(), any())).willReturn(presignedUrlResponse);

        // when & then
        assertThat(mvcTester.post().uri("/v1/storage/presigned-url")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .apply(print())
                .hasStatusOk()
                .bodyJson()
                .hasPathSatisfying("$.result", v -> v.assertThat().isEqualTo("SUCCESS"))
                .hasPathSatisfying("$.data.presignedUrl", v -> v.assertThat().isEqualTo("http://presigned-url"))
                .hasPathSatisfying("$.data.objectKey", v -> v.assertThat().isEqualTo("object-key"))
                .hasPathSatisfying("$.error", v -> v.assertThat().isNull());
    }

    @TestMember
    @Test
    void 업로드_완료_요청을_한다() throws JsonProcessingException {
        // given
        var request = new StorageUploadCompleteRequest("object-key-example");
        String requestJson = objectMapper.writeValueAsString(request);

        // when & then
        assertThat(mvcTester.post().uri("/v1/storage/upload-complete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .apply(print())
                .hasStatusOk()
                .bodyJson()
                .hasPathSatisfying("$.result", v -> v.assertThat().isEqualTo("SUCCESS"))
                .hasPathSatisfying("$.data", v -> v.assertThat().isNull())
                .hasPathSatisfying("$.error", v -> v.assertThat().isNull());
    }

}