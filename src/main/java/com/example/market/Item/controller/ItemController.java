package com.example.market.Item.controller;

import com.example.market.Item.dto.ItemDto;
import com.example.market.Item.entity.Item;
import com.example.market.Item.repo.ItemRepository;
import com.example.market.Item.service.ItemService;
import com.example.market.entity.UserEntity;
import com.example.market.repo.UserRepository;
import java.awt.event.ItemEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@Slf4j
@RestController
@RequestMapping("items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;


    /**
     * 물품 전체 조회
     * @return
     */
    @GetMapping
    public List<ItemDto> readAll() {
        return itemService.readAll();
    }

    /**
     * 일반 사용자의 물품 등록
     * @param dto
     * @return
     */
    @PostMapping("/register")
    public ResponseEntity<String> registerItem(
            @RequestBody
            ItemDto dto
    ) {
        // 현재 인증된 사용자의 정보 가져오기
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        // 데이터베이스에서 사용자 정보 가져오기
        Optional<UserEntity> optionalUserEntity = userRepository.findByUsername(username);
        if (optionalUserEntity.isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("사용자 정보를 찾을 수 없습니다.");
        }
        UserEntity userEntity = optionalUserEntity.get();


        // 물품 객체 생성
        Item itemEntity = Item.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .price(dto.getPrice())
                .status("판매중") // 기본값 설정
                .writer(username)
                .build();

        itemRepository.save(itemEntity);

        return ResponseEntity.ok("물품이 성공적으로 등록되었습니다.");
    }


    /**
     * 일반 사용자가 등록한 물품의 이미지 등록
     * @param multipartFile
     * @param itemId
     * @return
     * @throws IOException
     */
    @PostMapping(
            value = "/register-image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<String> registerItemImage(
            @RequestParam("file")
            MultipartFile multipartFile,
            @RequestParam("itemId")
            Long itemId
    ) throws IOException {
        // 현재 인증된 사용자의 정보 가져오기
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        // 데이터베이스에서 사용자 정보 가져오기
        Optional<UserEntity> optionalUserEntity = userRepository.findByUsername(username);
        if (optionalUserEntity.isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("사용자 정보를 찾을 수 없습니다.");
        }

        // 데이터베이스에서 물품 정보 가져오기
        Optional<Item> optionalItemEntity = itemRepository.findById(itemId);
        if (optionalItemEntity.isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("물품 정보를 찾을 수 없습니다.");
        }
        Item itemEntity = optionalItemEntity.get();

        // 파일 저장 경로 설정
        String profileDir = String.format("media/%s/", username);
        try {
            // 폴더 생성
            Files.createDirectories(Paths.get(profileDir));
        } catch (IOException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("디렉토리 생성 실패");
        }

        // 파일명 조합: media/{username}/profile.{확장자}
        String originalFilename = multipartFile.getOriginalFilename();
        String[] fileNameSplit = originalFilename.split("\\.");
        String extension = fileNameSplit[fileNameSplit.length - 1];
        String profileFilename = "product." + extension;
        String profilePath = profileDir + profileFilename;
        log.info(profileFilename);

        multipartFile.transferTo(Path.of(profilePath));
        log.info(profilePath);

        // 업로드된 파일의 URL 저장
        String requestPath = String.format("/media/%s/%s", username, profileFilename);
        itemEntity.setImageUrl(requestPath);
        itemRepository.save(itemEntity);

        return ResponseEntity.ok("물품 이미지가 성공적으로 등록되었습니다.");
    }


    /**
     * 물품 수정
     * @param dto
     * @return
     */
    @PutMapping("/update/{itemId}")
    public ResponseEntity<String> updateItem(
            @PathVariable
            Long itemId,
            @RequestBody
            ItemDto dto
    ) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (itemService.updateItem(dto, itemId, username)) {
            return ResponseEntity.ok("물품이 성공적으로 수정되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("물품을 찾을 수 없거나 권한이 없습니다.");
        }
    }

    /**
     * 물품 삭제
     * @return
     */
    @DeleteMapping("/delete/{itemId}")
    public ResponseEntity<String> deleteItem(
            @PathVariable
            Long itemId
    ) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (itemService.deleteItem(itemId, username)) {
            return ResponseEntity.ok("물품이 성공적으로 삭제되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("물품을 찾을 수 없습니다.");
        }
    }


}
