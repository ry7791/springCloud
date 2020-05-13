package com.example.myappapiusers.controller;

import com.example.myappapiusers.data.User1Entity;
import com.example.myappapiusers.exception.ResourceNotFoundException;
import com.example.myappapiusers.model.CreateUserRequestModel;
import com.example.myappapiusers.model.CreateUserResponseModel;
import com.example.myappapiusers.repository.UserRepository;
import com.example.myappapiusers.service.UsersService;
import com.example.myappapiusers.shared.UserDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UsersController {
    @Autowired
    private UserRepository repository;

    @Autowired
    UsersService userService;

    @Autowired
    Environment env;  // 랜덤값으로 설정한 port 번호를 가져오기 위해서 사용
    @GetMapping("/status/check")
    public String status(){
        return String.format("Working on port %s",env.getProperty("local.server.port"));
    }

    @PostMapping(
            consumes = {MediaType.APPLICATION_ATOM_XML_VALUE,MediaType.APPLICATION_JSON_VALUE },
            produces = {MediaType.APPLICATION_ATOM_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}
    )
    public ResponseEntity<CreateUserResponseModel> createUsers(@Valid @RequestBody CreateUserRequestModel userDetails,
                                                               HttpServletRequest req){
        System.out.println(req.getRemoteAddr());
        //CreateUserRequestModel - > UserDto
       ModelMapper modelMapper = new ModelMapper();
       modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
       UserDto userDto = modelMapper.map(userDetails, UserDto.class);
       UserDto createdDto = userService.createUser(userDto);


        //return new ResponseEntity(HttpStatus.CREATED);
       CreateUserResponseModel returnValue = modelMapper.map(createdDto, CreateUserResponseModel.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(returnValue);
   }

}
