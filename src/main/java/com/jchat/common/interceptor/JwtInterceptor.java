package com.jchat.common.interceptor;

import com.jchat.auth.dto.UserInfoDto;
import com.jchat.common.annotation.NoAuth;
import com.jchat.common.context.UserContext;
import com.jchat.common.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;  // OPTIONS 요청은 무조건 통과
        }

        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;

        // @NoAuth 있으면 토큰 검증 생략
        if (handlerMethod.hasMethodAnnotation(NoAuth.class)) {
            return true;
        }

        // 쿠키에서 accessToken 추출
        String token = extractTokenFromCookie(request, "accessToken");

        if (token == null || !jwtUtil.validateToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"Unauthorized\"}");
            return false;
        }

        // 토큰에서 사용자 정보 추출해서 request에 저장
        UserInfoDto userInfo = jwtUtil.getUserInfoFromToken(token);

        // UserContext 셋
        UserContext.setUserInfo(userInfo);

        request.setAttribute("userInfo", userInfo);
        request.setAttribute("userNo", userInfo.getUserNo());
        request.setAttribute("id", userInfo.getId());
        request.setAttribute("name", userInfo.getName());
        request.setAttribute("birth", userInfo.getBirth());

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) throws Exception {
        // 요청 완료 후 ThreadLocal 정리 (메모리 누수 방지!)
        UserContext.clear();

        if (ex != null) {
            log.error("Request processing error", ex);
        }
    }

    private String extractTokenFromCookie(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookieName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}