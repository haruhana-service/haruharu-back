package org.kwakmunsu.haruhana.security.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.kwakmunsu.haruhana.security.TestGuestSecurityContextFactory;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithSecurityContext;

@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.METHOD, ElementType.TYPE })
@WithSecurityContext(factory = TestGuestSecurityContextFactory.class, setupBefore = TestExecutionEvent.TEST_EXECUTION)
public @interface TestGuest {

    long id() default 1L;

    String role() default "ROLE_GUEST";

}