# Ewaste-Mangement-Backend

 Ewaste Management Portal is an web based application that will help the us to recycle ewaste. It has three interfaces -
 
  -Customer/User
  -Collector
  -Vendor




[![Build Status](https://travis-ci.org/codecentric/springboot-sample-app.svg?branch=master)](https://travis-ci.org/codecentric/springboot-sample-app)
[![Coverage Status](https://coveralls.io/repos/github/codecentric/springboot-sample-app/badge.svg?branch=master)](https://coveralls.io/github/codecentric/springboot-sample-app?branch=master)
[![License](http://img.shields.io/:license-apache-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

Minimal [Spring Boot](http://projects.spring.io/spring-boot/) sample app.

## Requirements

For building and running the application you need:

- [JDK 1.8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
- [Maven 3](https://maven.apache.org)
- [IDE] (https://www.jetbrains.com/idea/)
- [My-Sql] (https://www.mysql.com/)

## Running the application locally

There are several ways to run a Spring Boot application on your local machine. One way is to execute the `main` method in the `com.groupfive.EwasteManagement.EwasteManagementApplication.java` class from your IDE.

Alternatively you can use the [Spring Boot Maven plugin](https://docs.spring.io/spring-boot/docs/current/reference/html/build-tool-plugins-maven-plugin.html) like so:

```shell
1. Build the project using`mvn clean install`
2. Run using `mvn spring-boot:run`
3. The web application is accessible via localhost:8080
```


##### Folder Structure

```
project
└───src
│   └───main
│   │   └───java
│   │   │     └───com.nineleaps.timesheet
│   │   │         └───config
│   │   │         └───controller
│   │   │         └───dto
│   │   │         └───entity
│   │   │         └───helper
│   │   │         └───model
│   │   │         └───repository
│   │   │         └───service
│   │   │         └───EwasteManagementApplication.java
│   │   │         
│   │   │         
│   │   │         
│   │   │         
│   │   └───resources
│   │       └─── application.properties
└───pom.xml
└───...
```

##### Configuring the backend

 The Directory `backend/src/main/resources/` contains the `appication.properties` config file for the backend.
 This includes the following configurations
 - The port on which the server needs to be started
    ```java
        server.port=8080
    ```
- Database connection credentials
    ```java
    spring.datasource.url=jdbc:mysql://[HOST]:[PORT]/[DB_NAME]
    spring.datasource.username=yourDatabaseUsername
    spring.datasource.password=yourDatabasePassword
    spring.datasource.driverClassName=com.mysql.cj.jdbc.Driver
    spring.jpa.show-sql=true
    spring.jpa.hibernate.ddl-auto=update
    ```
- SMPT config for sending the mails from the Server
    ```java
    spring.mail.host=smtp.gmail.com
    spring.mail.port=587
    spring.mail.username=yourEmail@nineleaps.com
    spring.mail.password=yourPassword
    spring.mail.properties.mail.smtp.auth=true
    spring.mail.properties.mail.smtp.starttls.enable=true
    mail.smtp.debug=true
    ```
- JWT configuration
    ```java
    jwt.secret=yourSecretForJwtAuth
    ```
