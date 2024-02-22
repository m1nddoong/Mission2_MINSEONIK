# 사용자 인증 및 권한 처리
세션과 JWT 간단한 비교
- 세션
  - HTML 폼 기반의 로그인 페이지를 사용하여 인증을 처리하는 경우
- JWT
  - Restful API 통신에서의 인증 방식
  - 토큰에 사용자 인증 및 권한 정보 외에도 Claim 을 통한 사용자 정보를 포함시킬 수 있다.

JWT 발급
- `JwtTokenUtils.java`
  - JWT 토큰 생성에 사용되는 메서드를 포함하는 유틸리티 클래스
  - 주로 사용자의 인증 정보를 바탕으로 JWT 토큰을 생성하는데 사용된다.
  - generateToken() 메서드는 UserDetails 객체를 입력으로 받아 해당 사용자를 나타내는 JWT 토큰을 생성한다.
  
JWT를 이용한 인증 - Bearer Token 인증 방식
- JWT 를 비롯한 토큰을 활용해서 인증을 진행할 경우, HTTP 요청을 보낼 때 Authorization Header 에  `Bearer {token 값}` 의 형태로 추가해 보내게 된다.
- 즉 서버에서는 들어온 HTTP 요청에 Authorization 이라는 헤더가 존재하는지, 존재한다면 거기에 담긴 값이 Bearer 로 시작하는지, 그리고 담겨있는 Token 이 유효한지 판단하는 JwtFilter 를 만들어야한다.
  - JWT 유효한지 알아보는 메서드 : `JwtTokenUtils.validate()`
  - `OncePerRequestFilter` 추상클래스로 필터 구현 : 사용자가 포함시킨 JWT의 유효성을 확인하고 Spring Security의 필터를 사용하여 사용자가 인증되었는지 여부를 판단

