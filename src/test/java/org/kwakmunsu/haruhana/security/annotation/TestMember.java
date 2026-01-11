package org.kwakmunsu.haruhana.security.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.kwakmunsu.haruhana.security.TestMemberSecurityContextFactory;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithSecurityContext;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = TestMemberSecurityContextFactory.class, setupBefore = TestExecutionEvent.TEST_EXECUTION)
public @interface TestMember {

    long id() default 1L;

    String role() default "MEMBER";

}