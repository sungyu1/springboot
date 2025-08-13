package jpabook.jpashop.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

public class LoginCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        
        // 로그인 체크 제외할 경로들
        if (isExcludedPath(requestURI)) {
            return true;
        }
        
        // 세션에서 로그인 정보 확인
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loginMember") == null) {
            // 로그인되지 않은 경우 로그인 페이지로 리다이렉트
            response.sendRedirect("/login");
            return false;
        }
        
        return true;
    }
    
    // 로그인 체크에서 제외할 경로들
    private boolean isExcludedPath(String requestURI) {
        return requestURI.equals("/") ||
               requestURI.equals("/login") ||
               requestURI.equals("/members/login") ||
               requestURI.equals("/members/create") ||
               requestURI.equals("/css/**") ||
               requestURI.equals("/js/**") ||
               requestURI.equals("/images/**") ||
               requestURI.equals("/webfonts/**") ||
               requestURI.equals("/favicon.ico") ||
               requestURI.startsWith("/css/") ||
               requestURI.startsWith("/js/") ||
               requestURI.startsWith("/images/") ||
               requestURI.startsWith("/webfonts/");
    }
}
