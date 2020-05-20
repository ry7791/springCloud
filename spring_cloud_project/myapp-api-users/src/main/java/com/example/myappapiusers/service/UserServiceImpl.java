package com.example.myappapiusers.service;

import com.example.myappapiusers.client.AlbumServiceClient;
import com.example.myappapiusers.data.User1Entity;
import com.example.myappapiusers.data.UsersRepository;
import com.example.myappapiusers.model.AlbumResponseModel;
import com.example.myappapiusers.shared.UserDto;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class UserServiceImpl implements UsersService {
    UsersRepository repository;
    BCryptPasswordEncoder bCryptPasswordEncoder;
    //    RestTemplate restTemplate;
    AlbumServiceClient albumServiceClient;
    Environment env;

    @Autowired   //명시된 class가 두개 이상이면? -> Qualifier 이용해서 지정한 곳 가져오자
    public UserServiceImpl(UsersRepository repository,
                           BCryptPasswordEncoder bCryptPasswordEncoder, Environment env,AlbumServiceClient albumServiceClient) {
        this.repository = repository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        //this.restTemplate = restTemplate;
        this.albumServiceClient = albumServiceClient;
        this.env = env;
    }

    @Override
    public UserDto createUser(UserDto userDetails) {
        // UserDto -> UserEntity
        userDetails.setUserId(UUID.randomUUID().toString());

        userDetails.setEncryptedPassword(
                bCryptPasswordEncoder.encode(userDetails.getPassword())
        );

        ModelMapper modelMapper =  new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(
                MatchingStrategies.STRICT);
        User1Entity userEntity = modelMapper.map(userDetails, User1Entity.class);

        repository.save(userEntity);

        UserDto returnValue = modelMapper.map(userEntity, UserDto.class);
        return returnValue;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User1Entity userEntity = repository.findByEmail(email);

        if (userEntity == null) {
            throw new UsernameNotFoundException(email);
        }

        return new User(userEntity.getEmail() , userEntity.getEncryptedPassword(),
                true, true, true,
                true, new ArrayList<>());
    }

    @Override
    public UserDto getUserDetailsByEmail(String email) {
        User1Entity userEntity = repository.findByEmail(email);

        if (userEntity == null) {
            throw new UsernameNotFoundException(email);
        }
        // UserEntity -> UserDto (using ModelMapper)
        return new ModelMapper().map(userEntity, UserDto.class);
    }

    @Override
    public UserDto getUserByUserID(String userId){
        User1Entity userEntity = repository.findByUserId(userId);

        if (userEntity == null) {
            throw new UsernameNotFoundException(userId);
        }

        UserDto userDto = new ModelMapper().map(userEntity, UserDto.class);
        //call - > albums microservice
//        ResponseEntity<List<AlbumResponseModel>> albumsListResponse =
//       restTemplate.exchange(
//               String.format("http://albums-ws/users/%s/albums",userId),
//               HttpMethod.GET,
//               null,
//               new ParameterizedTypeReference<List<AlbumResponseModel>>() {
//       });
//
//        List<AlbumResponseModel> albumsList = albumsListResponse.getBody();

// try catch
//        List<AlbumResponseModel> albumsList = null;
//        try {
//            albumsList = albumServiceClient.getAlbums(userId);
//        }catch (FeignException ex){
//            log.error(ex.getLocalizedMessage());
//        }

        log.info("Before calling albums microservice");
        List<AlbumResponseModel> albumsList = albumServiceClient.getAlbums(userId);
        log.info("After calling albums microservice");

        userDto.setAlbums(albumsList);

        return userDto;
    }
}