# Ewaste-Mangement-Backend

 Ewaste Management Portal is an web based application that will help the us to recycle ewaste. It has three interfaces -
 
  - Customer/User
  
  - Collector
  
  - Vendor




[![Build Status](https://travis-ci.org/codecentric/springboot-sample-app.svg?branch=master)](https://travis-ci.org/codecentric/springboot-sample-app)
[![Coverage Status](https://coveralls.io/repos/github/codecentric/springboot-sample-app/badge.svg?branch=master)](https://coveralls.io/github/codecentric/springboot-sample-app?branch=master)
[![License](http://img.shields.io/:license-apache-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

Minimal [Spring Boot](http://projects.spring.io/spring-boot/) sample app.

## Requirements

For building and running the application you need:

- [JDK 1.8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
- [Maven 3](https://maven.apache.org)
- [Any IDE](https://www.jetbrains.com/idea/)
- [My-Sql](https://www.mysql.com/)
- [GIT](https://git-scm.com/)

## Running the application locally

There are several ways to run a Spring Boot application on your local machine. One way is to execute the `main` method in the `com.groupfive.ewastemanagement.EwasteManagementApplication.java` class from your IDE.

Alternatively you can use the [Spring Boot Maven plugin](https://docs.spring.io/spring-boot/docs/current/reference/html/build-tool-plugins-maven-plugin.html) like so:

```shell
1. Build the project using`mvn clean install`
2. Run using `mvn spring-boot:run`
3. The web application is accessible via localhost:8080
```


#### Folder Structure

```
project
└───src
├── main
│   ├── java
│   │   └── com
│   │       └── groupfive
│   │           └── EwasteManagement
│   │               ├── Config
│   │               │   ├── JwtAuthFilter.java
│   │               │   └── MySecurityConfig.java
│   │               ├── Controller
│   │               │   ├── CollectorController.java
│   │               │   ├── CustomerController.java
│   │               │   ├── UserController.java
│   │               │   └── VendorController.java
│   │               ├── Dto
│   │               │   ├── EnvelopeMessage.java
│   │               │   └── ErrorMessages.java
│   │               ├── Entity
│   │               │   ├── CollectorEntity
│   │               │   │   ├── AllPendingRequest.java
│   │               │   │   ├── CategoriesAccepted.java
│   │               │   │   ├── Collector.java
│   │               │   │   ├── EWasteDriveCategories.java
│   │               │   │   ├── EWasteDrive.java
│   │               │   │   └── SellItems.java
│   │               │   ├── CustomerEntity
│   │               │   │   ├── Customer.java
│   │               │   │   └── Orders.java
│   │               │   ├── Notification
│   │               │   │   ├── CollectorNotification.java
│   │               │   │   ├── CustomerNotification.java
│   │               │   │   └── VendorNotification.java
│   │               │   ├── PasswordResetToken.java
│   │               │   ├── UserEntity
│   │               │   │   ├── Role.java
│   │               │   │   └── User.java
│   │               │   └── VendorEntity
│   │               │       ├── Vendor.java
│   │               │       └── VendorOrders.java
│   │               ├── EwasteManagementApplication.java
│   │               ├── Helper
│   │               │   ├── Constants.java
│   │               │   ├── JwtUtil.java
│   │               │   └── ValidationHandler.java
│   │               ├── Model
│   │               │   ├── AcceptSellItemsVendor.java
│   │               │   ├── EWasteDriveModel.java
│   │               │   ├── JwtModel
│   │               │   │   ├── JwtRequest.java
│   │               │   │   └── JwtResponse.java
│   │               │   ├── PasswordModel.java
│   │               │   ├── RequestModel.java
│   │               │   ├── SellItemModel.java
│   │               │   └── UserModel.java
│   │               ├── Repository
│   │               │   ├── CollectorRepository
│   │               │   │   ├── AllPendingRequestRepo.java
│   │               │   │   ├── CategoriesAcceptedRepo.java
│   │               │   │   ├── CollectorRepo.java
│   │               │   │   ├── EWasteDriveRepo.java
│   │               │   │   └── SellItemRepo.java
│   │               │   ├── CustomerRepository
│   │               │   │   ├── CustomerRepo.java
│   │               │   │   └── OrdersRepo.java
│   │               │   ├── Notification
│   │               │   │   ├── CollectorNotificationRepo.java
│   │               │   │   ├── CustomerNotificationRepo.java
│   │               │   │   └── VendorNotificationRepo.java
│   │               │   ├── PasswordResetTokenRepository.java
│   │               │   ├── UserRepository
│   │               │   │   └── UserRepo.java
│   │               │   └── VendorRepository
│   │               │       ├── VendorOrdersRepo.java
│   │               │       └── VendorRepo.java
│   │               └── Service
│   │                   ├── CollectorService
│   │                   │   ├── CollectorServiceImplementation.java
│   │                   │   └── CollectorService.java
│   │                   ├── CustomerService
│   │                   │   ├── CustomerServiceImplementation.java
│   │                   │   ├── CustomerService.java
│   │                   │   └── Order
│   │                   │       ├── OrderServiceImplementation.java
│   │                   │       └── OrderService.java
│   │                   ├── JWTUserService
│   │                   │   ├── JWTUserDetails.java
│   │                   │   └── JWTUserDetailsService.java
│   │                   ├── UserService
│   │                   │   ├── UserServiceImplementation.java
│   │                   │   └── UserService.java
│   │                   └── VendorService
│   │                       ├── VendorServiceImplementation.java
│   │                       └── VendorService.java
│   └── resources
│       └── application.properties
└── test
    └── java
        └── com
            └── groupfive
                └── EwasteManagement
                    ├── Controller
                    │   ├── CollectorControllerTest.java
                    │   ├── CustomerControllerTest.java
                    │   └── VendorControllerTest.java
                    ├── EwasteManagementApplicationTests.java
                    ├── Repository
                    │   ├── CollectorRepository
                    │   │   ├── CollectorRepoTest.java
                    │   │   └── EWasteDriveRepoTest.java
                    │   ├── CustomerRepository
                    │   │   ├── CustomerRepoTest.java
                    │   │   └── OrdersRepoTest.java
                    │   ├── UserRepository
                    │   │   └── UserRepoTest.java
                    │   └── VendorRepository
                    │       ├── VendorOrdersRepoTest.java
                    │       └── VendorRepoTest.java
                    └── Service
                        ├── CollectorService
                        │   └── CollectorServiceTest.java
                        ├── CustomerService
                        │   ├── CustomerServiceTest.java
                        │   └── Order
                        │       └── OrderServiceTest.java
                        ├── UserService
                        │   └── UserServiceTest.java
                        └── VendorService
                            └── VendorServiceImplementationTest.java

```

#### Configuring the backend

 The Directory `EwasteManagement/src/main/resources/` contains the `appication.properties` config file for the backend.
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


## Resources

For more information about Spring Boot and Starters try below resources:

- [Spring Docs](https://spring.io/projects/spring-boot)


## Code of Conduct
[![Contributor Covenant](https://img.shields.io/badge/Contributor%20Covenant-v1.4%20adopted-ff69b4.svg)](CODE_OF_CONDUCT.md)


Please note that this project is released with a Contributor Code of Conduct. By participating in this project you agree to abide by its terms.
