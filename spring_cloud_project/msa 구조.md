### Spring Cloud msa 구조

순서

port 8010 => eureka server

port 8012 => configuration server

port 8011 => zuul server



랜덤포트 => users-ws





- bootstrap.yml

  ```yml
  spring:
    cloud:
      config:
        uri: http://localhost:8012
        name: ConfigServer
  ```

  ​	각각의 서버에 bootstrap.yml을 넣어줘서 설정을 config Server에서 관리하자



- config의 application.yml

```yml
  cloud:
    config:
      server:
        git:
          uri: https://github.com/ry7791/MyAppConfiguration.git
          username:
          password:
          clone-on-start: true
      

```

​       	config Server의 설정은 깃에 있는 설정 내용을 받아서 쓰자



### postman에서 확인

```java
ex)

GET  -  http://59.29.224.68:8011/users-ws/users/status/check
결과 =>  Working on port 54083


/////////////////////////////계정 생성/////////////////////////////
POST - http://59.29.224.68:8011/users-ws/users
Body -  type -json
{
	"firstName" : "test",
	"lastName" : "test",
	"email" : "test@naver.com",
	"password" : "test"
}
결과 => 
<CreateUserResponseModel>
    <firstName>test</firstName>
    <lastName>test</lastName>
    <email>test@naver.com</email>
    <userId>8aa4860d-bff6-4c9d-9973-64b902fa5ad0</userId>
</CreateUserResponseModel>
    
    
    
    
/////////////////////아이디 접속///////////////////////
POST - http://59.29.224.68:8011/users-ws/users/login
Body - 
{
	"email" : "test@naver.com",
	"password" : "test"
}

결과 => 토큰 생성 확인 
token →eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI4YWE0ODYwZC1iZmY2LTRjOWQtOTk3My02NGI5MDJmYTVhZDAiLCJleHA
    
    
/////////////////////토큰으로 다른 곳 접속 ////////////////////////
    
GET - http://59.29.224.68:8011/users-ws/users/status/check
key = Authorization
Value = Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI4YWE0ODYwZC1iZmY2LTRjOWQtOTk3My02NGI5MDJmYTVhZDAiLCJleHAi
    
결과 => Users-WS] Users-WS] Working on port 62283, test_secret
```



### CloudBus - Rabbitmq를 이용한 비동기 메시징 서비스

- 깃으로 설정을 관리하자

```
깃에 있는 설정 내용
token:
    expiration_time: 86400000 # 1days (milliseconds)
    secret: test_secret
```

> 깃에 있는 설정 token.secret 값을 바꾸면 바뀐 값을 configuration 서버에서 받아와서 -> user-ws 의 token.secret 값도 바껴야 한다.  하지만 이 과정은 비동기 통신. 따라서 rabbitmq를 사용하고 refresh를 실행해주면서 zuul, user-ws도 해당값을 받도록 해주자



- docker로 rabbitmq 실행

```shell
docker run -d --name rabbitmq -p 5672:5672 -p 9090:15672 --restart=unless-stopped -e RABBITMQ_DEFAULT_USER=admin -e RABBITMQ_DEFAULT_PASS=admin rabbitmq:management
```



- 의존성 추가

```xml
////// users-ws 랑 config 에 추가 //////////
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-bus-amqp</artifactId>
		</dependency>
		
/////// config에 추가 ////////////
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-actuator</artifactId>
		</dependency>
```



- zuul, user-ws, configuration에  application.yml에 설정 추가

```java
  rabbitmq:
    host: localhost
    port: 5672
    username: admin
    password: admin
```



- configuration의  application.yml에 설정 추가

```yml
management:
  endpoints:
    web:
      exposure:
        include: bus-refresh
```



### postman 에서 확인

```java
깃에서 token.secret 값을 바꿔주고
token:
    expiration_time: 86400000 # 1days (milliseconds)
    secret: local_secret 



////////////////// refresh 해주면 /////////////////////////
POST - http://localhost:8012/actuator/bus-refresh


/////////////////////토큰으로 다른 곳 접속 ////////////////////////
    
GET - http://59.29.224.68:8011/users-ws/users/status/check
key = Authorization
Value = Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI4YWE0ODYwZC1iZmY2LTRjOWQtOTk3My02NGI5MDJmYTVhZDAiLCJleHAi
    
결과 => Users-WS] Users-WS] Working on port 62283, local_secret
```





### 깃 말고 내부 폴더에서 config 관리하자

- 특정 경로에 설정파일 생성

```powershell
C:\Users\HPE\Work\springCloudDev>code application.yml

C:\Users\HPE\Work\springCloudDev>code users-ws.yml
```

- application.yml

```yml
login:
    url:
      path: /users/login

spring:
    datasource:
    url: jdbc:h2:mem:testdb
    username: sa
    password: sa
```

- users-ws.yml

```yml
login:
    url:
      path: /users/login

spring:
    datasource:
    url: jdbc:h2:mem:testdb
    username: sa
    password: sa
```







- configuration 의 yml

```javascript
spring:
  application:
    name: ConfigServer

//profiles:
//  active: native
/// 두줄 추가

   cloud:
    config:
      server:
        git:
//      native:
//        search-locations: file:///${user.home}/Work/springCloudDev
// 두줄 추가
```



- user-ws의 bootstrap.yml

```yml
spring:
  cloud:
    config:
      uri: http://localhost:8012
#      name: ConfigServer
      name: users-ws
```



### postman에서 확인

```java
GET - http://59.29.224.68:8012/users-ws/default

결과 =>
{
    "name": "users-ws",
    "profiles": [
        "default"
    ],
    "label": null,
    "version": null,
    "state": null,
    "propertySources": [
        {
            "name": "file:///C:/Users/HPE/Work/springCloudDev/users-ws.yml",
            "source": {
                "login.url.path": "/users/login",
                "spring.datasource": "",
                "spring.url": "jdbc:h2:mem:testdb",
                "spring.username": "sa",
                "spring.password": "sa"
            }
        },
        {
            "name": "file:///C:/Users/HPE/Work/springCloudDev/application.yml",
            "source": {
                "gateway.ip": "59.29.224.68",
                "token.expiration_time": 86400000,
                "token.secret": "local_secret"
            }
        }
    ]
}

```



## 암호화 방법 2가지



### 1. jce 사용해서 암호화

- C:\Program Files\Java\jdk-13.0.2\lib\security  경로에 다운받은 jce_policy-8 에 있는 파일 넣어줌



- config-server 에서 bootstrap.yml 추가

```yml
encrypt:
  key: test1234  //키값은 아무거나
```



- postman에서 확인

```javascript
post - http://59.29.224.68:8012/encrypt

body - raw - 12341234

결과값 => 82b0a93fb68fbf76f5ccb59bf4146f9b99478e5dd22fd893e6bdc9f4b9c99554

post - http://59.29.224.68:8012/decrypt
body - raw - 82b0a93fb68fbf76f5ccb59bf4146f9b99478e5dd22fd893e6bdc9f4b9c99554
결과값 => 12341234
```



### 2. self 인증서

- cmd 창에서

```cmd
C:\Users\HPE\Work\springCloudDev> keytool -genkeypair -alias apiEncryptionKey -keyalg RSA -keypass "1q2w3e4r" -keystore apiEncryptionKey.jks -storepass "1q2w3e4r"

[no]:  yes
```

- config-server  bootstrap

```yml
encrypt:
  key: test1234

  key-store:
    location: file:///${user.home}/Work/springCloudDev/apiEncryptionKey.jks
    password: 1q2w3e4r
    alias: apiEncryptionKey
```



- users-ws.yml

```yml
login:
    url:
      path: /users/login

spring:
    datasource:
    url: jdbc:h2:mem:testdb
    username: sa
    password: '{cipher}test'
```

- get - http://59.29.224.68:8012/users-ws/default 에서 

```java
   "propertySources": [
        {
            "name": "file:///C:/Users/HPE/Work/springCloudDev/users-ws.yml",
            "source": {
                "login.url.path": "/users/login",
                "spring.datasource": "",
                "spring.url": "jdbc:h2:mem:testdb",
                "spring.username": "sa",
                "invalid.spring.password": "<n/a>"
            }
        },
        {
```



- post - http://59.29.224.68:8012/encrypt 로 test 값 변환해서 넣으면

```yml
login:
    url:
      path: /users/login

spring:
    datasource:
    url: jdbc:h2:mem:testdb
    username: sa
    password: '{cipher}AQBrePw0iuADV/BAfKulKq9FtKXYI94vJ59RehbZ/YgUqxwc92xNOC+1gj1ufpnZFCDd+hU844iyQgZkH0h6dLwwdg+EctcVb1ltvT1LX0t9aBuzMo69zIsDqjMk8XncFJHEAhB0vcn7vkX5JlrvSA61yqyZrqWPdIcryIOuVVTo0GlBM1LNatEofwqmUANvM0o+DDG+FGShW15Uq/YiWKG02C3MkMX2D4ydMPBm/ui/r1BoCA495PzGO5eqCTN4HwB/3RSCN2O/bnXQaA0eHVMuScArrIpIAv4svhivqS0ARZ2QzUczaSVYpOXswiJmxnOOyJWZQhZ2Wyg3u2Z3hBsk+uf8gBwW8uHgJByYU8iiiMlLiKW8t1RDbvzDex1ZCes='
```



- 나옴

```java
    "propertySources": [
        {
            "name": "file:///C:/Users/HPE/Work/springCloudDev/users-ws.yml",
            "source": {
                "login.url.path": "/users/login",
                "spring.datasource": "",
                "spring.url": "jdbc:h2:mem:testdb",
                "spring.username": "sa",
                "spring.password": "test"
            }
        },
```







### kafka 메세지 큐잉 시스템



```javascript
C:\Users\HPE\Work\kafka_2.12-2.3.1>.\bin\windows\zookeeper-server-start.bat .\config\zookeeper.properties

C:\Users\HPE\Work\kafka_2.12-2.3.1>.\bin\windows\kafka-server-start.bat .\config\server.properties

```



- consumer

  ```powershell
  C:\Users\HPE\Work\kafka_2.12-2.3.1>.\bin\windows\kafka-topics.bat --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic msa_20200515
  
  C:\Users\HPE\Work\kafka_2.12-2.3.1>.\bin\windows\kafka-topics.bat --list --bootstrap-server localhost:9092
  
  C:\Users\HPE\Work\kafka_2.12-2.3.1>.\bin\windows\kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic msa_20200515
  ```

  

- broker

```powershell
C:\Users\HPE\Work\kafka_2.12-2.3.1>.\bin\windows\kafka-console-producer.bat --broker-list localhost:9092 --topic msa_20200515

그다음에  메세지 쓰면 consumer한테 그대로 전달됨
```







- restTemplate을 이용한 users에서의 albums 호출

```java
get - http://localhost:8011/users-ws/users/{userId}

Headers
key = Authorization
value = Bearer+' '+토큰값

결과값 => 
{
    "userId": "280e5311-e490-40ab-b8e8-a8d73cd6ac69",
    "firstName": "test",
    "lastName": "test",
    "email": "test@naver.com",
    "albums": [
        {
            "albumId": "album1Id",
            "userId": "280e5311-e490-40ab-b8e8-a8d73cd6ac69",
            "name": "album 1 name",
            "description": "album 1 description"
        },
        {
            "albumId": "album2Id",
            "userId": "280e5311-e490-40ab-b8e8-a8d73cd6ac69",
            "name": "album 2 name",
            "description": "album 2 description"
        }
    ]
}
```



- Feign - restTemplate 필요없이 msa 간 데이터 호출 가능

```java
package com.example.myappapiusers.client;

import com.example.myappapiusers.model.AlbumResponseModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "albums-ws")
public interface AlbumServiceClient {

    @GetMapping("/users/{id}/albums")
    List<AlbumResponseModel> getAlbums(@PathVariable String id);
}
//이거 만들면
```



```java
// restTemplate과 달리 한 줄로 끝~~~
//        ResponseEntity<List<AlbumResponseModel>> albumsListResponse =
//       restTemplate.exchange(
//               String.format("http://albums-ws/users/%s/albums",userId),
//               HttpMethod.GET,
//               null,
//               new ParameterizedTypeReference<List<AlbumResponseModel>>() {
//       });
//
//        List<AlbumResponseModel> albumsList = albumsListResponse.getBody();


        List<AlbumResponseModel> albumsList = albumServiceClient.getAlbums(userId);
```









- h2 console : http://59.29.224.68:54415/h2-console 에서 데이터 확인가능



- cmd  포트 확인 = jps

- modelMapper dependency

다른 클래스로 어떠한 object가 갖고 있는 필드 값들을 쉽게, **자동으로** mapping 해주는 라이브러리

```
// 새로운 객체로 생성. user 를 userDTO로 변환
UserDTO userDTO = userMapper.map(user, UserDTO.class);

```





