package gift.order.service;

import gift.auth.oauth.KakaoApiClient;
import gift.member.domain.Member;
import gift.member.dto.MemberTokenRequest;
import gift.member.repository.MemberRepository;
import gift.order.domain.Order;
import gift.order.dto.OrderRequestDto;
import gift.order.dto.OrderResponseDto;
import gift.order.repository.OrderRepository;
import gift.product.domain.ProductOption;
import gift.product.exception.ProductNotFoundException;
import gift.product.repository.ProductOptionRepository;
import gift.wish.repository.WishRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ProductOptionRepository optionRepository;
    private final WishRepository wishRepository;
    private final KakaoApiClient kakaoApiClient;

    public OrderService(OrderRepository orderRepository, MemberRepository memberRepository, ProductOptionRepository optionRepository, WishRepository wishRepository, KakaoApiClient kakaoApiClient) {
        this.orderRepository = orderRepository;
        this.memberRepository = memberRepository;
        this.optionRepository = optionRepository;
        this.wishRepository = wishRepository;
        this.kakaoApiClient = kakaoApiClient;
    }

    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto requestDto, MemberTokenRequest memberTokenRequest) {
        Member member = memberRepository.findById(memberTokenRequest.id())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        ProductOption option = optionRepository.findById(requestDto.optionId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 옵션입니다."));

        option.subtractQuantity(requestDto.quantity());

        Order order = new Order(option, member, requestDto.quantity(), requestDto.message());
        orderRepository.save(order);

        // 카카오 유저 대상 메시지 전송 구현 필요

        return OrderResponseDto.from(order);
    }
}
