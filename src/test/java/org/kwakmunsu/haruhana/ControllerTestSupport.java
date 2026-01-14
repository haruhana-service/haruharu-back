package org.kwakmunsu.haruhana;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.kwakmunsu.haruhana.domain.auth.controller.AuthController;
import org.kwakmunsu.haruhana.domain.auth.service.AuthService;
import org.kwakmunsu.haruhana.domain.dailyproblem.controller.DailyProblemController;
import org.kwakmunsu.haruhana.domain.dailyproblem.service.DailyProblemService;
import org.kwakmunsu.haruhana.domain.member.controller.MemberController;
import org.kwakmunsu.haruhana.domain.member.service.MemberService;
import org.kwakmunsu.haruhana.domain.submission.service.SubmissionService;
import org.kwakmunsu.haruhana.security.TestSecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
@WebMvcTest(
        controllers = {
                MemberController.class,
                AuthController.class,
                DailyProblemController.class,
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

    @MockitoBean
    protected DailyProblemService dailyProblemService;

    @MockitoBean
    protected SubmissionService submissionService;

}