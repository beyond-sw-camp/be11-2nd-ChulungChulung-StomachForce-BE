package com.beyond.StomachForce.youngjae.common.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class JwtAuthFilter extends GenericFilter {
    private final HttpServletResponse httpServletResponse;
    @Value("${jwt.secretKey}")
    private String secretKey;

    public JwtAuthFilter(HttpServletResponse httpServletResponse) {
        this.httpServletResponse = httpServletResponse;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)throws IOException, ServletException {
        //      token검증
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        //      예외처리를 위해 응답 선언
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        String bearerToken = httpServletRequest.getHeader("Authorization");
        //      token 분해 후 Authentication 객체 생성
        //      token이 있는데 잘못된 경우 (), token이 없는 경우
        try {
            if (bearerToken != null) {
                //      Bearer 를 관례적으로 붙이는데, 안붙였을 때 에러 발생
                if (!bearerToken.substring(0, 7).equals("Bearer ")) {
                    throw new AuthenticationServiceException("Bearer 형식이 아닙니다.");
                }
                String token = bearerToken.substring(7);
                //      token 검증 및 claims 추출 (만료된 토큰을 넣으면 여기서 500 error을 출력함
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(secretKey)
                        .build()
                        .parseClaimsJws(token)
                        .getBody();
                //      Authentication 객체 생성
                List<GrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority("ROLE_" + claims.get("role")));
                Authentication authentication =
                        new UsernamePasswordAuthenticationToken(claims.getSubject(), "password", authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);

            }
            //      다시 filterChain으로 되돌아 가는 로직
            chain.doFilter(request, response);
        }catch (Exception e) {
            log.error(e.getMessage());
            httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            httpServletResponse.getWriter().write("token is invalid");
        }

    }
}
