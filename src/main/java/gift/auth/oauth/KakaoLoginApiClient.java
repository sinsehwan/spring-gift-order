package gift.auth.oauth;

import gift.auth.oauth.dto.KakaoTokenResponseDto;
import gift.auth.oauth.dto.KakaoUserInfoResponseDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Component
public class KakaoLoginApiClient {
    private final RestClient authClient;
    private final RestClient apiClient;
    private final KakaoProperties properties;

    private final String AUTH_URI = "https://kauth.kakao.com";
    private final String USER_API_URL = "https://kapi.kakao.com";

    public KakaoLoginApiClient(KakaoProperties properties, RestClient.Builder restClientBuilder) {
        this.properties = properties;
        this.authClient = restClientBuilder.clone()
                .baseUrl(AUTH_URI)
                .build();
        this.apiClient = restClientBuilder.clone()
                .baseUrl(USER_API_URL)
                .build();
    }

    public String fetchAccessToken(String authCode) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();

        body.add("grant_type", "authorization_code");
        body.add("client_id", properties.clientId());
        body.add("redirect_uri", properties.redirectUri());
        body.add("code", authCode);
        body.add("client_secret", properties.clientSecret());

        KakaoTokenResponseDto tokenResponse = authClient.post()
                .uri("/oauth/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve().body(KakaoTokenResponseDto.class);

        return tokenResponse.accessToken();
    }

    public KakaoUserInfoResponseDto fetchUserInfo(String accessToken) {
        return apiClient.get()
                .uri("/v2/user/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve().body(KakaoUserInfoResponseDto.class);
    }

}
