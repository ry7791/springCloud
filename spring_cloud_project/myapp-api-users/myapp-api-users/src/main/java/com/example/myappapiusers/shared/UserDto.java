package com.example.myappapiusers.shared;

import lombok.Data;

@Data
public class UserDto {
    private String firstName;
    private String lastName;
    private String email;
    private String password;

    private String userId;
    private String encryptedPassword;

}
