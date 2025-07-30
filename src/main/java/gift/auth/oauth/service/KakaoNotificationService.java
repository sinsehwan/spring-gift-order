package gift.auth.oauth.service;

import gift.auth.oauth.KakaoApiClient;
import gift.auth.oauth.dto.KakaoMessageDto;
import gift.auth.oauth.event.KakaoOrderCompletedEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
public class KakaoNotificationService {
    private final KakaoApiClient kakaoApiClient;

    public KakaoNotificationService(KakaoApiClient kakaoApiClient) {
        this.kakaoApiClient = kakaoApiClient;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendOrderMessageToMe(KakaoOrderCompletedEvent event) {
        // access token 만료 시 refresh token을 사용해서 갱신하도록 추후 보완 필요.
        KakaoMessageDto messageDto = KakaoMessageDto.createCommerceTemplate(event.product());
        kakaoApiClient.sendMessageToMe(event.kakaoAccessToken(), messageDto);
    }
}
