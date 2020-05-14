### Spring Cloud msa 구조



port 8010 => eureka server

port 8011 => zuul server

port 8012 => configuration server

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



### Rabbitmq를 이용한 비동기 메시징 서비스

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





















































- h2 console : http://59.29.224.68:54415/h2-console 에서 데이터 확인가능



- cmd  포트 확인 = jps

- modelMapper dependency

다른 클래스로 어떠한 object가 갖고 있는 필드 값들을 쉽게, **자동으로** mapping 해주는 라이브러리

```
// 새로운 객체로 생성. user 를 userDTO로 변환
UserDTO userDTO = userMapper.map(user, UserDTO.class);

```



