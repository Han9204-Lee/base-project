package com.example.entity;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class User extends BaseEntity{
    private Long id;
    private String loginId;
    private String password;
    private String userName;
    private List<Roles> roles;
    private String refreshToken;
}
