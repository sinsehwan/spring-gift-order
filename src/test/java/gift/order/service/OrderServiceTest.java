package gift.order.service;

import gift.auth.oauth.event.KakaoOrderCompletedEvent;
import gift.member.domain.Member;
import gift.member.domain.RoleType;
import gift.member.dto.MemberTokenRequest;
import gift.member.repository.MemberRepository;
import gift.order.dto.OrderRequestDto;
import gift.product.domain.Product;
import gift.product.domain.ProductOption;
import gift.product.repository.ProductOptionRepository;
import gift.product.repository.ProductRepository;
import gift.wish.domain.Wish;
import gift.wish.repository.WishRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Transactional
class OrderServiceTest {
    @Autowired
    private OrderService orderService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductOptionRepository optionRepository;
    @Autowired
    private WishRepository wishRepository;

    @MockitoBean
    private ApplicationEventPublisher eventPublisher;

    private Member member;
    private Member kakaoMember;
    private Product product;
    private ProductOption option;
    private MemberTokenRequest memberTokenRequest;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(new Member("test@test.com", "password", RoleType.USER));
        kakaoMember = memberRepository.save(new Member("kakao@gmail.com", "password", RoleType.USER, 1234L, "access token", "refresh token"));
        product = new Product("product", 10000, "image.jpg");
        option = new ProductOption("option", 100);
        product.addProductOption(option);
        product = productRepository.save(product);

        memberTokenRequest = new MemberTokenRequest(member.getId(), member.getEmail(), "password", RoleType.USER);
    }

    @Test
    @DisplayName("주문 성공 - 위시리스트 삭제 확인")
    void createOrder() {
        wishRepository.save(new Wish(member, product, 10));
        OrderRequestDto requestDto = new OrderRequestDto(option.getId(), 10, "체크");

        orderService.createOrder(requestDto, memberTokenRequest);

        boolean wishExists = wishRepository.existsByMemberIdAndProductId(member.getId(), product.getId());
        assertThat(wishExists).isFalse();

        ProductOption updatedOption = optionRepository.findById(option.getId()).get();
        assertThat(updatedOption.getQuantity()).isEqualTo(90);

        verify(eventPublisher, never()).publishEvent(any(KakaoOrderCompletedEvent.class));
    }

    @Test
    @DisplayName("카카오 유저 주문 성공 - 이벤트 발행 확인")
    void createOrderKakaoUser() {
        OrderRequestDto requestDto = new OrderRequestDto(option.getId(), 5, "카카오 유저 주문");

        orderService.createOrder(requestDto, memberTokenRequest);

        verify(eventPublisher).publishEvent(any(KakaoOrderCompletedEvent.class));
    }

    @Test
    @DisplayName("Product Option 재고 부족 시 주문 생성 실패")
    void createOrderInsufficient() {
        OrderRequestDto requestDto = new OrderRequestDto(option.getId(), 101, "많이 주문하기");

        assertThrows(IllegalArgumentException.class, () -> {
            orderService.createOrder(requestDto, memberTokenRequest);
        });

        assertThat(optionRepository.findById(option.getId()).get().getQuantity()).isEqualTo(100);
    }
}