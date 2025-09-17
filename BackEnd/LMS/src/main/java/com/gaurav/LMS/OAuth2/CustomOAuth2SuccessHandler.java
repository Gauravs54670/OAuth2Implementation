package com.gaurav.LMS.OAuth2;

import com.gaurav.LMS.Entity.UserEntity;
import com.gaurav.LMS.JWT.JwtUtils;
import com.gaurav.LMS.Repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Slf4j
@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    public CustomOAuth2SuccessHandler(
            UserRepository userRepository,
            JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
    }
    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws IOException, ServletException {
        log.info("Success handler executed");
        //Extract user info from OAuth2User and generate token
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        UserDetails userDetails = new OAuth2Adapter(oAuth2User);
        String jwtToken = this.jwtUtils.generateJwtToken(userDetails);
        //check if the user is already present in the db
        boolean exist = this.userRepository.findByEmail(email).isPresent();
        if(!exist) {
            UserEntity user = UserEntity.builder()
                    .email(email)
                    .build();
            this.userRepository.save(user);
        }
        // ✅ Redirect with token in query param
        String redirectUrl = "http://127.0.0.1:5500/OAuthSuccess.html?token=" + jwtToken;
        response.sendRedirect(redirectUrl);

        log.info("Redirected to {}", redirectUrl);
    }
}
