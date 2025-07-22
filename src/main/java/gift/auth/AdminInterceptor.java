package gift.auth;

import gift.member.domain.Member;
import gift.member.domain.RoleType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class AdminInterceptor implements HandlerInterceptor {

    private JwtUtil jwtUtil;

    public AdminInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = AuthUtil.extractToken(request);

        if(jwtUtil.getRoleType(token) != RoleType.ADMIN) {
            AuthUtil.handleAuthError(request, response, "관리자 권한이 필요합니다.");
            return false;
        }

        return true;
    }
}
