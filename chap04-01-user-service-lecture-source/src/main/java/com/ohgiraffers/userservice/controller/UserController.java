package com.ohgiraffers.userservice.controller;

import com.ohgiraffers.userservice.dto.UserDTO;
import com.ohgiraffers.userservice.service.UserService;
import com.ohgiraffers.userservice.vo.Hello;
import com.ohgiraffers.userservice.vo.RequestResistUserVO;
import com.ohgiraffers.userservice.vo.ResponseRegistUserVO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private Environment env;
    private Hello hello;
    private ModelMapper modelMapper;
    private UserService userService;

    @Autowired
    public UserController(Environment env,
                          Hello hello,
                          ModelMapper modelMapper,
                          UserService userService) {
        this.env = env;
        this.hello = hello;
        this.modelMapper = modelMapper;
        this.userService = userService;
    }

    @GetMapping("/health")
    public String status() {
        return "I'm Working in User Service " + env.getProperty("local.server.port");
    }

    /* 설명. 해당 마이크로 서비스가 application.yml에 설정 값이 제대로 들어 있는지 확인(feat. @Value) */
    @GetMapping("/welcome")
    public String welcome() {
        return hello.getMessage();
    }

    /* 설명. 로그인 기능(feat. security 모듈 활용) 전에 회원가입 기능 만들기 */
    @PostMapping("users")
    public ResponseEntity<ResponseRegistUserVO> registUser(@RequestBody RequestResistUserVO newUser) {
        UserDTO userDTO = modelMapper.map(newUser, UserDTO.class);

        userService.registUser(userDTO);

//        return ResponseEntity.status(HttpStatus.OK).build();
        ResponseRegistUserVO responseUser =modelMapper.map(userDTO, ResponseRegistUserVO.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseUser);
    }

}






