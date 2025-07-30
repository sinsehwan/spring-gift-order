package gift.auth.oauth.exception;

import org.springframework.http.HttpStatusCode;

public class KakaoApiFailedException extends RuntimeException {
    private final HttpStatusCode statusCode;

    public KakaoApiFailedException(String message, HttpStatusCode statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }
}
