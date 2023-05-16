package com.sau.intern.config;

import com.sau.intern.model.User;
import com.sau.intern.repository.UserRepository;
import com.sau.intern.util.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

@RequiredArgsConstructor
public class Interceptor implements AsyncHandlerInterceptor {

    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        var token = getToken(request);

        var api = getApi(request);

        var restMethod = getRestMethod(request);

        var userId = getUserIdFromToken(token);

        var user = getUser(userId);

        if (isUserHaveAdminRole(user)) {
            return true;
        }

        if (checkUserHavePermissionForRequest(api, restMethod, user))
            return true;
        else throw new RuntimeException();
    }

    private static boolean checkUserHavePermissionForRequest(String api, String restMethod, User user) {
        return user.getRole().getPermissionList()
                .stream().anyMatch(permission -> {
                    var permissionParts = permission.getName().split(":");
                    return permissionParts.length > 1
                            && permissionParts[0].toUpperCase().equals(restMethod)
                            && permissionParts[1].toUpperCase().equals(api);
                });
    }

    private static String getToken(HttpServletRequest request) {
        return request.getHeader(HttpHeaders.AUTHORIZATION);
    }

    private static String getRestMethod(HttpServletRequest request) {
        return request.getMethod().toUpperCase();
    }

    private static String getApi(HttpServletRequest request) {
        return request.getRequestURI().toUpperCase();
    }

    private static boolean isUserHaveAdminRole(User user) {
        return "ADMIN".equals(user.getRole().getName());
    }

    private Long getUserIdFromToken(String token) {
        return jwtUtil.parseToken(token).orElseThrow(() -> new RuntimeException());
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new RuntimeException());
    }
}
