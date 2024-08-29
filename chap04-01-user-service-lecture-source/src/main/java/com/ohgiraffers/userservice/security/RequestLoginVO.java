package com.ohgiraffers.userservice.security;

import lombok.Data;

@Data
public class RequestLoginVO {
    private String email;
    private String pwd;
}
