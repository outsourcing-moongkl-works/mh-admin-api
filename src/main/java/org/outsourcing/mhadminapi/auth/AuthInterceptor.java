package org.outsourcing.mhadminapi.auth;

import jakarta.security.auth.message.AuthException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.outsourcing.mhadminapi.dto.SessionAdmin;
import org.outsourcing.mhadminapi.repository.AdminRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Base64;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final AdminRepository adminRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //어노테이션 체크 - Controller에 @Auth 어노테이션이 있는지 확인

        boolean hasAnnotation = checkAnnotation(handler, Auth.class);

        if (hasAnnotation) {

            //어노테이션이 있으면서, User의 정보가 맞다면 true 반환
            //request에서 session 받아오기
            HttpSession session = request.getSession();
            SessionAdmin sessionAdmin = (SessionAdmin) session.getAttribute("sessionMember");//sessionMember객체로 저장된 객체 반환
            String adminId = sessionAdmin.getAdminId();
            String password = sessionAdmin.getPassword();

            log.info("id, pw : {}, {}", adminId, password);

            if (adminId != null && password != null && adminRepository.existsByAdminIdAndPassword(adminId, password)) {
                return true;
            }

            throw new AuthException();
        }

        //Auth를 실패하더라도 Controller를 실행하기 위해서는 true로 설정해야한다. ex)/session/add의 경우 walter/20 이 아닌 다른 값이 들어가도 실행되어야 한다.
        return true;
    }


    private boolean checkAnnotation(Object handler, Class<Auth> authClass) {
        HandlerMethod handlerMethod = (HandlerMethod) handler;

        //Auth anntotation이 있는 경우
        if (null != handlerMethod.getMethodAnnotation(authClass) || null != handlerMethod.getBeanType().getAnnotation(authClass)) {
            return true;
        }

        //annotation이 없는 경우
        return false;
    }

}