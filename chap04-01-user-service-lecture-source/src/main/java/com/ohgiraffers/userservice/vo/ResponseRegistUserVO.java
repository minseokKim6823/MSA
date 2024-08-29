package com.ohgiraffers.userservice.vo;

import lombok.Data;

@Data
public class ResponseRegistUserVO {
    private String email;
    private String name;
    private String userId;      // 회원가입 이후 생성된 회원 고유번호
}
