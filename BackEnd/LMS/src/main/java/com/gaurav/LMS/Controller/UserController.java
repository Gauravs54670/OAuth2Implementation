package com.gaurav.LMS.Controller;

import com.gaurav.LMS.DTO.ForgotPasswordRequest;
import com.gaurav.LMS.DTO.UserDTO;
import com.gaurav.LMS.Service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping("/get-details")
    public ResponseEntity<?> getDetails() {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            UserDTO user = this.userService.getDetails(email);
            return new ResponseEntity<>(Map.of(
                    "message", "details fetched successfully",
                    "response", user
            ), HttpStatus.OK);
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
    @PutMapping("/update-details")
    public ResponseEntity<?> updateDetails(@RequestBody UserDTO userDTO) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            UserDTO user = this.userService.updateDetails(email, userDTO);
            return new ResponseEntity<>(Map.of(
                    "message","User updated successfully",
                    "response", user
            ),HttpStatus.OK);
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
    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestParam(name = "newPassword") String newPassword,
            @RequestParam(name = "oldPassword") String oldPassword) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            String message = this.userService.changePassword(email,oldPassword, newPassword);
            return new ResponseEntity<>(Map.of(
                    "response", message
            ),HttpStatus.OK);
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
}
