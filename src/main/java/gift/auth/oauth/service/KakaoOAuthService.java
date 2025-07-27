package gift.auth.oauth.service;

import gift.auth.JwtUtil;
import gift.auth.oauth.KakaoLoginApiClient;
import gift.auth.oauth.dto.KakaoUserInfoResponse;
import gift.member.domain.Member;
import gift.member.domain.RoleType;
import gift.member.dto.MemberTokenResponse;
import gift.member.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class KakaoOAuthService {
    private final KakaoLoginApiClient loginApiClient;
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    public KakaoOAuthService(KakaoLoginApiClient loginApiClient, MemberRepository memberRepository, JwtUtil jwtUtil){
        this.loginApiClient = loginApiClient;
        this.memberRepository = memberRepository;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public MemberTokenResponse login(String authCode){
        String accessToken = loginApiClient.fetchAccessToken(authCode);
        KakaoUserInfoResponse userInfo = loginApiClient.fetchUserInfo(accessToken);

        Member member = memberRepository.findByKakaoId(userInfo.id())
                .orElseGet(() -> registerMemberByOAuth(userInfo));

        String memberToken = jwtUtil.generateToken(member);
        return new MemberTokenResponse(memberToken);
    }

    private Member registerMemberByOAuth(KakaoUserInfoResponse userInfo){
        Member newMember = new Member(
                userInfo.kakaoAccount().email(),
                UUID.randomUUID().toString(),
                RoleType.USER,
                userInfo.id()
        );
        return memberRepository.save(newMember);
    }
}
