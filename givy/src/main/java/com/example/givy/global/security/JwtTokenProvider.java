package com.example.givy.global.security;

import com.example.givy.domain.user.enums.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value; //Lombok 말고 이 Value 어노테이션을 써야 application.yml에서 긁어올 수 있음.
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final SecretKey secretKey; // 토큰 서명 키
    private final long validationTime; // 토큰 만료 시간(ms) (1000ms = 1s -> 360000ms = 360s = 6m)


    //@Value = application.yml에서 값을 읽어옴.
    public JwtTokenProvider(
            @Value("${spring.jwt.secret}") String secret,
            @Value("${spring.jwt.expiration-ms}") long validationTime
    ){
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.validationTime = validationTime;
    }

    /** Access Token 생성
    로그인 성공 시 userId, role을 JWT에 넣고 토큰 문자열 생성하기.
    header / payload / signature 자동 생성
     */
    public String createToken(Long userId, Role role){

        Date now = new Date(); // 현재 시간 계산
        Date expiry = new Date(now.getTime() + validationTime); // 만료 시간 계산

        //JWT 생성 - Jwts.builder()로 Token 제작
        //subject(userId), role claim 넣기 -> secretKey로 서명 -> .compact()로 문자열 완성
        return Jwts.builder()
                .setSubject(userId.toString()) //JWT 표준 claim 중 하나인 sub(subject)를 설정 - "이 토큰이 누구의 토큰인가"
                .claim("role", role.name()) //payload에 custom claim 추가, JWT 표준이 아닌 “사용자 정의 정보”
                .setIssuedAt(now) //토큰이 언제 만들어졌는지 기록하는 표준 claim
                .setExpiration(expiry) //토큰 만료 시간 기록하는 표준 claim
                .signWith(secretKey, SignatureAlgorithm.HS256) //JWT signature(서명) 생성 단계
                .compact(); //header, payload, signature를 모두 합쳐 최종 JWT 문자열을 만들어주는 함수
    }

    /** <토큰 검증을 위한 파싱 + 검증함수>
     payload는 그냥 하나의 JSON처럼 생긴 Claims 묶음
     */

    private Claims parseClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token) //-> 여기서 새 시그니처 만들고, 토큰 검증. . .다 함.
                .getBody();
    }

    public boolean validateToken(String token){
        try{
            parseClaims(token);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    /* userId랑 role 반환 */
    public Long getUserId(String token){
        return Long.parseLong(parseClaims(token).getSubject());
    }
    public Role getRole(String token){
        return Role.valueOf(parseClaims(token).get("role", String.class));
    }

}
