package com.gaurav.LMS.DTO;

import lombok.Builder;
import lombok.Data;

@Builder @Data
public class ForgotPasswordRequest {
    String forgotPasswordToken;
    String newPassword;
}
