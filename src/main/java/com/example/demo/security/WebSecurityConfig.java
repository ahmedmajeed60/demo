package com.example.demo.security;

import com.example.demo.service.ICustomerService;
import com.example.demo.service.ITokenService;
import com.example.demo.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.security.config.Customizer.withDefaults;

@EnableMethodSecurity
@EnableWebSecurity
@Configuration
public class WebSecurityConfig {

    private final ICustomerService customerService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ITokenService tokenService;

    @Autowired
    public WebSecurityConfig(ICustomerService customerService, BCryptPasswordEncoder bCryptPasswordEncoder,
                             ITokenService tokenService) {
        this.customerService = customerService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.tokenService = tokenService;
    }

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(customerService)
                .passwordEncoder(bCryptPasswordEncoder);
        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();
        return http.authorizeHttpRequests(auth -> auth
                        .requestMatchers(new AntPathRequestMatcher(Constant.ACTUATOR_URL)).permitAll()
                        .requestMatchers(new AntPathRequestMatcher(Constant.H2_CONSOLE_URL)).permitAll()
                        .requestMatchers(new AntPathRequestMatcher(Constant.OPEN_API_URL)).permitAll()
                        .requestMatchers(new AntPathRequestMatcher(Constant.SWAGGER_URL)).permitAll()
                        .requestMatchers(new AntPathRequestMatcher(Constant.LOGIN_URL)).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilter(new AuthorizationFilter(authenticationManager, tokenService, customerService))
                .authenticationManager(authenticationManager)
                .cors(withDefaults())
                .csrf((csrf) -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers(new AntPathRequestMatcher(Constant.H2_CONSOLE_URL));
    }
}

