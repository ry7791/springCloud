### msa 구조



port 8010 => eureka

port 8011 => zuul

port 8012 => config

랜덤포트 => myapp-api-users-application





### postman에서 확인

```java
ex)

GET  -  http://59.29.224.68:8011/users-ws/users/status/check
결과 =>  Working on port 54083



POST - http://59.29.224.68:8011/users-ws/users
Body - 
{
	"firstName" : "21111kenneth1",
	"lastName" : "211111hi1",
	"email" : "211111test1@naver.com",
	"password" : "211111hi12341"
}

결과 => 
<CreateUserResponseModel>
    <firstName>kenneth2</firstName>
    <lastName>hi2</lastName>
    <email>test2@naver.com</email>
    <encryptedPassword>test encrypted password</encryptedPassword>
</CreateUserResponseModel>
```



- h2 console : http://59.29.224.68:54415/h2-console





## Spring Cloud





modelMapper dependency

다른 클래스로 어떠한 object가 갖고 있는 필드 값들을 쉽게, **자동으로** mapping 해주는 라이브러리

```
// 새로운 객체로 생성. user 를 userDTO로 변환
UserDTO userDTO = userMapper.map(user, UserDTO.class);

```



