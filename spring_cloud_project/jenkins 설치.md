### jenkins plugin 설치

```
vagrant plugin install vagrant-vbguest
bcdedit /set hypervisorlaunchtype off
```

https://archive.apache.org/dist/tomcat/tomcat-9/v9.0.30/bin/apache-tomcat-9.0.30.tar.gz

```java
vagrant ssh jenkins-server

#sudo yum install java-1.8*
#sudo yum -y install java-1.8.0-openjdk-devel

find /usr/lib/jvm/java-1.8* | head -n 3 //버전확인하고 아래 줄 수정해서 넣어줌
vi ~/.bash_profile

///추가 내용////
JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk-<Java version which seen in the above output>
export JAVA_HOME
PATH=$PATH:$JAVA_HOME
//////////////////////////////////

```

### jenkins 설치

```java
yum -y install wget
sudo wget -O /etc/yum.repos.d/jenkins.repo https://pkg.jenkins.io/redhat-stable/jenkins.repo
sudo rpm --import https://pkg.jenkins.io/redhat-stable/jenkins.io.key
yum -y install jenkins

// vagrantfile 들어가서
// public _network -> private_network 으로 3개다 수정 후 저장

vagrant halt
vagrant up
```





### jenkins start

```java
### start Jenkins

# Start jenkins service
service jenkins start

# Setup Jenkins to start at boot,
chkconfig jenkins on
    
//locahost:18080 으로 가면 패스워드 입력하라고함
[vagrant@jenkins-server ~]$ sudo cat /var/lib/jenkins/secrets/initialAdminPassword
8a2681fa6b3d4770aca88c78d05ba087   // 이거 입력
    
// 접속해서 설정에서 비번 편한걸로 바꿔주자
```



### docker, jenkins, tomcat 수정

```java
C:\Users\HPE\Work\vagrant>vagrant ssh docker-server
C:\Users\HPE\Work\vagrant>vagrant ssh jenkins-server
C:\Users\HPE\Work\vagrant>vagrant ssh tomcat-server
    
sudo vi /etc/ssh/sshd_config 

65 PasswordAuthentication yes  // yes로 수정
    
sudo systemctl restart sshd
    
    
```



### locahost:18080 - JDK 설정

```JAVA
gloval tool configuration 가서
NAME : JAVA8
JAVA_HOME : //echo $JAVA_HOME 로 확인한 값 넣자

```



### tomcat-server

```java
1. java (openjdk 1.8) 설치

2. apache-tomcat-9 설치
sudo wget https://archive.apache.org/dist/tomcat/tomcat-9/v9.0.30/bin/apache-tomcat-9.0.30.tar.gz
tar xvfz apache-tomcat-9.0.30.tar.gz
 
[vagrant@tomcat-server ~]$ cd apache-tomcat-9.0.30/bin
[vagrant@tomcat-server bin]$ ./startup.sh
3. (windows) http://localhost:28080
```

