package com.ohgiraffers.userservice.vo;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class Hello {

    @Value("{hello.message}")
    private String message;
}
