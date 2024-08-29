package com.ohgiraffers.userservice.vo;

import lombok.Data;

/* 설명. 회원가입 시 사용자의 입력값을 받아낼 커맨드 객체용 클래스*/
@Data
public class RequestResistUserVO {
    private String email;
    private String name;
    private String pwd;             //암호화 되기 전 사용자가 입력한 평문(비밀번호)(plain text)
}
