package com.pilpil.common.config;

import com.pilpil.common.Interceptor.LoginInterceptor;
import com.pilpil.common.Interceptor.RefreshTokenIntercreptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class MvcConfig implements WebMvcConfigurer {
    private final StringRedisTemplate redisTemplate;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .excludePathPatterns(
                        "/user",
                        "/user/code",
                        "/user/login",
                        "/doc.html",
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/webjars/**",
                        "/category",
                        "/admin/user/login",
                        "/video",
                        "/danmu/list",
                        "/video/record/play",
                        "/comment",
                        "/coin"
                ).order(1);
        registry.addInterceptor(new RefreshTokenIntercreptor(redisTemplate)).order(0);
    }
}
