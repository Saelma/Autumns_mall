package com.example.AutumnMall.config;

import com.example.AutumnMall.security.jwt.exception.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

// Spring Security 설정.
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationManagerConfig authenticationManagerConfig;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(formLogin -> formLogin.disable())
                .csrf(csrf -> csrf.disable())
                .cors()
                .and()
                .httpBasic(httpBasic -> httpBasic.disable())
                .authorizeRequests(authorizeRequests -> {
                    authorizeRequests
                            .antMatchers(
                                    "/members/signup", "/members/login", "/members/refreshToken"
                            ).permitAll()
                            .antMatchers("/email/**").permitAll()  // 이메일 인증 관련 경로도 모두 허용

                            .antMatchers("/uploads/**").permitAll()
                            .antMatchers("/actuator/**").permitAll()
                            .antMatchers("/members/password/**").permitAll()
                            // 접속 안 해도 볼 수 있음
                            .antMatchers(HttpMethod.GET, "/categories/**", "/products/**").permitAll()

                            // 최소 '유저' ~ '관리자'가 가능함
                            .antMatchers(HttpMethod.GET, "/**").hasAnyRole("USER", "ADMIN")
                            .antMatchers(HttpMethod.POST, "/**").hasAnyRole("USER", "ADMIN")
                            .antMatchers(HttpMethod.POST, "/report/**").hasAnyRole("USER", "ADMIN")
                            .antMatchers(HttpMethod.GET, "/report/**").hasAnyRole("USER", "ADMIN")
                            .antMatchers(HttpMethod.PATCH, "/carItems/**", "/members/**").hasAnyRole("USER", "ADMIN")
                            .antMatchers(HttpMethod.DELETE, "/cartItems/**", "/favorites/**").hasAnyRole("USER","ADMIN")

                            // 관리자만 가능
                            .antMatchers(HttpMethod.POST, "/categories/**", "/products/**").hasRole("ADMIN")
                            .antMatchers(HttpMethod.DELETE, "/payment/**", "/orders/**", "/products/**", "/categories", "/mileage", "members").hasRole("ADMIN")
                            .antMatchers(HttpMethod.PATCH, "/payment/**", "/orders/**", "/products/**", "/categories", "/mileage").hasRole("ADMIN");
                })
                .exceptionHandling(exceptionHandling -> exceptionHandling.authenticationEntryPoint(customAuthenticationEntryPoint))
                .apply(authenticationManagerConfig);

        return http.build();
    }

    // <<Advanced>> Security Cors로 변경 시도
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        // config.setAllowCredentials(true); // 이거 빼면 된다
        // https://gareen.tistory.com/66
        config.addAllowedOrigin("*");
        config.addAllowedMethod("*");
        config.setAllowedMethods(List.of("GET","POST","DELETE","PATCH","OPTION","PUT"));
        source.registerCorsConfiguration("/**", config);

        return source;
    }

    // 암호를 암호화하거나, 사용자가 입력한 암호가 기존 암호랑 일치하는지 검사할 때 이 Bean을 사용
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}


/*
BCrypt는 비밀번호를 안전하게 저장하기 위한 해시 함수입니다. BCrypt는 비밀번호 해싱을 위해 Blowfish 암호화 알고리즘을 사용하며, 암호화된 비밀번호를 저장할 때 임의의 솔트(salt)를 생성하여 비밀번호의 보안성을 높입니다.

BCrypt는 강력한 암호화 알고리즘을 사용하기 때문에 해독이 거의 불가능합니다. 이는 해커가 데이터베이스를 공격하여 해시된 비밀번호를 복원하는 것을 어렵게 만듭니다. 또한, BCrypt는 더 높은 수준의 보안성을 위해 비밀번호를 반복해서 해싱하는 기능(최소 10회 이상)을 지원합니다.

BCrypt는 Java, Ruby, Python, C#, PHP 등 다양한 프로그래밍 언어에서 사용할 수 있으며, 많은 웹 프레임워크에서 기본적으로 BCrypt를 지원하고 있습니다. 비밀번호를 안전하게 저장하기 위해서는 BCrypt와 같은 안전한 해시 함수를 사용하는 것이 좋습니다.
 */