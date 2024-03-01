# Project -  Market


## ☀️ 프로젝트 소개
- 일반 사용자는 중고거래가 가능하며, 사업자는 인터넷 쇼핑몰을 운영할 수 있게 해주는
  쇼핑몰 사이트를 위한 REST API 구현


## 💻 개발 환경

- Java : `17`
- Framework : `Spring Boot 3.2.1`, `querydsl`, `jjwt`, `smtp`
- Build : `gradle`
- DataBase : `SQLite`
- ORM : `JPA` 


## 🌳 패키지 구조
<details>
<summary>src 패키지 구조 (24.03.01 ver)</summary>
<div markdown="1">

```bash
src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── example
    │   │           └── market
    │   │               ├── Item
    │   │               │   ├── controller
    │   │               │   │   ├── ItemController.java
    │   │               │   │   └── ItemProposalController.java
    │   │               │   ├── dto
    │   │               │   │   ├── ItemDto.java
    │   │               │   │   └── ItemProposalDto.java
    │   │               │   ├── entity
    │   │               │   │   ├── Item.java
    │   │               │   │   ├── ItemProposal.java
    │   │               │   │   └── ProposalStatus.java
    │   │               │   ├── repo
    │   │               │   │   ├── ItemProposalRepository.java
    │   │               │   │   └── ItemRepository.java
    │   │               │   └── service
    │   │               │       ├── ItemProposalService.java
    │   │               │       └── ItemService.java
    │   │               ├── MarketApplication.java
    │   │               ├── config
    │   │               │   ├── PasswordEncoderConfig.java
    │   │               │   └── WebSecurityConfig.java
    │   │               ├── shop
    │   │               │   ├── controller
    │   │               │   │   ├── ProductController.java
    │   │               │   │   └── ShopController.java
    │   │               │   ├── dto
    │   │               │   │   ├── ClosureRequestDto.java
    │   │               │   │   ├── EmailDto.java
    │   │               │   │   ├── ProductDto.java
    │   │               │   │   └── ShopDto.java
    │   │               │   ├── entity
    │   │               │   │   ├── Product.java
    │   │               │   │   ├── ProductOrder.java
    │   │               │   │   ├── Shop.java
    │   │               │   │   ├── ShopCategory.java
    │   │               │   │   └── ShopStatus.java
    │   │               │   ├── repo
    │   │               │   │   ├── ProductRepository.java
    │   │               │   │   └── ShopRepository.java
    │   │               │   └── service
    │   │               │       ├── EmailService.java
    │   │               │       ├── ProductService.java
    │   │               │       └── ShopService.java
    │   │               └── user
    │   │                   ├── controller
    │   │                   │   ├── TokenController.java
    │   │                   │   └── UserController.java
    │   │                   ├── dto
    │   │                   │   └── UserDto.java
    │   │                   ├── entity
    │   │                   │   ├── CustomUserDetails.java
    │   │                   │   └── UserEntity.java
    │   │                   ├── jwt
    │   │                   │   ├── JwtRequestDto.java
    │   │                   │   ├── JwtResponseDto.java
    │   │                   │   ├── JwtTokenFilter.java
    │   │                   │   └── JwtTokenUtils.java
    │   │                   ├── repo
    │   │                   │   └── UserRepository.java
    │   │                   └── service
    │   │                       ├── JpaUserDetailsManager.java
    │   │                       └── UserService.java
    │   └── resources
    │       ├── application.yaml
    │       ├── static
    │       └── templates
    └── test
        └── java
            └── com
                └── example
                    └── market
                        └── MarketApplicationTests.java

```
</div>
</details>

## 📚 DB 설계



## 🛠️  기능 

### 1. 기본 기능
 

- [사용자 인증 및 권한 처리](./md/auth.md)
- [중고거래 중개하기](./md/item.md)
- [쇼핑몰 운영하기](./md/shop.md)

### 2. 추가 기능
- 결제 시스템



## 📌 목차
