package org.kwakmunsu.haruhana.domain.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.kwakmunsu.haruhana.domain.member.controller.dto.MemberCreateRequest;
import org.kwakmunsu.haruhana.domain.member.controller.dto.PreferenceRegisterRequest;
import org.kwakmunsu.haruhana.domain.member.service.MemberProfileResponse;
import org.kwakmunsu.haruhana.domain.member.service.MemberService;
import org.kwakmunsu.haruhana.global.annotation.LoginMember;
import org.kwakmunsu.haruhana.global.support.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class MemberController extends MemberDocsController {

    private final MemberService memberService;

    @Override
    @PostMapping("/v1/members/sign-up")
    public ResponseEntity<ApiResponse<Long>> create(@RequestBody @Valid MemberCreateRequest request) {
        Long memberId = memberService.createMember(request.toNewProfile());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(memberId));
    }

    @Override
    @PostMapping("/v1/members/preferences")
    public ResponseEntity<ApiResponse<?>> registerPreference(
            @RequestBody @Valid PreferenceRegisterRequest request,
            @LoginMember Long memberId
    ) {
        memberService.registerPreference(request.toNewPreference(), memberId);

        return ResponseEntity.ok(ApiResponse.success());
    }

    @Override
    @GetMapping("/v1/members")
    public ResponseEntity<ApiResponse<MemberProfileResponse>> getProfile(@LoginMember Long memberId) {
        MemberProfileResponse response = memberService.getProfile(memberId);

        return  ResponseEntity.ok(ApiResponse.success(response));
    }

}