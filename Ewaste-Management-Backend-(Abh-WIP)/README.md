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

There are several ways to run a Spring Boot application on your local machine. One way is to execute the `main` method in the `com.groupfive.ewastemanagement.EWasteManagementApplication.java` class from your IDE.

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
├───main
│   ├── java
│   │   └── com
│   │       └── groupfive
│   │           └── ewastemanagement
│   │               ├── config
│   │               │   ├── InterceptorConfig.java
│   │               │   ├── JwtAuthFilter.java
│   │               │   └── MySecurityConfig.java
│   │               ├── controller
│   │               │   ├── CollectorController.java
│   │               │   ├── CustomerController.java
│   │               │   ├── UserController.java
│   │               │   └── VendorController.java
│   │               ├── dto
│   │               │   ├── error
│   │               │   │   ├── ErrorMessage.java
│   │               │   │   └── ValidationErrorMessage.java
│   │               │   ├── ResponseMessage.java
│   │               │   └── ResponseMessageWithPagination.java
│   │               ├── entity
│   │               │   ├── collectorentity
│   │               │   │   ├── AllPendingRequest.java
│   │               │   │   ├── CategoriesAccepted.java
│   │               │   │   ├── EWasteDrive.java
│   │               │   │   ├── SellItems.java
│   │               │   │   └── UserDetails.java
│   │               │   ├── customerentity
│   │               │   │   └── Orders.java
│   │               │   ├── notification
│   │               │   │   └── Notification.java
│   │               │   ├── userentity
│   │               │   │   ├── Role.java
│   │               │   │   └── User.java
│   │               │   └── vendorentity
│   │               │       └── VendorOrders.java
│   │               ├── EWasteManagementApplication.java
│   │               ├── exception
│   │               │   ├── BadRequestException.java
│   │               │   ├── DuplicateUpdationException.java
│   │               │   ├── GlobalExceptionHandler.java
│   │               │   ├── InvalidDomainException.java
│   │               │   ├── InvalidTokenException.java
│   │               │   ├── InvalidUserException.java
│   │               │   ├── NoDataException.java
│   │               │   ├── NotFoundException.java
│   │               │   ├── OAuth2AuthenticationProcessingException.java
│   │               │   ├── RangeNotSatisfiedException.java
│   │               │   └── ResourceNotFoundException.java
│   │               ├── helper
│   │               │   ├── Constants.java
│   │               │   ├── CookieUtils.java
│   │               │   ├── JwtUtil.java
│   │               │   ├── Util.java
│   │               │   └── ValidationHandler.java
│   │               ├── interceptor
│   │               │   └── TraceInterceptor.java
│   │               ├── model
│   │               │   ├── AcceptSellItemsVendor.java
│   │               │   ├── AuthProvider.java
│   │               │   ├── EWasteDriveModel.java
│   │               │   ├── jwtmodel
│   │               │   │   ├── JwtRequest.java
│   │               │   │   └── JwtResponse.java
│   │               │   ├── PasswordModel.java
│   │               │   ├── RefreshTokenRequest.java
│   │               │   ├── RequestModel.java
│   │               │   ├── SellItemModel.java
│   │               │   └── UserModel.java
│   │               ├── repository
│   │               │   ├── collectorrepository
│   │               │   │   ├── AllPendingRequestRepo.java
│   │               │   │   ├── CategoriesAcceptedRepo.java
│   │               │   │   ├── EWasteDriveRepo.java
│   │               │   │   ├── SellItemRepo.java
│   │               │   │   └── UserDetailsRepo.java
│   │               │   ├── customerrepository
│   │               │   │   └── OrdersRepo.java
│   │               │   ├── notification
│   │               │   │   └── NotificationRepo.java
│   │               │   ├── userrepository
│   │               │   │   ├── RoleRepo.java
│   │               │   │   └── UserRepo.java
│   │               │   └── vendorrepository
│   │               │       └── VendorOrdersRepo.java
│   │               ├── security
│   │               │   ├── CurrentUser.java
│   │               │   ├── CustomUserDetailsService.java
│   │               │   ├── oauth2
│   │               │   │   ├── CustomOAuth2UserService.java
│   │               │   │   ├── HttpCookieOAuth2AuthorizationRequestRepository.java
│   │               │   │   ├── OAuth2AuthenticationFailureHandler.java
│   │               │   │   ├── OAuth2AuthenticationSuccessHandler.java
│   │               │   │   └── user
│   │               │   │       ├── GoogleOAuth2UserInfo.java
│   │               │   │       ├── OAuth2UserInfoFactory.java
│   │               │   │       └── OAuth2UserInfo.java
│   │               │   ├── RestAuthenticationEntryPoint.java
│   │               │   └── UserPrincipal.java
│   │               └── service
│   │                   ├── collectorservice
│   │                   │   ├── CollectorServiceImplementation.java
│   │                   │   └── CollectorService.java
│   │                   ├── customerservice
│   │                   │   ├── CustomerServiceImplementation.java
│   │                   │   ├── CustomerService.java
│   │                   │   └── order
│   │                   │       ├── OrderServiceImplementation.java
│   │                   │       └── OrderService.java
│   │                   ├── jwtuserservice
│   │                   │   ├── JWTUserDetails.java
│   │                   │   └── JWTUserDetailsService.java
│   │                   ├── userservice
│   │                   │   ├── UserServiceImplementation.java
│   │                   │   └── UserService.java
│   │                   └── vendorservice
│   │                       ├── VendorServiceImplementation.java
│   │                       └── VendorService.java
│   └── resources
│       ├── application-dev.properties
│       ├── application.properties
│       ├── application.yml
│       └── logback.xml
└── test
    └── java
        └── com
            └── groupfive
                └── ewastemanagement
                    ├── controller
                    │   ├── CollectorControllerTest.java
                    │   ├── CustomerControllerTest.java
                    │   ├── UserControllerTest.java
                    │   └── VendorControllerTest.java
                    ├── EWasteManagementApplicationTests.java
                    └── service
                        ├── collectorservice
                        │   └── CollectorServiceTest.java
                        ├── customerservice
                        │   ├── CustomerServiceTest.java
                        │   └── order
                        │       └── OrderServiceTest.java
                        ├── userservice
                        │   └── UserServiceTest.java
                        └── vendorservice
                            └── VendorServiceImplementationTest.java

```

#### Configuring the backend

 The Directory `EwasteManagement/src/main/resources/` contains the `appication.properties` config file for the backend.
 This includes the following configurations
 - The port on which the server needs to be started
    ```java
        server.port=8083
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
