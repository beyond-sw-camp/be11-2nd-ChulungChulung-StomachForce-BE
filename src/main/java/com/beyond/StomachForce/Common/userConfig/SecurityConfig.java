package com.beyond.StomachForce.Common.userConfig;

import com.beyond.StomachForce.Common.Auth.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    private final JwtAuthFilter authFilter;

    public SecurityConfig(JwtAuthFilter authFilter) {
        this.authFilter = authFilter;
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .cors(c->c.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)

                .authorizeHttpRequests(a -> a.requestMatchers("/user/idValid","/user/nickNameValid","/user/create", "/user/doLogin","/",

                        "/user/refresh-token","/restaurant/create","/restaurant/login","/restaurant/refresh-token","restaurant/detail/{id}",
                        "/restaurant/list","/menu/list/{restaurantId}","/restaurant/{restaurantId}/review/list","/restaurant/info/list/{id}",
                        "/list/korean","/list/chinese","/list/western","/list/japanese","/list/fusion",
                        "/service/list", "/service/answer/create", "/service/answer/update/{answerId}",
                        "/service/answer/delete/{answerId}","user/me", "/report/create", "/report/update/{reportId}",
                        "/report/delete/{reportId}", "/report/admin-comment/{reportId}",
                        "/service/post/{postId}", "/service/answer/{answerId}","/restaurant/top-favorites","/user/top-influencers",
                        "/announcement/event/ongoing","/restaurant/categories","/post/postList","announcement/list","announcement/detail/{id}","/post/postList",
                        "/user/userInfo","/post/getLike/{postId}","/post/getLike/{postId}","/user/google/doLogin"

                        ).permitAll().anyRequest().authenticated())
                .build();
    }

    private CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000","www.naver.com"));
        configuration.setAllowedMethods(Arrays.asList("*"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",configuration);
        return source;
    }

    @Bean
    public PasswordEncoder makePassword(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
