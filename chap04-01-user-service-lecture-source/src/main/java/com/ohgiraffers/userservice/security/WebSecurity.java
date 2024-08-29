package com.ohgiraffers.userservice.security;


import com.ohgiraffers.userservice.service.UserService;
import jakarta.servlet.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurity {
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private UserService userService;
    private Environment env;

    @Autowired
    public WebSecurity(BCryptPasswordEncoder bCryptPasswordEncoder
    ,UserService userService,Environment env){
        this.bCryptPasswordEncoder =bCryptPasswordEncoder;
        this.userService = userService;
        this.env=env;
    }

    /* 설명. 인가(Authoriazation) 용 메소드(인증 필터 추가) */
    @Bean
    protected SecurityFilterChain configure(HttpSecurity http) throws Exception {

        /* 설명. csrf 비활성화*/
        http.csrf((csrf)->csrf.disable());

        /* 설명. 로그인 시  추가할 authentication Manager 만들기 */
        AuthenticationManagerBuilder authenticationMangerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationMangerBuilder.userDetailsService(userService)
                        .passwordEncoder(bCryptPasswordEncoder);
        
        AuthenticationManager authenticationManager = authenticationMangerBuilder.build();

        http.authorizeHttpRequests((authz)->
                authz.requestMatchers(new AntPathRequestMatcher("/users/**")).permitAll()
                        .anyRequest().authenticated()
                // AntPathRequestMatcher parameter 뒤에 GET, POST 등을 붙일 수 있다.
                // requestMatchers().hasRole("ADMIN")으로 권한 부여 가능
        )
                /* 설명. authenticationManager 등록(UserDetails를 상속 받는 Service 계층 + BCrypt 암호화) */
                .authenticationManager(authenticationManager)
                /* 설명. session 방식을 사용하지 않음 (JWT Token 방식 사용시 설정할 내용) */
                .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));//stateless 무상태

        http.addFilter(getAuthenticationFilter(authenticationManager));

        return http.build();
    }

    private Filter getAuthenticationFilter(AuthenticationManager authenticationManager) {
        return new AuthenticationFilter(authenticationManager,userService, env);
    }

    /* 설명. 인증(Authentication) 용 메소드(인증 필터 반환) */
}
