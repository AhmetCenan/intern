package com.sau.intern.config;

import com.sau.intern.repository.UserRepository;
import com.sau.intern.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
@Configuration
@RequiredArgsConstructor
public class InterceptorConfig implements WebMvcConfigurer {

    @Value("${spring.security.whiteList}")
    private String[] authWhiteList;

    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new Interceptor(userRepository, jwtUtil)).excludePathPatterns(authWhiteList);
    }
}
