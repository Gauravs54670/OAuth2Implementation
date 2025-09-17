package com.gaurav.LMS.Service;

import com.gaurav.LMS.DTO.ForgotPasswordRequest;
import com.gaurav.LMS.DTO.SignUpDTO;
import com.gaurav.LMS.DTO.UserDTO;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    public String registerUser(SignUpDTO signUpDTO);
    public UserDTO getDetails(String email);
    public UserDTO updateDetails(String email, UserDTO userDTO);
    public String forgotPasswordRequest(String email);
    public String updatePasswordRequest(String email, ForgotPasswordRequest forgotPasswordRequest);
    public String changePassword(String email,String currentPassword, String newPassword);
}
