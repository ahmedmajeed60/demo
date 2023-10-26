package com.example.demo.security;

import com.example.demo.config.ApplicationProperties;
import com.example.demo.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@EnableMethodSecurity
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final IUserService usersService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ApplicationProperties applicationProperties;

    @Autowired
    public WebSecurityConfig(IUserService usersService, BCryptPasswordEncoder bCryptPasswordEncoder,
                             ApplicationProperties applicationProperties) {
        this.usersService = usersService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.applicationProperties = applicationProperties;
    }

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);

        authenticationManagerBuilder.userDetailsService(usersService)
                .passwordEncoder(bCryptPasswordEncoder);

        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();

        AuthenticationFilter authenticationFilter =
                new AuthenticationFilter(usersService, authenticationManager, applicationProperties);
        authenticationFilter.setFilterProcessesUrl(applicationProperties.getLoginEndpoint());

        return http.authorizeHttpRequests((authz) -> authz
                        .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/actuator/**")).permitAll()
                )
                .addFilter(new AuthorizationFilter(authenticationManager, applicationProperties))
                .addFilter(authenticationFilter)
                .authenticationManager(authenticationManager)
                .build();
    }
}

