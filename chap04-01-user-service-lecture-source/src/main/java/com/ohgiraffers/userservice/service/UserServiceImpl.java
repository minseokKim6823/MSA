package com.ohgiraffers.userservice.service;

import com.ohgiraffers.userservice.aggregate.UserEntity;
import com.ohgiraffers.userservice.dto.UserDTO;
import com.ohgiraffers.userservice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class UserServiceImpl implements UserService{

    UserRepository userRepository;
    ModelMapper modelMapper;

    /* 설명. security 모듈 추가 후 암호화를 위해 BCryptPasswordEncoder bean 주입*/
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,ModelMapper modelMapper,BCryptPasswordEncoder bCryptPasswordEncoder){
        this.userRepository=userRepository;
        this.modelMapper =modelMapper;
        this.bCryptPasswordEncoder =bCryptPasswordEncoder;
    }
    @Override
    @Transactional
    public void registUser(UserDTO userDTO) {

        /* 설명. 회원 가입할 인원에게 고유 아이디 생성*/
        userDTO.setUserId(UUID.randomUUID().toString());

        /* 설명. 경우에 따라 ModelMapper는 자의적인 판단으로 필드끼리 매핖ㅇ하는 경우가 있어 정확히 일치되게 매칭할려면 추가할 속성 */
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserEntity userEntity = modelMapper.map(userDTO, UserEntity.class);
        log.info("Service 계층에서 DTO -> Entity: {}",userEntity);

        /* 설명. BCryptPasswordEncoder 주입 후 암호화 (평문 -> 다이제스트) */
        userEntity.setEncryptedPwd(bCryptPasswordEncoder.encode(userDTO.getPwd()));

        userRepository.save(userEntity);
    }

    /* 설명. 로그인 시 security가 자동으로 호출하는 메소드 */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        /* 설명. 넘어온 email이 사용자가 입력한 id로써 email로 회원을 조회하는 쿼리 메소드 작성*/
        UserEntity loginUser = userRepository.findByEmail(email);

        if(loginUser == null){
            throw new UsernameNotFoundException(email + "이메일 아이디의 유저는 존재사지 않습니다.");
        }

        /* 설명. 사용자의 권한들을 가져왔다는 가정*/
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_ENTERPRISE"));

        return new User(loginUser.getEmail(), loginUser.getEncryptedPwd(),
                true,true,true,true,
                grantedAuthorities);
    }
}
