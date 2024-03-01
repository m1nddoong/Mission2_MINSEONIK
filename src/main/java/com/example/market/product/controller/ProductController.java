package com.example.market.product.controller;


import com.example.market.entity.UserEntity;
import com.example.market.product.dto.ProductDto;
import com.example.market.product.service.ProductService;
import com.example.market.repo.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService service;
    // 사용자 인증 정보 조회를 위한 용도
    // 테스트용 데이터를 등록시키기 위해서
    // createProduct 서비스 메서드 내에 사용자 인증 부분을 요구하는 부분을 컨트롤러로 분리시킴
    private final UserRepository userRepository;

    /**
     * 쇼핑몰에 상품 등록
     * @param dto 상품 등록 정보
     * @return 등록 완료 (쇼핑몰 주인만 가능)
     */
    @PostMapping("/create")
    public ResponseEntity<String> createProduct(
            @RequestBody
            ProductDto dto
    ) {
        // 현재 인증된 사용자 정보 가져오기
        UserEntity userEntity = getCurrentUser();
        if (service.createProduct(userEntity, dto)) {
            return ResponseEntity.ok("쇼핑몰에 상품을 등록하였습니다.");
        } else {
            return ResponseEntity.ok("쇼핑몰이 개설되지 않았거나, 상품 등록에 실패하였습니다.");
        }
    }

    /**
     *  TODO : 상품 이미지 추가는 제외 (나중에 구현)
     */


    /**
     * 쇼핑몰 상품 수정
     * @param dto
     * @return
     */
    @PutMapping("{productId}/update")
    public ResponseEntity<String> updateProduct(
            @PathVariable
            Long productId,
            @RequestBody
            ProductDto dto
    ) {
        if (service.updateProduct(productId, dto)) {
            return ResponseEntity.ok("쇼핑몰의 상품이 수정되었습니다.");
        } else {
            return ResponseEntity.ok("쇼핑몰이 개설되지 않았거나, 상품 수정에 실패하였습니다.");
        }
    }


    /**
     * 쇼핑몰 상품 삭제
     * @param productId
     * @return
     */
    @DeleteMapping("{productId}/delete")
    public ResponseEntity<String> deleteProduct(
            @PathVariable
            Long productId
    ) {
        if (service.deleteProduct(productId)) {
            return ResponseEntity.ok("쇼핑몰의 상품이 삭제되었습니다.");
        } else {
            return ResponseEntity.ok("쇼핑몰이 개설되지 않았거나, 상품 삭제에 실패하였습니다.");
        }
    }



    // 사용자의 인증 정보를 가져올 메서드
    private UserEntity getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<UserEntity> optionalUserEntity = userRepository.findByUsername(username);
        if (optionalUserEntity.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "사용자 정보를 찾을 수 없습니다.");
        }
        return optionalUserEntity.get();
    }


}
