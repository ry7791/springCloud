package com.example.myappapiusers.security;


import com.example.myappapiusers.model.LoginRequestModel;
import com.example.myappapiusers.service.UsersService;
import com.example.myappapiusers.shared.UserDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.core.env.Environment;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private UsersService usersService;
    private Environment env;



    @Autowired
    public AuthenticationFilter(UsersService usersService, Environment env, AuthenticationManager authenticationManager) {
        this.usersService = usersService;
        this.env = env;
        super.setAuthenticationManager(authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try{
            LoginRequestModel creds = new ObjectMapper()
                    .readValue(request.getInputStream(), LoginRequestModel.class);

            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            creds.getEmail(),
                            creds.getPassword(),
                            new ArrayList<>()
                    )
            );
        }catch (IOException ex){
            throw new RuntimeException();
        }

    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res,
                                            FilterChain chain,
                                            Authentication authResult) throws  IOException, ServletException{
        // 비밀번호는 안나옴
        // System.out.println(authResult);

        // generate token
        String email = ((User)authResult.getPrincipal()).getUsername();
        // 한번 더 접속해서 DB 정보를 가져옴
        UserDto userDetail = usersService.getUserDetailsByEmail(email);

        // compact 호출해야 토큰 생성 됨
        String token = Jwts.builder()
                .setSubject(userDetail.getUserId())
                .setExpiration(new Date(System.currentTimeMillis()
                        + Long.parseLong(env.getProperty("token.expiration_time"))))
                .signWith(SignatureAlgorithm.HS512, env.getProperty("token.secret"))
                .compact();

        res.addHeader("token", token);
        res.addHeader("userId", userDetail.getUserId());
    }
}