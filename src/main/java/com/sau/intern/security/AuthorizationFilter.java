package com.sau.intern.security;

import com.sau.intern.repository.UserRepository;
import com.sau.intern.util.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;

public class AuthorizationFilter extends BasicAuthenticationFilter {

    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;

    public AuthorizationFilter(AuthenticationManager authenticationManager, UserRepository userRepository, JWTUtil jwtUtil) {
        super(authenticationManager);
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header == null || !header.startsWith(jwtUtil.getTokenPrefix())) {
            chain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (token == null || !jwtUtil.verifyToken(token))
            return null;
        var userIdOpt = jwtUtil.parseToken(token);
        return userIdOpt.flatMap(userId -> userRepository.findById(userId)
                        .map(user -> new UsernamePasswordAuthenticationToken(
                                user,
                                user.getPassword(),
                                user.getRole().getPermissionList().stream().map(permission -> new SimpleGrantedAuthority(permission.getName())).toList()
                        )))
                .orElse(null);
    }
}
