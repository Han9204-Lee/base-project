package com.example.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Roles extends BaseEntity{
    private long id;
    private String name;

    public Roles(String name) {
        this.name = name;
    }
}
