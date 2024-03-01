package com.example.market.Item.controller;

import com.example.market.Item.dto.ItemDto;
import com.example.market.Item.repo.ItemRepository;
import com.example.market.Item.service.ItemService;
import com.example.market.user.repo.UserRepository;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    private final ItemService service;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;


    /**
     * 물품 전체 조회
     * @return
     */
    @GetMapping
    public List<ItemDto> readAll() {
        return service.readAll();
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
        service.registerItem(dto);
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
        service.registerItemImage(multipartFile, itemId);
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
        if (service.updateItem(dto, itemId)) {
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
        if (service.deleteItem(itemId)) {
            return ResponseEntity.ok("물품이 성공적으로 삭제되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("물품을 찾을 수 없습니다.");
        }
    }


}
