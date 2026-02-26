package org.kwakmunsu.haruhana.admin.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.kwakmunsu.haruhana.admin.member.controller.dto.MemberUpdateRoleRequest;
import org.kwakmunsu.haruhana.admin.member.enums.SortBy;
import org.kwakmunsu.haruhana.admin.member.service.AdminMemberService;
import org.kwakmunsu.haruhana.admin.member.service.dto.AdminMemberPreferenceResponse;
import org.kwakmunsu.haruhana.admin.member.service.dto.AdminMemberPreviewResponse;
import org.kwakmunsu.haruhana.global.support.OffsetLimit;
import org.kwakmunsu.haruhana.global.support.response.ApiResponse;
import org.kwakmunsu.haruhana.global.support.response.PageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AdminMemberController extends AdminMemberDocsController {

    private final AdminMemberService adminMemberService;

    @Override
    @GetMapping("/v1/admin/members")
    public ResponseEntity<ApiResponse<PageResponse<AdminMemberPreviewResponse>>> findMembers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) SortBy sortBy,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        PageResponse<AdminMemberPreviewResponse> response = adminMemberService.findMembers(
                search,
                sortBy,
                new OffsetLimit(page, size)
        );

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Override
    @GetMapping("/v1/admin/members/{memberId}/preferences")
    public ResponseEntity<ApiResponse<AdminMemberPreferenceResponse>> findMemberPreference(@PathVariable Long memberId) {
        AdminMemberPreferenceResponse response = adminMemberService.findMemberPreference(memberId);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Override
    @PatchMapping("/v1/admin/members/{memberId}/roles")
    public ResponseEntity<ApiResponse<?>> updateMemberRole(
            @PathVariable Long memberId,
            @Valid @RequestBody MemberUpdateRoleRequest request
    ) {
        adminMemberService.updateRole(memberId,request.role());

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.success());
    }

}