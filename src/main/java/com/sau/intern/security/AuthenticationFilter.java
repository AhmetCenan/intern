package com.sau.intern.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sau.intern.dto.LoginDto;
import com.sau.intern.model.User;
import com.sau.intern.repository.UserRepository;
import com.sau.intern.util.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.ArrayList;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final UserRepository userRepository;

    private final JWTUtil jwtUtil;

    public AuthenticationFilter(AuthenticationManager authenticationManager, UserRepository userRepository, JWTUtil jwtUtil) {
        super(authenticationManager);
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            LoginDto loginDto = new ObjectMapper().readValue(request.getInputStream(), LoginDto.class);
            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword(), new ArrayList<>())
            );
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException {
        User user = userRepository.findByEmail(((org.springframework.security.core.userdetails.User) authResult.getPrincipal()).getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("username not found"));

        String token = jwtUtil.createToken(user);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", user.getId());
        jsonObject.put("name", user.getName() + " " + user.getSurName());
        jsonObject.put("email", user.getEmail());
        jsonObject.put("role", user.getRole().getId());
        jsonObject.put("token", token);
        String res = jsonObject.toString();

        response.addHeader(HttpHeaders.AUTHORIZATION, token);
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(res);
        response.getWriter().flush();
        response.getWriter().close();
    }
}
