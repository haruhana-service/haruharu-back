package org.kwakmunsu.haruhana.global.annotation.resolver;


import org.kwakmunsu.haruhana.global.annotation.LoginMember;
import org.kwakmunsu.haruhana.global.support.error.ErrorType;
import org.kwakmunsu.haruhana.global.support.error.HaruHanaException;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoginMember.class) &&
                parameter.getParameterType().equals(Long.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            throw new HaruHanaException(ErrorType.EMPTY_SECURITY_CONTEXT);
        }

        return Long.parseLong(authentication.getName());
    }

}