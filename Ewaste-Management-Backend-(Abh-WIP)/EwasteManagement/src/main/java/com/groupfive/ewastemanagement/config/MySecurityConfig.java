package com.groupfive.ewastemanagement.config;

import com.groupfive.ewastemanagement.security.CustomUserDetailsService;
import com.groupfive.ewastemanagement.security.RestAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
public class MySecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    JwtAuthFilter jwtFilter;
    @Autowired
    CustomUserDetailsService customUserDetailsService;

    private static final String[] AUTH_WHITELIST = {
            // -- Swagger UI v2
            "/v2/api-docs",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**",
            // -- Swagger UI v3 (OpenAPI)
            "/v3/api-docs/**",
            "/swagger-ui/**",

            "/api/v1/user",
            "/api/v1/password/save",
            "/api/v1/password/reset",
            "/api/v1/password/change",
            "/api/v1/sign-in",
            "/api/v1/signIn/google",
            "/api/v1/refresh/token",

            "/oauth/**",
            "/actuator/**",
            "/auth/*",
            "/oauth2/*",
            "/",
            "/error",
            "/favicon.ico",
            "/*/.png",
            "/*/.gif",
            "/*/.svg",
            "/*/.jpg",
            "/*/.html",
            "/*/.css",
            "/*/.js"

    };

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().configurationSource(request -> {
                    var corSConfiguration = new CorsConfiguration();
                    corSConfiguration.setAllowedOrigins(List.of("*"));
                    corSConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    corSConfiguration.setAllowedHeaders(List.of("*"));
                    return corSConfiguration;
                }).and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .csrf()
                .disable()
                .formLogin()
                .disable()
                .httpBasic()
                .disable()
                .exceptionHandling()
                .authenticationEntryPoint(new RestAuthenticationEntryPoint())
                .and()
                .authorizeRequests()
                .mvcMatchers(AUTH_WHITELIST).permitAll()
                .anyRequest()
                .authenticated();
        http
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        http.headers().xssProtection().and().contentSecurityPolicy("script-src 'self'");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserDetailsService)
                .passwordEncoder(new BCryptPasswordEncoder());
    }


    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
