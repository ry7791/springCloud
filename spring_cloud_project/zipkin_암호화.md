

### zipkin



https://zipkin.io/pages/quickstart.html 들어가서 java의 lastest release 클릭

- jar파일 있는곳에서 명령어 실행

```shell
java -jar zipkin-server-2.21.1-exec.jar
```



- pom.xml에 의존성 추가

```xml
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-sleuth-zipkin</artifactId>
		</dependency>
```

- application.yml

```yml
  zipkin:
    base-url: http://localhost:9411
    sender:
      type: web
  sleuth:
    sampler:
      probability: 1 #1은 모든 요구가 다 된다라는 뜻
```

- UserServiceImpl

```java
        log.info("Before calling albums microservice");
        List<AlbumResponseModel> albumsList = albumServiceClient.getAlbums(userId);
        log.info("after calling albums microservice");
```

http://localhost:9411에서 확인



### 유레카 암호화 

아이디/비번

요청정보가 통과 되어야지 유레카 서비스에 신청 되게 세팅



- 유레카 의존성

```xml
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-security</artifactId>
		</dependency>
```

- 유레카 application.yml

```yml
spring:
  application:
    name: DiscoveryService
  security:
    user:
      name: test
      password: test
```

- 유레카에 파일 하나 생성

```java
package com.example.msadiscoveryserver.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {

    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .httpBasic();
    }

}

```

- 유레카를 이용하는 클라이언트들의 application.yml 수정

```java
eureka:
  client:
    serviceUrl:
      defaultZone: http://test:test@localhost:8010/eureka/
      
     // 서비스들 설정값에 아이디 비번을 넣어준다.

```



- 유레카의 설정을 config에서 관리해보자

유레카 pom.xml에 config 의존성 추가 - 이게 있어야 config 읽을 수 있음

```xml
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-config</artifactId>
		</dependency>
```

- bootstrap.yml

```yml
spring:
  cloud:
    config:
      uri: http://localhost:8012
      name: eureka

```

- dev 폴더의 eureka.yml

```yml
spring:
    security:
        user:
            name: test
            password: '{cipher}AQBIZEy8dSzuV87v/OyIlsZMWmkNoB2YPB3iQx7/kkeDv6ruefVVXjKm2zLjCjI2uvjezFIJigNkjqgZz1cE58kiGVO9ivNJdzpGl65+nCiueaR8aTKSxdbXt6JBaqV+w/HjeyangdeVvwd8x3JnZ7ARRNZkO+DDEyFe9UYW9QdSv1RgIoi4PWEk2XjgbYWU2pr1+zwIab4dqJdFBBOvOlapRED0LS0lKivu0jd+l7H52/ox5A6oSrU6I9RPUnQOW27ssjuCo/N+JHr/THeZPsiGIvSu/iZM/a5motK48inYf3PFlLAkyu7/dJlO/hbyKaupTFoMsYyXNe2UPhHZHbMwGyi/VEEAmUYKXdS2brU0trxAoiNg9soLw667p2kecjM='
```



### docker

```
docker system prune  // 실행중인거 빼고 필요없는 거 다 삭제
```

- docker + EC2

로컬에 있는 환경을 그대로 EC2로 옮겨보자



자바버전

볼륨마운트

암호화처리키

암호화세팅

자르파일을 도커이미지에 복사

도커 이미지 컨테이너화해서 명령어 실행

- intellij 내 도커파일

```dockerfile
FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY apiEncryptionKey.jks apiEncryptionKey.jks
COPY UnlimitedJCEPolicyJDK8/* /usr/lib/jvm/java-1.8-openjdk/jre/lib/security/
COPY target/myapp-config-server-0.1.jar ConfigServer.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","ConfigServer.jar"]
```

- 터널에서의 명령어

```shell
mvn clean
mvn package
docker build --tag=ry7791/config-server --force-rm=true .
docker push ry7791/config-server //도커 허브에 올리는 명령
```



rabbitmq ip 확인

```
docker inspect rabbitmq
```







### 이것저것



마커인터페이스 - 내용이 아무것도 없는 인터페이스

- Serailizable 마커인터페이스
  : dvo 같은 객체를 string형태로 바꿔서 데이터를 전달하는데 문제가 없게 해줌
  : object -> string



- git의 소스 코드를 바로 intellij로 가져오는법

new -> project from version controll 클릭후 주소 복붙

