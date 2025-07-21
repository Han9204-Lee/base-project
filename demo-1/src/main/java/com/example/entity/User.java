package com.example.entity;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class User extends BaseEntity{
    private Long id;
    private String userId;
    private String password;
    private String userName;
    private List<String> roles;
    private String refreshToken;
}
