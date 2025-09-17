package com.gaurav.LMS.DTO;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class UserDTO {
    private String email;
    private String contact;
    private String firstName;
    private String lastName;
    private String bio;
}
