# 결제 시스템 (Toss Payments)

<details>
<summary>요구 사항 체크 ✔️</summary>
<div markdown="1">

사용자가 서비스 사용중 두가지 상황에서 결제를 진행하도록 서비스를 수정한다.

- 쇼핑몰 상품 구매
    - 사용자가 상품의 구매 요청을 하는 시점에, 결제를 진행한다.
    - 결제가 이뤄지면 자동으로 재고가 갱신된다.
    - 주인은 구매 요청에 대하여 구매 요청을 수락할 수 있다. 이후엔 구매 취소가 불가능하다.
    - 정당한 사유가 있으면 구매 요청을 거절할 수 있다. 사유는 관리자가 확인 가능하다.
    - 구매 요청이 수락되기 전에는 사용자가 구매 요청을 취소할 수 있다.
    - 구매 요청이 취소될 경우 사용자는 구매에 결제된 금액을 환불받는다.


- ~~중고거래 물품 구매 확정~~
    - ~~물품을 등록한 사용자가 구매를 수락하면, 구매 제안을 등록한 사용자는 구매 확정을 하기 위해 결제를 진행한다.~~
        - ~~결제된 돈은 위탁금의 개념으로, 물품을 등록한 사용자에게 바로 전송되지 않는다.~~
        - ~~결제가 정상적으로 이뤄질 때, 구매 제안의 상태는 **확정**이 된다. 물품의 상태는 판매 완료로 전환되지 않도록 수정한다.~~
    - ~~구매 제안을 한 사용자와 물품을 등록한 사용자는 이후 실제로 물품을 주고받는다.~~
        - ~~물품을 주고받은 뒤, 각 사용자는 일정 기간 안에 거래의 상태(완료, 실패)를 서비스에 알려야 한다.~~
        - ~~양쪽 사용자가 거래 완료를 서비스에 알리면, 물품의 상태가 판매 완료로 전환되며, 물품을 등록한 사용자는 위탁금을 전송받는다(실제로 전송받는 부분은 미구현)~~
        - ~~어느 한쪽 사용자가 완료를 하지 못한 상태의 구매 제안은 관리자가 확인할 수 있다.~~
            - ~~이때 오래된 구매 제안부터 확인이 된다.~~

</div>
</details>





### 막힌 부분 : 의문의 CORS 오류
이전에 배웠던 tosspayment 챕터에서 `items.html`, `item.html` 등을 제공하여 클라이언트가 상품을 결제 요청을 보낼 수 수 있도록 프론트를 구성했었다.

클라이언트가 토스페이먼츠와 주고받은 결제정보를 백엔드로 전달하는 과정을 진행하면 된다.

하지만, HTML 파일 파일이 Spring Security 떄문인지, 웹 브라우저의 기본적인 보안 문제 떄문인지 요청의 대한 응답이 제대로 돌아오지 않았다.  

![toss_1.png](img_toss%2Ftoss_1.png)

`사러가기` 를 눌렀을떄 다음 결제 페이지로 넘어가지지 않는다.

CORS 문제의 가능성이 있어 아래의 코드를 삽입하였으나 실패.

```java
@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {
    // 메서드의 결과를 Bean 객체로 관리해주는 어노테이션
    private final JwtTokenUtils jwtTokenUtils;
    private final UserDetailsManager manager;


    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http // 인증과 권한 관련 설정을 적용할 수 있는 객체
    ) throws Exception {
        http
                .cors(c -> {
                    c.configurationSource(corsConfigurationSource());
                })
                
                ...
    }
    
    ...

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("*"); // 모든 도메인에서의 요청을 허용한다.
        configuration.addAllowedMethod("*"); // 모든 HTTP 메소드를 허용한다.
        configuration.addAllowedHeader("*"); // 모든 헤더를 허용한다.
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 경로에 대해 CORS 설정을 적용한다.
        return source;
    }
}
```

스프링 시큐리티가 static 파일의 경로를 허용할떄, 해당 정작 파일들을 인식하지 못하는 형식의 문제일까 싶어 URL 패턴을 바꿔 보았지만 실패

![toss_2.png](img_toss%2Ftoss_2.png)


toss payment 구현은 여기서 더 나아가지 못했다.😰


