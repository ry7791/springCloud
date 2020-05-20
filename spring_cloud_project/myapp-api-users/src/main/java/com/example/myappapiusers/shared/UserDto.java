package com.example.myappapiusers.shared;

import com.example.myappapiusers.model.AlbumResponseModel;
import lombok.Data;

import java.util.List;

@Data
public class UserDto {
    private String firstName;
    private String lastName;
    private String email;
    private String password;

    private String userId;
    private String encryptedPassword;

    private List<AlbumResponseModel> albums;
}
