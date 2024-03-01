package com.example.market.product.service;


import static com.example.market.shop.entity.ShopStatus.OPEN;

import com.example.market.entity.UserEntity;
import com.example.market.product.dto.ProductDto;
import com.example.market.product.entity.Product;
import com.example.market.product.repo.ProductRepository;
import com.example.market.repo.UserRepository;
import com.example.market.shop.entity.Shop;
import com.example.market.shop.entity.ShopStatus;
import com.example.market.shop.repo.ShopRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
// @RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ShopRepository shopRepository;
    private final UserRepository userRepository;

    public ProductService(
            ProductRepository productRepository,
            ShopRepository shopRepository,
            UserRepository userRepository
    ) {
        this.productRepository = productRepository;
        this.shopRepository = shopRepository;
        this.userRepository = userRepository;

        // 테스트용 데이터 가져오기
        // BusinessUser1
        Optional<UserEntity> userEntity = userRepository.findById(2L);
        if (userEntity.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.");

        Product product = Product.builder()
                .name("아로마틱 우드 10ml")
                .content("스프레이 퍼퓸")
                .price(20000)
                .category("화장품")
                .subCategory("향수")
                .Stock(5)
                .writer(userEntity.get())
                .build();
        productRepository.save(product);

        // USER1
        userEntity = userRepository.findById(3L);
        if (userEntity.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.");

        product = Product.builder()
                .name("벤큐 모니터")
                .content("144hz")
                .price(150000)
                .category("PC제품")
                .subCategory("모니터")
                .Stock(30)
                .writer(userEntity.get())
                .build();
        productRepository.save(product);

        product = Product.builder()
                .name("삼성 모니터")
                .content("60hz")
                .price(240000)
                .category("PC제품")
                .subCategory("모니터")
                .Stock(50)
                .writer(userEntity.get())
                .build();
        productRepository.save(product);

        // USER2
        userEntity = userRepository.findById(4L);
        if (userEntity.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.");

        product = Product.builder()
                .name("수제 첼시부츠")
                .content("천연 가죽, 무광")
                .price(110000)
                .category("의류")
                .subCategory("신발")
                .Stock(1)
                .writer(userEntity.get())
                .build();
        productRepository.save(product);
    }


    /**
     * 쇼핑몰에 상품 등록
     *
     * @param dto
     * @return
     */
    public boolean createProduct(ProductDto dto) {
        // 현재 인증된 사용자 정보 가져오기
        UserEntity userEntity = getCurrentUser();

        // 쇼핑몰 정보 가져오기
        Shop shop = getCurrentUserShop(userEntity);

        if (shop.getShopStatus().equals(OPEN) && shop.getOwner().getId().equals(userEntity.getId())) {
            // 쇼핑몰에 상품을 추가
            Product product = new Product();
            product.setName(dto.getName());
            product.setContent(dto.getContent());
            product.setPrice(dto.getPrice());
            product.setCategory(dto.getCategory());
            product.setSubCategory(dto.getSubCategory());
            product.setStock(dto.getStock());
            product.setWriter(userEntity);

            // 저장
            productRepository.save(product);
            return true;
        } else {
            return false;
        }
    }

    /**
     * TODO : 상품 이미지 추가는 제외 (나중에 구현)
     */

    // 상품 수정
    public boolean updateProduct(Long productId, ProductDto dto) {
        // 현재 인증된 사용자 정보 가져오기
        UserEntity userEntity = getCurrentUser();
        // 쇼핑몰 정보 가져오기
        Shop shop = getCurrentUserShop(userEntity);
        // 쇼핑몰의 상품 정보 가져오기 (상품 id 로)
        Product product = getProductFromId(productId);

        if (shop.getShopStatus().equals(OPEN) && product.getWriter().getId().equals(userEntity.getId())) {
            // 쇼핑몰의 상품 수정하기
            product.setName(dto.getName());
            product.setContent(dto.getContent());
            product.setPrice(dto.getPrice());
            product.setCategory(dto.getCategory());
            product.setSubCategory(dto.getSubCategory());
            product.setStock(dto.getStock());

            // 저장
            productRepository.save(product);
            return true;

        } else {
            return false;
        }
    }


    // 상품 삭제
    public boolean deleteProduct(Long productId) {
        // 현재 인증된 사용자 정보 가져오기
        UserEntity userEntity = getCurrentUser();
        // 쇼핑몰 정보 가져오기
        Shop shop = getCurrentUserShop(userEntity);

        // productId 로 상품의 정보를 조회하고
        Product product = getProductFromId(productId);

        // 상품의 writer_id 가 현재 인증된 사용자의 id 와 같다면
        if (product.getWriter().getId().equals(userEntity.getId())) {
            // 쇼핑몰 상품 삭제하기
            productRepository.deleteById(productId);
            return true;
        }
        return false;

    }

    // 쇼핑몰을 조회


    /*
   <===================================================================>
   사용자 객체, 쇼핑몰 객체, 쇼핑몰 상품 객체를 가져오는 부분을 따로 메서드로 추출
   <===================================================================>
    */

    // 사용자의 인증 정보를 가져올 메서드
    private UserEntity getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UserEntity> optionalUserEntity = userRepository.findByUsername(username);
        if (optionalUserEntity.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "사용자 정보를 찾을 수 없습니다.");
        }
        return optionalUserEntity.get();
    }

    private Shop getCurrentUserShop(UserEntity userEntity) {
        Optional<Shop> optionalShopEntity = shopRepository.findByOwnerId(userEntity.getId());
        if (optionalShopEntity.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "사용자의 쇼핑몰 정보를 찾을 수 없습니다.");
        }
        return optionalShopEntity.get();
    }

    private Product getProductFromId(Long productId) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "상품 정보를 찾을 수 없습니다.");
        }
        return optionalProduct.get();
    }

}
