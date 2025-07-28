package gift.auth.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import gift.auth.oauth.dto.KakaoMessageDto;
import gift.auth.oauth.dto.KakaoTokenResponseDto;
import gift.auth.oauth.dto.KakaoUserInfoResponseDto;
import gift.common.util.JsonUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class KakaoApiClient {
    private final RestClient authClient;
    private final RestClient apiClient;
    private final KakaoProperties properties;
    private final JsonUtil jsonUtil;

    private final String AUTH_URI = "https://kauth.kakao.com";
    private final String USER_API_URL = "https://kapi.kakao.com";

    public KakaoApiClient(KakaoProperties properties, JsonUtil jsonUtil, RestClient.Builder restClientBuilder) {
        this.properties = properties;
        this.jsonUtil = jsonUtil;
        this.authClient = restClientBuilder.clone()
                .baseUrl(AUTH_URI)
                .build();
        this.apiClient = restClientBuilder.clone()
                .baseUrl(USER_API_URL)
                .build();
    }

    public KakaoTokenResponseDto fetchAccessToken(String authCode) {
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

        return tokenResponse;
    }

    public KakaoUserInfoResponseDto fetchUserInfo(String accessToken) {
        return apiClient.get()
                .uri("/v2/user/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve().body(KakaoUserInfoResponseDto.class);
    }

    public void sendMessageToMe(String accessToken, KakaoMessageDto messageDto) {
        String templateJson = jsonUtil.toJson(messageDto);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();

        body.add("template_object", templateJson);

        apiClient.post()
                .uri("/v2/api/talk/memo/default/send")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .toBodilessEntity();
    }
}
