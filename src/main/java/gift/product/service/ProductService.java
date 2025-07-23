package gift.product.service;

import gift.member.domain.Member;
import gift.member.domain.RoleType;
import gift.product.domain.Product;
import gift.product.dto.ProductEditRequestDto;
import gift.product.dto.ProductOptionRequestDto;
import gift.product.dto.ProductRequestDto;
import gift.product.exception.ProductNotFoundException;
import gift.product.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    public Product saveProduct(ProductRequestDto requestDto){
        Product product = new Product(requestDto.name(), requestDto.price(), requestDto.imageUrl());
        requestDto.options()
                .stream()
                .map(ProductOptionRequestDto::toEntity)
                .forEach(product::addProductOption);

        return productRepository.save(product);
    }

    public Page<Product> getProducts(Pageable pageable){
        return productRepository.findAll(pageable);
    }

    public Product getProduct(Long id){
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("상품을 찾을 수 없습니다. ID: " + id));
    }

    public void update(Long id, ProductEditRequestDto requestDto){
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("상품을 찾을 수 없습니다. ID: " + id));
        product.updateProduct(requestDto.name(), requestDto.price(), requestDto.imageUrl());
    }

    public void delete(Long id){
        productRepository.deleteById(id);
    }

    private void validateProductNameByRole(String productName, Member member) {
        if (productName.contains("카카오") && member.getRole() != RoleType.ADMIN) {
            throw new IllegalArgumentException("상품 이름에 '카카오'는 포함될 수 없습니다. 담당 MD와 협의해 주세요.");
        }
    }
}
