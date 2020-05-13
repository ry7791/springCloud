package com.example.myappapiusers.model;

import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class CreateUserRequestModel {

    @NotNull
    @Size(min=2)
    private String firstName;
    @NotNull
    @Size(min=2)
    private String lastName;
    @NotNull
    @Email
    private String email;
    @NotNull
    @Size(min=2, max=16)
    private String password;
}
