package jpabook.jpashop.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginCheckInterceptor())
                .addPathPatterns("/**")  // 모든 경로에 적용
                .excludePathPatterns(    // 제외할 경로들
                        "/",
                        "/login",
                        "/members/login", 
                        "/members/create",
                        "/css/**",
                        "/js/**", 
                        "/images/**",
                        "/webfonts/**",
                        "/favicon.ico"
                );
    }
}
