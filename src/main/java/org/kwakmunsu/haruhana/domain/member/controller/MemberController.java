package org.kwakmunsu.haruhana.domain.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.kwakmunsu.haruhana.domain.member.controller.dto.MemberCreateRequest;
import org.kwakmunsu.haruhana.domain.member.service.MemberService;
import org.kwakmunsu.haruhana.global.support.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class MemberController extends MemberDocsController {

    private final MemberService memberService;

    @Override
    @PostMapping("/v1/sign-up")
    public ResponseEntity<ApiResponse<Long>> create(@RequestBody @Valid MemberCreateRequest request) {
        Long memberId = memberService.createMember(request.toNewProfile());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(memberId));
    }

}