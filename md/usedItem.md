# 중고거래 중개하기

<details>
<summary>요구 사항 체크 ✔️</summary>
<div markdown="1">

- ✅ 물품 등록
    - ✅ 일반 사용자는 중고 거래를 목적으로 물품에 대한 정보를 등록할 수 있다.
        - ✅ 제목, 설명, 대표 이미지, 최소 가격이 필요하다.
            - ✅ 대표 이미지는 반드시 함께 등록될 필요는 없다.
            - ✅ 다른 항목은 필수이다.
            - ✅ 최초로 물품이 등록될 때, 중고 물품의 상태는 **판매중** 상태가 된다.
    - ✅ 등록된 물품 정보는 비활성 사용자를 제외 누구든지 열람할 수 있다.
        - ✅ 사용자의 상세 정보는 공개되지 않는다.
    - ✅ 등록된 물품 정보는 작성자가 수정, 삭제가 가능하다.
  

- ✅ 구매 제안
    - ✅ **물품을 등록한 사용자**와 **비활성 사용자** 제외, 등록된 물품에 대하여 구매 제안을 등록할 수 있다.
    - ✅ 등록된 구매 제안은 **물품을 등록한 사용자**와 **제안을 등록한 사용자**만 조회가 가능하다.
        - ✅ **제안을 등록한 사용자**는 자신의 제안만 확인이 가능하다.
        - ✅ **물품을 등록한 사용자**는 모든 제안이 확인 가능하다.
    - ✅ **물품을 등록한 사용자**는 **등록된 구매 제안**을 수락 또는 거절할 수 있다.
        - ✅ 이때 구매 제안의 상태는 **수락** 또는 **거절**이 된다.
    - ✅ **제안을 등록한 사용자**는 자신이 등록한 제안이 수락 상태일 경우, 구매 확정을 할 수 있다.
        - ✅ 이때 구매 제안의 상태는 **확정** 상태가 된다.
        - ✅ 구매 제안이 확정될 경우, 대상 물품의 상태는 **판매 완료**가 된다.
        - ✅ 구매 제안이 확정될 경우, 확정되지 않은 다른 구매 제안의 상태는 모두 **거절**이 된다.

</div>
</details>


### 1. 일반 사용자의 중고 물품 등록

<details>
<summary>Postman - 일반 사용자의 중고 뭂품 등록, 물품 이미지 등록</summary>
<div markdown="1">

중고 물품 등록

![used_item_1.png](img_usedItem%2Fused_item_1.png)
![used_item_2.jpeg](img_usedItem%2Fused_item_2.jpeg)

중고 물품 이미지 등록

![used_item_3.png](img_usedItem%2Fused_item_3.png)
![used_item_4.png](img_usedItem%2Fused_item_4.png)
![used_item_5.png](img_usedItem%2Fused_item_5.png)

</div>
</details>

```java
/**
 * 일반 사용자의 물품 등록
 * @param dto
 * @return
 */
public void registerItem(UsedItemDto dto) {
    // 현재 인증된 사용자의 정보 가져오기
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    UserEntity userEntity = getCurrentUserFromUsername(username);

    // 물품 객체 생성
    UsedItem usedItemEntity = UsedItem.builder()
    .title(dto.getTitle())
    .content(dto.getContent())
    .price(dto.getPrice())
    .status("판매중") // 기본값 설정
    .writer(userEntity)
    .build();

    usedItemRepository.save(usedItemEntity);
}

/**
 * 일반 사용자(현재 인증된)가 등록한 물품의 이미지 등록
 * @param multipartFile 이미지 파일 업로드
 * @param itemId 이미지를 업로드할 물품의 id
 * @return void
 * @throws IOException
 */
public void registerItemImage(MultipartFile multipartFile, Long itemId) throws IOException {
    // 사용자 정보 가져오기
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    UserEntity userEntity = getCurrentUserFromUsername(username);

    // 물품 정보 가져오기
    UsedItem usedItemEntity = getItemFromId(itemId);

    if (usedItemEntity.getWriter().getId().equals(userEntity.getId())) {
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
    usedItemEntity.setImageUrl(requestPath);
    usedItemRepository.save(usedItemEntity);
    }
}
```

### 2. 중고 물품 조회, 수정, 삭제

<details>
<summary>Postman - 일반 사용자의 중고 뭂품 조회, 수정, 삭제</summary>
<div markdown="1">

중고 물품 조회

![used_item_6.png](img_usedItem%2Fused_item_6.png)

중고 물품 수정

![used_item_7.png](img_usedItem%2Fused_item_7.png)
![used_item_8.png](img_usedItem%2Fused_item_8.png)

중고 물품 삭제

![used_item_9.png](img_usedItem%2Fused_item_9.png)
![used_item_10.png](img_usedItem%2Fused_item_10.png)

</div>
</details>

```java
/**
* 물품 전체 조회 서비스
* @return 조회된 물품 리스트
*/
public List<UsedItemDto> readAll() {
return usedItemRepository.findAll().stream()
        .map(item -> {
            UsedItemDto usedItemDto = UsedItemDto.fromEntity(item);
            usedItemDto.setWriterId(item.getWriter().getId()); // 작성자의 username을 설정
            return usedItemDto;
        })
        .toList();
}

/**
 * 물품 수정
 * @param dto 수정 내용
 * @param itemId 수정할 물품의 id
 * @return 업데이트가 완료되었으면 true, 아니면 false
 */
@Transactional
public Boolean updateItem(UsedItemDto dto, Long itemId) {

    // 물품 정보 조회
    UsedItem usedItemEntity = getItemFromId(itemId);

    // 사용자 정보 가져오기
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    UserEntity userEntity = getCurrentUserFromUsername(username);

    // itemId 에 헤댕하는 물품의 writer_id 와 현재 인증된 사용자의 id 가 같으면
    if (usedItemEntity.getWriter().getId().equals(userEntity.getId())) {
    usedItemEntity.setTitle(dto.getTitle());
    usedItemEntity.setContent(dto.getContent());
    usedItemEntity.setPrice(dto.getPrice());
    usedItemRepository.save(usedItemEntity);
    return true;
    }
    return false;
}

/**
 * 물품 삭제
 * @param itemId 삭제할 물품의 id
 * @return 조회한 물품을 삭제했으면 true, 아니면 false
 */
@Transactional
public boolean deleteItem(Long itemId) {
    // 사용자 정보 가져오기
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    UserEntity userEntity = getCurrentUserFromUsername(username);

    // 물품 정보 조회
    UsedItem usedItemEntity = getItemFromId(itemId);

    // itemId 에 헤댕하는 물품의 writer_id 와 현재 인증된 사용자의 id 가 같으면
    if (usedItemEntity.getWriter().getId().equals(userEntity.getId())) {
    // 조회한 물품 삭제
    usedItemRepository.delete(usedItemEntity);
    return true;
    }
    return false; // 물품을 찾을 수 없거나 권한이 없음
}
```

<br>

### 데이터베이스 정보 가져오기 
JWT 사용자 인증 정보, Path 파라미터로 부터 데이터베이스의 저장된 값을 가져온다.
서비스 메서드마다 중복되어 사용되므로 따로 메서드를 만들어서 사용

```java
private UserEntity getCurrentUserFromUsername(String username) {
    Optional<UserEntity> optionalUserEntity = userRepository.findByUsername(username);
    if (optionalUserEntity.isEmpty())
    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "사용자 정보를 찾을 수 없습니다.");
    return optionalUserEntity.get();
}

private UsedItem getItemFromId(Long itemId) {
    // 물품 정보 조회
    Optional<UsedItem> optionalItem = usedItemRepository.findById(itemId);
    if (optionalItem.isEmpty())
    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "물품 정보를 찾을 수 없습니다.");
    return optionalItem.get();
}
```

<br>

### 2. 구매 제안 등록하기

<details>
<summary>Postman - 중고 뭂품 구매 제안 등록, </summary>
<div markdown="1">

중고 물품 구매 제안

BusinessUser, User1 이라는 username 을 가진 사용자로 로그인

Changmo 사용자가 등록한 중고 물품에 대한 구매 제안 등록 (대기중 PENDING 상태)
![proposal_1.png](img_usedItem%2Fproposal_1.png)
![proposal_2.png](img_usedItem%2Fproposal_2.png)


</div>
</details>

```java
/**
 * 상품 구매 제안 등록하기
 * @param usedItemId
 * @return
 */
public void createProposal(Long usedItemId) {
    // 현재 인증된 사용자 정보 가져오기
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    UserEntity userEntity = getCurrentUserFromUsername(username);
    // 물품 조회
    UsedItem usedItemEntity = getUsedItemFromId(usedItemId);

    // 해당 물품을 등록한 사용자는 제외
    if (!userEntity.getId().equals(usedItemEntity.getWriter().getId())) {
        // 구매 제안 등록
        UsedItemProposal proposal = new UsedItemProposal();
        proposal.setUsedItem(usedItemEntity);
        proposal.setProposer(userEntity); // UserEntity 객체 설정
        proposal.setStatus(ProposalStatus.PENDING); // 대기 중 상태로 등록
        usedItemProposalRepository.save(proposal);
    } else {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "해당 물품을 등록한 사람은 구매 제안을 등록할 수 없습니다.");
    }
}
```

<br>

### 3. 구매 제안 확인하기

<details>
<summary>Postman - 중고 뭂품 구매 제안 확인 </summary>
<div markdown="1">

중고 물품 구매 제안 확인

- 해당 중고 물품을 구매한 사용자라면 (BusinessUser 또는 User1)
![proposal_3.png](img_usedItem%2Fproposal_3.png)

- 해당 중고 물품을 등록한 사용자라면 (Changmo)
![proposal_4.png](img_usedItem%2Fproposal_4.png)


</div>
</details>

```java
/**
 * 구매 제안 확인
 * 등록된 구매 제안은 물품을 등록한 사용자와 제안을 등록한 사용자만 조회 가능
 * @param usedItemId
 * @return
 */
public List<UsedItemProposalDto> getItemProposals(Long usedItemId) {
    // 현재 인증된 사용자 정보 가져오기
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    UserEntity userEntity = getCurrentUserFromUsername(username);
    // 물품 조회
    UsedItem usedItemEntity = getUsedItemFromId(usedItemId);

    // 해당 물품을 등록한 사용자라면
    if (usedItemEntity.getWriter().getId().equals(userEntity.getId())) {
    // 해당 물품에 대한 다른 사람들의 모든 구매 제안을 확인 가능
        List<UsedItemProposal> allProposals = usedItemProposalRepository.findByUsedItem(usedItemEntity);
        return allProposals.stream()
            .map(UsedItemProposalDto::fromEntity)
            .collect(Collectors.toList());
    }
    // 해당 물품을 구매 제안한 사람이라면
    else {
        List<UsedItemProposal> userProposals = usedItemProposalRepository.findByUsedItemAndProposerUsername(usedItemEntity, username);
        return userProposals.stream()
            .map(UsedItemProposalDto::fromEntity)
            .collect(Collectors.toList());
    }
}
```

<br>

### 4. 구매 제안 수락, 거절

<details>
<summary>Postman - 중고 뭂품 구매 제안 수락 </summary>
<div markdown="1">

수락
![proposal_5.png](img_usedItem%2Fproposal_5.png)
![proposal_6.png](img_usedItem%2Fproposal_6.png)

거절
![proposal_7.png](img_usedItem%2Fproposal_7.png)
![proposal_8.png](img_usedItem%2Fproposal_8.png)


</div>
</details>

```java
/**
 * 구매 제안 수락
 * @param usedItemId
 * @param proposalId
 */
public void acceptProposal(Long usedItemId, Long proposalId) { // 어떤 물품을, 누가 구매 제안했는지
    // 현재 인증된 사용자 정보 가져오기
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    UserEntity userEntity = getCurrentUserFromUsername(username);
    // 물품 조회
    UsedItem usedItemEntity = getUsedItemFromId(usedItemId);
    // 물품 구매 제안 조회
    UsedItemProposal proposalEntity = getUsedItemProposalFromId(proposalId);

    // 해당 물품을 등록한 사용자라면
    if (usedItemEntity.getWriter().getId().equals(userEntity.getId())) {
        // 구매 제인을 수락상태로 변경
        proposalEntity.setStatus(ProposalStatus.ACCEPTED);
        usedItemProposalRepository.save(proposalEntity);
    }
    else {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "해당 물품을 등록한 사용자만 구매 제안을 수락할 수 있습니다.");
    }
}

/**
 * 구매 제안 거절
 * @param proposalId
 */
public void rejectProposal(Long usedItemId, Long proposalId) { // 어떤 물품을, 누가 구매 제안했는지
    // 현재 인증된 사용자 정보 가져오기
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    UserEntity userEntity = getCurrentUserFromUsername(username);
    // 물품 조회
    UsedItem usedItemEntity = getUsedItemFromId(usedItemId);
    // 물품 구매 제안 조회
    UsedItemProposal proposalEntity = getUsedItemProposalFromId(proposalId);

    // 해당 물품을 등록한 사용자라면
    if (usedItemEntity.getWriter().getId().equals(userEntity.getId())) {
        // 구매 제인을 수락상태로 변경
        proposalEntity.setStatus(ProposalStatus.REJECTED);
        usedItemProposalRepository.save(proposalEntity);
    }
    else {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "해당 물품을 등록한 사용자만 구매 제안을 거절할 수 있습니다.");
    }
}
```

<br>

### 중고 물품 구매 제안 수락 확정

<details>
<summary>Postman - 중고 뭂품 구매 제안 수락 확정</summary>
<div markdown="1">


수락 확정
나머지 제안들은 거절 처리
![proposal_9.png](img_usedItem%2Fproposal_9.png)
![proposal_10.png](img_usedItem%2Fproposal_10.png)_

</div>
</details>

```java
// 제안을 등록한 사용자의 구매 확정
// 좀 복잡하다.
public void confirmProposal(Long usedItemId, Long proposalId) {
    // 현재 인증된 사용자 정보 가져오기
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    UserEntity userEntity = getCurrentUserFromUsername(username);
    // 물품 조회
    UsedItem usedItemEntity = getUsedItemFromId(usedItemId);
    // 물품 구매 제안 조회
    UsedItemProposal proposalEntity = getUsedItemProposalFromId(proposalId);
    
    // 해당 물품을 구매 제안한 사용자라면
    if (proposalEntity.getProposer().getId().equals(userEntity.getId())) {
        // 구매 제안 상태가 "ACCEPTED" 일 경우 아래 진행 (따로 조건문은 작성하지 않음)
        // 1. 구매 제안 상태를 "확정 상태" 로 변경
        proposalEntity.setStatus(ProposalStatus.CONFIRMED);
        usedItemProposalRepository.save(proposalEntity);

        // 2. 구매 제안이 "확정 상태"가 될 경우, 물품의 상태는 판매 완료가 된다.
        usedItemEntity.setStatus("판매완료");
        usedItemRepository.save(usedItemEntity);

        // 3. 구매 제안이 "확정 상태"가 될 경우, 확정되지 않은 다른 구매 제안의 상태는 모두 거절된다.
        List<UsedItemProposal> otherProposals = usedItemProposalRepository.findByUsedItemAndIdNot(usedItemEntity, proposalId);
        for (UsedItemProposal otherProposal : otherProposals) {
            otherProposal.setStatus(ProposalStatus.REJECTED);
            usedItemProposalRepository.save(otherProposal);
        }
    }
    else {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "해당 물품을 등록한 사용자만 구매를 확정할 수 있습니다.");
    }
}
```

