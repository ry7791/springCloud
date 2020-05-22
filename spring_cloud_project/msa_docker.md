# msa_docker

```cmd
명령어
컨테이너 확인 docker ps
정지된 컨테이너 확인 docker ps -a
컨테이너 삭제 docker rm 컨테이너id
컨테이너 모두 삭제 docker rm `docker ps -a -q`
현재 이미지 확인 docker images
이미지 삭제 docker rmi 이미지id
컨테이너를 삭제하기 전에 이미지 삭제 docker rmi -f 이미지id
```





```
docker system prune  // 실행중인거 빼고 필요없는 거 다 삭제
```

- docker + EC2 : 로컬에 있는 환경을 그대로 EC2로 옮겨보자 => 이건 나중에

- 과정

> 자바버전
>
> 볼륨마운트
>
> 암호화처리키
>
> 암호화세팅
>
> 자르파일을 도커이미지에 복사
>
> 도커 이미지 컨테이너화해서 명령어 실행



### 네트워크 연결

- 도커 내 각각의 서비스는 네트워크 지정을 안했기 때문에 서로 통신 불가

```java
docker network ls
NETWORK ID          NAME                DRIVER              SCOPE
2a0cb1bfcf5d        bridge              bridge              local
e97a8bdaca85        host                host                local
6b11333cdcaa        none                null                local
// 도커 네트워크는 3개가 있는데
// 리눅스에서는 --network host 설정가능, 윈도우는 안되니 네트워크를 만들어주자
docker network create photo-app-network
    
// 각각의 서비스 run 할때 --network photo-app-network 추가
```



### rabbitmq

```
docker run -d --name rabbitmq --network photo-app-network -p 5672:5672 -p 15672:15672 --restart=unless-stopped -e RABBITMQ_DEFAULT_USER=admin -e RABBITMQ_DEFAULT_PASS=admin rabbitmq:management



```



### config-server 도커 설정

- config-msa의 도커파일

```dockerfile
FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY apiEncryptionKey.jks apiEncryptionKey.jks
COPY UnlimitedJCEPolicyJDK8/* /usr/lib/jvm/java-1.8-openjdk/jre/lib/security/
COPY target/myapp-config-server-0.1.jar ConfigServer.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","ConfigServer.jar"]
```

- 터미널

```shell
mvn clean //타겟 디렉터리 삭제
mvn package
docker build --tag=ry7791/config-server --force-rm=true .
docker push ry7791/config-server //도커 허브에 올리는 명령

```



- rabbitmq ip 확인

```shell
docker inspect rabbitmq
```

- config에 있는 application.yml 의  spring.profiles.active를 default 로 설정하고 실행

```shell
docker run -d -p 8012:8012 --network photo-app-network --name config -e "spring.rabbitmq.host=172.18.0.2" -e "spring.profiles.active=default" ry7791/config-server
```



### Eureka Discovery 도커 설정

- Eureka 도커파일

```dockerfile
FROM openjdk:8-jdk-alpine
COPY target/photoappdiscoveryservice-0.0.1-SNAPSHOT.jar DiscoveryService.jar
ENTRYPOINT ["java", "-jar","DiscoveryService.jar"]
```

- 터미널

```java
mvn clean
mvn package
docker build --tag=ry7791/eureka-server --force-rm=true .
docker push ry7791/eureka-server //도커 허브에 올리는 명령
docker run -d -p 8010:8010 --network photo-app-network --name eureka-server -e "spring.cloud.config.url=172.18.0.3:8012" ry7791/eureka-server
// ??? 부분은 conofig-server의 IPAdress 가 들어가야 한다
// docker ps 로 config-server 컨테이너id 확인하고
// docker inspect 컨테이너id    => config-server의 IPAdress 확인가능
```

여기까지 했으면 인터넷창에 localhost:8010 쳐서 유레카 잘 되는지 확인해보자



### Zuul 도커 설정

- Zuul 도커파일

```dockerfile
FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY target/myapp-zuul-gateway-0.0.1-SNAPSHOT.jar ZuulApiGateway.jar
ENTRYPOINT ["java", "-jar","ZuulApiGateway.jar"]
```

- 터미널

```shell
docker build --tag=ry7791/zuul-gateway --force-rm=true . //이미지화
docker push ry7791/zuul-gateway //도커 허브에 올리는 명령
docker run -d -p 8011:8011 --network photo-app-network --name zuul-gateway -e "spring.rabbitmq.host=172.18.0.2" -e "spring.cloud.config.url=172.18.03:8012" -e "eureka.client.serviceUrl.defaultZone=http://test:test@172.18.0.4:8010/eureka/"ry7791/zuul-gateway
```

> zuul 내의 유동적인 세팅 값들은 -e로 수정해주자

```java
EX)env.getProperty("token.secret") -> config를 통해 git에서 token.secret 가져옴

////////////zuul내의 bootstrap.yml에서는 이렇게 세팅 되어 있음//////////////////
spring:
  cloud:
    config:
      uri: http://localhost:8012
      name: ConfigServer
//현재 도커로 실행 된 config의 ip값은 로컬이 아니라 172.17.03 이므로
-e "spring.cloud.config.url=172.18.03:8012"
```



### Albums Microservice

- Albums 도커파일

```dockerfile
FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY target/myapp-api-albums-0.0.1-SNAPSHOT.jar PhotoAppApiAlbums.jar
ENTRYPOINT ["java", "-jar","PhotoAppApiAlbums.jar"]
```

- 터미널

```java
mvn clean

mvn package

docker build --tag=ry7791/albums-microservice --force-rm=true .
docker push ry7791/albums-microservice //도커 허브에 올리는 명령

// 같은 서비스를 랜덤포트로 여러개 만들 수 있으므로 name 지정하면 충돌남 name x
docker run -d --network photo-app-network -e "eureka.client.serviceUrl.defaultZone=http://test:test@172.18.0.4:8010/eureka/" ry7791/albums-microservice

```





### Users Microservice

- Users 도커파일

```
FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY target/myapp-api-users-0.0.1-SNAPSHOT.jar PhotoAppApiUsers.jar
ENTRYPOINT ["java", "-jar","PhotoAppApiUsers.jar"]
```

- 터미널

```java
mvn clean

mvn package

docker build --tag=ry7791/users-microservice --force-rm=true .
docker push ry7791/users-microservice //도커 허브에 올리는 명령

// 도커에 zipkin 깔아주자
docker run -d --network photo-app-network -p 5673:5673 --name zipkin openzipkin/zipkin -p 9091:15673 --restart=unless-stopped -e ZIPKIN_DEFAULT_USER=admin -e ZIPKIN_DEFAULT_PASS=admin zipkin:management

docker ps
docker inspect zipkin아이디

// 도커에 mysql 깔아서 spring_db 만들어주자
docker pull mysql
docker run -d --network photo-app-network -p 3306:3306 -e MYSQL_ROOT_PASSWORD=mysql --name mysql1 mysql
docker exec -i -t mysql1 bash
mysql -u root -p
create database spring_db;


docker ps
docker inspect mysql아이디

// 같은 서비스를 랜덤포트로 여러개 만들 수 있으므로 name 지정하면 충돌남 name x
docker run -d --network photo-app-network
-e "spring.zipkin.base-url=172.18.0.7:5673" 
-e "spring.cloud.config.url=172.18.03:8012" 
-e "spring.rabbitmq.host=172.18.0.2" 
-e "eureka.client.serviceUrl.defaultZone=http://test:test@172.18.0.4:8010/eureka/" 
-e "server.port=40000" 
-e "spring.datasource.url=jdbc:mysql://172.18.0.8:3306/spring_db?serverTimezone=UTC&characterEncoding=UTF-8"ry7791/users-microservice

```





