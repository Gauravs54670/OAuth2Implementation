package com.gaurav.LMS.Controller;

import com.gaurav.LMS.DTO.SignUpDTO;
import com.gaurav.LMS.Service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/public")
public class PublicController {

    private final UserService userService;
    public PublicController(UserService userService) {
        this.userService = userService;
    }
    @PostMapping("/register-user")
    public ResponseEntity<?> registerUser(@RequestBody SignUpDTO signUpDTO) {
        try {
            String message = this.userService.registerUser(signUpDTO);
            log.info("User Registered");
            return new ResponseEntity<>(Map.of(
                    "message", message
            ), HttpStatus.OK);
        }
        catch (Exception exception) {
            return new ResponseEntity<>(Map.of(
                    "message", "error in registering user",
                    "response", exception.getMessage()
            ),HttpStatus.BAD_REQUEST);
        }
    }

}
