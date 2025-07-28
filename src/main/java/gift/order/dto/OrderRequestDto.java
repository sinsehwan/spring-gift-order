package gift.order.dto;

public record OrderRequestDto(
        Long productOptionId,
        Integer quantity,
        String message
) {
}
