package com.gaurav.LMS.DTO;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class SignInDTO {
    private String email;
    private String password;
}
