package org.kwakmunsu.haruhana.domain.storage.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.kwakmunsu.haruhana.UnitTestSupport;
import org.kwakmunsu.haruhana.domain.member.MemberFixture;
import org.kwakmunsu.haruhana.domain.member.enums.Role;
import org.kwakmunsu.haruhana.domain.storage.entity.Storage;
import org.kwakmunsu.haruhana.domain.storage.enums.UploadType;
import org.kwakmunsu.haruhana.domain.storage.repository.StorageJpaRepository;
import org.kwakmunsu.haruhana.global.support.error.ErrorType;
import org.kwakmunsu.haruhana.global.support.error.HaruHanaException;
import org.kwakmunsu.haruhana.global.support.image.StorageProvider;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class StorageManagerUnitTest extends UnitTestSupport {

    @Mock
    StorageReader storageReader;

    @Mock
    StorageProvider storageProvider;

    @Mock
    StorageJpaRepository storageJpaRepository;

    @InjectMocks
    StorageManager storageManager;

    private static final String OLD_OBJECT_KEY = "old-object-key-example";
    private static final String NEW_OBJECT_KEY = "new-object-key-example";

    @Test
    void 정상_업로드_확인() {
        // given
        var member = MemberFixture.createMember(Role.ROLE_MEMBER);
        var issue = Storage.issue(member.getId(), UploadType.PROFILE_IMAGE, OLD_OBJECT_KEY);

        given(storageReader.findByMemberIdAndObjectKey(any(), any())).willReturn(issue);

        // when
        storageManager.completeUpload(OLD_OBJECT_KEY, member);

        // then
        assertThat(member.getProfileImageObjectKey()).isEqualTo(OLD_OBJECT_KEY);
        assertThat(issue.isComplete()).isTrue();

        verify(storageProvider, times(1)).ensureObjectExists(any());
    }

    @Test
    void 기존에_OBJECT_KEY가_존재하고_새로운_OBJECT_KEY로_업로드_후_완료_요청_시_OBJECT_KEY가_변경된다() {
        // given
        var member = MemberFixture.createMember(Role.ROLE_MEMBER);
        var issue = Storage.issue(member.getId(), UploadType.PROFILE_IMAGE, OLD_OBJECT_KEY);

        given(storageReader.findByMemberIdAndObjectKey(any(), any())).willReturn(issue);

        // when
        storageManager.completeUpload(NEW_OBJECT_KEY, member);

        // then
        assertThat(member.getProfileImageObjectKey()).isEqualTo(NEW_OBJECT_KEY);
        assertThat(issue.isComplete()).isTrue();

        verify(storageProvider, times(1)).ensureObjectExists(any());
    }

    @Test
    void 이미_완료된_업로드_라면_아무_일도_발생하지_않는다() {
        // given
        var member = MemberFixture.createMember(Role.ROLE_MEMBER);
        var issue = Storage.issue(member.getId(), UploadType.PROFILE_IMAGE, OLD_OBJECT_KEY);

        issue.complete(member.getId());

        given(storageReader.findByMemberIdAndObjectKey(any(), any())).willReturn(issue);

        // when
        storageManager.completeUpload(OLD_OBJECT_KEY, member);

        // then
        verify(storageProvider, never()).ensureObjectExists(any());
    }

    @Test
    void 다른_사람이_발급한_Object_key_라면_예외를_던진다() {
        // given
        var member = MemberFixture.createMember(Role.ROLE_MEMBER);

        given(storageReader.findByMemberIdAndObjectKey(any(), any()))
                .willThrow(new HaruHanaException(ErrorType.STORAGE_ISSUE_NOT_FOUND));

        // when
        assertThatThrownBy(() -> storageManager.completeUpload(NEW_OBJECT_KEY, member))
                .isInstanceOf(HaruHanaException.class)
                .hasMessage(ErrorType.STORAGE_ISSUE_NOT_FOUND.getMessage());

        // then
        verify(storageProvider, never()).ensureObjectExists(any());
    }

}