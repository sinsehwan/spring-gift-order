package gift.auth;

import gift.member.domain.Member;
import gift.member.domain.RoleType;

public class FakeJwtUtil extends JwtUtil {

    public FakeJwtUtil() {
        super("fakeJwtSecretKey", 3600L);
    }

    @Override
    public String generateToken(Member member) {
        return "fakeToken" + member.getRole();
    }

    @Override
    public String getEmail(String token) {
        return "fakeEmail";
    }

    @Override
    public RoleType getRoleType(String token) {
        return RoleType.valueOf(token.substring("fakeToken".length()));
    }

    @Override
    public boolean validateToken(String token) {
        return true;
    }
}
