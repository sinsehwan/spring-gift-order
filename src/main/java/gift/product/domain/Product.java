package gift.product.domain;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private int price;
    @Column(nullable = false)
    private String imageUrl;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductOption> productOptions = new ArrayList<>();

    protected Product() {}

    public Product(Long id, String name, int price, String imageUrl){
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public Product(String name, int price, String imageUrl) {
        this(null, name, price, imageUrl);
    }

    public Product(Long id){
        this(id, null, 0, null);
    }

    public void addProductOption(ProductOption productOption){
        if(productOption != null && !this.productOptions.contains(productOption)){
            validateIsDuplicate(productOption);

            this.productOptions.add(productOption);
            productOption.setProduct(this);
        }
    }

    private void validateIsDuplicate(ProductOption newOption){
        boolean isDuplicate = productOptions
                .stream()
                .anyMatch(option -> Objects.equals(option.getName(), newOption.getName()));

        if(isDuplicate){
            throw new IllegalArgumentException("동일한 이름의 옵션이 이미 존재합니다.");
        }
    }

    public void updateProduct(String name, int price, String imageUrl) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public Long getId(){
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public List<ProductOption> getProductOptions() {
        return productOptions;
    }
}
