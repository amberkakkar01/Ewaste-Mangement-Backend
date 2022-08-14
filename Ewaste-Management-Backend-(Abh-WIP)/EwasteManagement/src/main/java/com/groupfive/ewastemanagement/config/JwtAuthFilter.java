package com.groupfive.ewastemanagement.config;


import com.groupfive.ewastemanagement.exception.InvalidTokenException;
import com.groupfive.ewastemanagement.helper.JwtUtil;
import com.groupfive.ewastemanagement.service.jwtuserservice.JWTUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final JWTUserDetailsService customUserDetailsService;

    @Autowired
    public JwtAuthFilter(JwtUtil jwtUtil, JWTUserDetailsService customUserDetailsService) {
        this.jwtUtil = jwtUtil;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestTokenHeader = request.getHeader("Authorization");
        String userName = null;
        String jwtToken;

        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);

            try {
                userName = jwtUtil.getUsernameFromToken(jwtToken);
            } catch (InvalidTokenException e) {
                logger.error("Invalid Token");
            }

            UserDetails userDetails = this.customUserDetailsService.loadUserByUsername(userName);

            if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            } else {
                logger.error("UserName null");
            }
        }
        filterChain.doFilter(request, response);
    }
}
