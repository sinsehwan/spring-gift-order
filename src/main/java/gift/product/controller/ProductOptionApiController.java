package gift.product.controller;

import gift.product.dto.ProductOptionResponseDto;
import gift.product.service.ProductOptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products/{productId}/options")
public class ProductOptionApiController {
    private final ProductOptionService optionService;

    public ProductOptionApiController(ProductOptionService optionService){
        this.optionService = optionService;
    }

    @GetMapping
    public ResponseEntity<List<ProductOptionResponseDto>> getOptions(@PathVariable Long productId) {
        List<ProductOptionResponseDto> options = optionService.getProductOptions(productId);

        return ResponseEntity.ok(options);
    }
}
