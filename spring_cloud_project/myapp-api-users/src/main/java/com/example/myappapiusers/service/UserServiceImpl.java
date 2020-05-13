package com.example.myappapiusers.service;

import com.example.myappapiusers.data.User1Entity;
import com.example.myappapiusers.data.UsersRepository;
import com.example.myappapiusers.shared.UserDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.UUID;

@Service
public class UserServiceImpl implements UsersService{
    UsersRepository repository;
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserServiceImpl(UsersRepository repository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.repository = repository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }



    @Override
    public UserDto createUser(UserDto userDetails){

        userDetails.setUserId(UUID.randomUUID().toString());

        userDetails.setEncryptedPassword(bCryptPasswordEncoder.encode(userDetails.getPassword()));

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        User1Entity userEntity = modelMapper.map(userDetails, User1Entity.class);
        userEntity.setEncryptedPassword("test encrypted password");


        repository.save(userEntity);

        UserDto returnValue = modelMapper.map(userEntity, UserDto.class);
        return returnValue;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User1Entity userEntity = repository.findByEmail(email);

        if(userEntity == null){
            throw new UsernameNotFoundException(email);
        }

        return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(),
                true, true, true, true, new ArrayList<>());
    }

    @Override
    public UserDto getUserDetailsByEmail(String email) {
        User1Entity userEntity = repository.findByEmail(email);

        if(userEntity == null){
            throw new UsernameNotFoundException(email);
        }
        //UserEntity -> UserDto (using ModelMapper)
        return new ModelMapper().map(userEntity, UserDto.class);
    }
}
