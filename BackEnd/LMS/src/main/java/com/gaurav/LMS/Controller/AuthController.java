package com.gaurav.LMS.Controller;

import com.gaurav.LMS.DTO.ForgotPasswordRequest;
import com.gaurav.LMS.DTO.SignInDTO;
import com.gaurav.LMS.JWT.JwtUtils;
import com.gaurav.LMS.Service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserService userService;
    public AuthController(
            UserService userService,
            JwtUtils jwtUtils,
            AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userService = userService;
    }
    @PostMapping("sign-in")
    public ResponseEntity<?> signIn(@RequestBody SignInDTO signInDTO) {
        try {
            Authentication authentication = authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(signInDTO.getEmail(), signInDTO.getPassword())
            );
            log.info("Authentication Completed");
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String jwtToken = this.jwtUtils.generateJwtToken(userDetails);
            // 🔥 Attach cookie via ResponseEntity, not raw HttpServletResponse
            return ResponseEntity.ok().body(Map.of(
                    "message", "Sign-In successful",
                    "response", jwtToken
            ));
        }
        catch (BadCredentialsException badCredentialsException) {
            return new ResponseEntity<>(Map.of(
                    "message", "Unauthorized Access",
                    "response", badCredentialsException.getMessage()
            ),HttpStatus.UNAUTHORIZED);
        }
        catch (Exception exception) {
            return new ResponseEntity<>(Map.of(
                    "message", "something went wrong",
                    "response", exception.getMessage()
            ),HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPasswordRequest(@RequestParam(name = "email") String email) {
        try {
            String message = this.userService.forgotPasswordRequest(email);
            return new ResponseEntity<>(Map.of(
                    "message", message
            ),HttpStatus.OK);
        }
        catch (AccessDeniedException accessDeniedException) {
            return new ResponseEntity<>(Map.of(
                    "message", "something went wrong",
                    "response", accessDeniedException.getMessage()
            ),HttpStatus.FORBIDDEN);
        }
        catch (Exception exception) {
            return new ResponseEntity<>(Map.of(
                    "message", "something went wrong",
                    "response", exception.getMessage()
            ),HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping("/reset-password")
    public ResponseEntity<?> updatePassword(
            @RequestParam(name = "email") String email,
            @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        try {
            String message = this.userService.updatePasswordRequest(email, forgotPasswordRequest);
            return new ResponseEntity<>(Map.of(
                    "message", message
            ),HttpStatus.OK);
        }
        catch (AccessDeniedException accessDeniedException) {
            return new ResponseEntity<>(Map.of(
                    "message", "something went wrong",
                    "response", accessDeniedException.getMessage()
            ),HttpStatus.FORBIDDEN);
        }
        catch (Exception exception) {
            return new ResponseEntity<>(Map.of(
                    "message", "something went wrong",
                    "response", exception.getMessage()
            ),HttpStatus.BAD_REQUEST);
        }
    }
}
