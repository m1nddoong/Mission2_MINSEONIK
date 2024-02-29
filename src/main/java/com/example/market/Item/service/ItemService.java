package com.example.market.Item.service;

import com.example.market.Item.dto.ItemDto;
import com.example.market.Item.entity.Item;
import com.example.market.Item.repo.ItemRepository;
import com.example.market.entity.UserEntity;
import com.example.market.repo.UserRepository;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;


@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;


    /**
     * 일반 사용자의 물품 등록
     * @param dto
     * @return
     */
    public void registerItem(ItemDto dto) {
        // 현재 인증된 사용자의 정보 가져오기
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        // 데이터베이스에서 사용자 정보 가져오기
        Optional<UserEntity> optionalUserEntity = userRepository.findByUsername(username);
        if (optionalUserEntity.isEmpty())
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "사용자 정보를 찾을 수 없습니다.");

        // 물품 객체 생성
        Item itemEntity = Item.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .price(dto.getPrice())
                .status("판매중") // 기본값 설정
                .writer(optionalUserEntity.get())
                .build();

        itemRepository.save(itemEntity);
    }


    /**
     * 일반 사용자(현재 인증된)가 등록한 물품의 이미지 등록
     * @param multipartFile 이미지 파일 업로드
     * @param itemId 이미지를 업로드할 물품의 id
     * @return void
     * @throws IOException
     */
    public void registerItemImage(MultipartFile multipartFile, Long itemId) throws IOException {
        // 현재 인증된 사용자의 username 가져오기
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        // 사용자 정보 가져오기
        Optional<UserEntity> optionalUserEntity = userRepository.findByUsername(username);
        if (optionalUserEntity.isEmpty())
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "사용자 정보를 찾을 수 없습니다.");
        UserEntity userEntity = optionalUserEntity.get();

        // 물품 정보 가져오기
        Optional<Item> optionalItemEntity = itemRepository.findById(itemId);
        if (optionalItemEntity.isEmpty())
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "물품 정보를 찾을 수 없습니다.");
        Item itemEntity = optionalItemEntity.get();

        if (itemEntity.getWriter().getId().equals(userEntity.getId())) {
            // 파일 저장 경로 설정
            String profileDir = String.format("media/%s/", username);
            try {
                // 폴더 생성
                Files.createDirectories(Paths.get(profileDir));
            } catch (IOException e) {
                log.error(e.getMessage());
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "디렉토리 생성 실패");
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
        }
    }


    /**
     * 물품 전체 조회 서비스
     * @return 조회된 물품 리스트
     */
    public List<ItemDto> readAll() {
        return itemRepository.findAll().stream()
                .map(item -> {
                    ItemDto itemDto = ItemDto.fromEntity(item);
                    itemDto.setWriterId(item.getWriter().getId()); // 작성자의 username을 설정
                    return itemDto;
                })
                .toList();
    }


    /**
     * 물품 수정
     * @param dto 수정 내용
     * @param itemId 수정할 물품의 id
     * @param username 현재 인증된 사용자
     * @return 업데이트가 완료되었으면 true, 아니면 false
     */
    @Transactional
    public Boolean updateItem(ItemDto dto, Long itemId, String username) {
        // 물품 정보 조회
        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if (optionalItem.isEmpty())
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "물품 정보를 찾을 수 없습니다.");
        Item itemEntity = optionalItem.get();

        // 사용자 정보 가져오기
        Optional<UserEntity> optionalUserEntity = userRepository.findByUsername(username);
        if (optionalUserEntity.isEmpty())
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "사용자 정보를 찾을 수 없습니다.");
        UserEntity userEntity = optionalUserEntity.get();

        // itemId 에 헤댕하는 물품의 writer_id 와 현재 인증된 사용자의 id 가 같으면
        if (itemEntity.getWriter().getId().equals(userEntity.getId())) {
            itemEntity.setTitle(dto.getTitle());
            itemEntity.setContent(dto.getContent());
            itemEntity.setPrice(dto.getPrice());
            itemEntity.setStatus(dto.getStatus());
            itemRepository.save(itemEntity);
            return true;
        }
        return false;
    }


    /**
     * 물품 삭제
     * @param itemId 삭제할 물품의 id
     * @param username 현재 인증된 사용자
     * @return 조회한 물품을 삭제했으면 true, 아니면 false
     */
    @Transactional
    public boolean deleteItem(Long itemId, String username) {
        // 물품 정보 조회
        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if (optionalItem.isEmpty())
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "물품 정보를 찾을 수 없습니다.");
        Item itemEntity = optionalItem.get();

        // 사용자 정보 가져오기
        Optional<UserEntity> optionalUserEntity = userRepository.findByUsername(username);
        if (optionalUserEntity.isEmpty())
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "사용자 정보를 찾을 수 없습니다.");
        UserEntity userEntity = optionalUserEntity.get();

        // itemId 에 헤댕하는 물품의 writer_id 와 현재 인증된 사용자의 id 가 같으면
        if (itemEntity.getWriter().getId().equals(userEntity.getId())) {
            // 조회한 물품 삭제
            itemRepository.delete(itemEntity);
            return true;
        }
        return false; // 물품을 찾을 수 없거나 권한이 없음
    }
}
