package com.gaurav.LMS.JWT;

import com.gaurav.LMS.Service.CustomUserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final CustomUserService customUserService;
    private final JwtUtils jwtUtils;
    public JwtAuthenticationFilter(
            JwtUtils jwtUtils,
            CustomUserService customUserService) {
        this.customUserService = customUserService;
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestPath = request.getServletPath();
        log.info(requestPath);
        log.info(request.toString());
        if (requestPath.startsWith("/api/auth") ||
                requestPath.startsWith("/oauth2") ||
                requestPath.startsWith("/login/oauth2") ||
                requestPath.equals("/favicon.ico")) {
            filterChain.doFilter(request, response);
            return;
        }
        String jwtToken = null;
        // 1. Try Authorization header
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer "))
            jwtToken = authorizationHeader.substring(7);
        //validate jwt token
        if(jwtToken != null) {
            try {
                String username = this.jwtUtils.extractUserName(jwtToken);
                UserDetails userDetails = this.customUserService.loadUserByUsername(username);
                if(jwtUtils.validateToken(jwtToken, userDetails) && SecurityContextHolder
                        .getContext().getAuthentication() == null) {
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            }
            catch (Exception exception) {
                log.warn("Authentication failed {}", exception.getMessage());
            }
        }
        filterChain.doFilter(request, response);
    }
}
