package com.example.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ApiSecretKeys extends BaseEntity{
    private Long id;
    private String clientId;
    private String clientSecret;
    private String refreshToken;
}
