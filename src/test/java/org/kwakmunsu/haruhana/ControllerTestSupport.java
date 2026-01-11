package org.kwakmunsu.haruhana;

import org.kwakmunsu.haruhana.domain.auth.controller.AuthController;
import org.kwakmunsu.haruhana.domain.auth.service.AuthService;
import org.kwakmunsu.haruhana.domain.member.controller.MemberController;
import org.kwakmunsu.haruhana.domain.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import tools.jackson.databind.ObjectMapper;

@ActiveProfiles("test")
@WebMvcTest(
        controllers = {
                MemberController.class,
                AuthController.class
        })
public abstract class ControllerTestSupport {

    @Autowired
    protected MockMvcTester mvcTester;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockitoBean
    protected MemberService memberService;

    @MockitoBean
    protected AuthService authService;

}