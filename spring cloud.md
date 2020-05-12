## spring cloud

순서 

1. configuration   8888
2. eureka     9091
   1. 

### msa-architecture-config-server-master

- application.yml

```yml
server:
  port: 8888

spring:
  application:
    name: msa-architecture-config-server
    
  cloud:
    config:
      server:
        git: 
          uri: https://github.com/joneconsulting/spring-microservice.git

```



- https://github.com/joneconsulting/spring-microservice.git 에 있는 msa-architecture-config-server.yml파일

```yml
msaconfig:
  greeting: "hello"
  topic-name: "coffee-topic"   // 이건 profiles 안씀 default값 
  ipaddress: "192.168.10.1"  //http://localhost:8888/msa-architecture-config-server/default
  dbtype: "oracle"
---

spring:
  profiles: local

msaconfig:
  greeting: "Welcome to local server!!!"
  topic-name: "coffee-topic-local"  
  ipaddress: "192.168.10.102"   //http://localhost:8888/msa-architecture-config-server/local 에서 확인가능
  dbtype: "mysql"
  
 // 이 local 설정을 쓰려면 ex) msa-service-coffee-member의 application.yml에서
//    #Config Server
//  cloud:
//    config:
//      uri: http://localhost:8888
//      name: msa-architecture-config-server
//      profile: local    <= 이렇게 local로 설정, 안쓰면 default

---

spring:
  profiles: dev

msaconfig:
  greeting: "welcome to dev server"
  topic-name: "coffee-topic-dev"
  ipaddress: "192.168.10.3"
  dbtype: "Nosql"
---

spring:
  profiles: test
  
msaconfig:
  greeting: "welcome to test server"
  topic-name: "coffee-topic-test"

---

spring:
  profiles: staging
  
msaconfig:
  greeting: "welcome to staging server"
  topic-name: "coffee-topic-staging"

---

spring:
  profiles: prod
  
msaconfig:
  greeting: "welcome to prod server"
  topic-name: "coffee-topic-prod"
```



### Eureka

유레카 서버는 디스커버리 서버를 상속받은 것이므로 둘다 같은 의미 로 보자















### 예제

8010 - eureka

8011 - zuul

8012 - config



터미널에서 실행 (mvn 환경변수 설정해주고)
mvn --version
C:\java\myapp-api-users\myapp-api-users>mvn spring-boot:run



포트랑 아이디 지정하고 실행
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.application.instance_id=kenneth4 --server.port=9001"



유레카에서 확인 가능 