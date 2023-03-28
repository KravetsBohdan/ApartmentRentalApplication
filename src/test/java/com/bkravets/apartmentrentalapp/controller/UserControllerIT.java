package com.bkravets.apartmentrentalapp.controller;

import com.bkravets.apartmentrentalapp.entity.User;
import com.bkravets.apartmentrentalapp.repository.UserRepository;
import com.bkravets.apartmentrentalapp.security.TokenProvider;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class UserControllerIT {

    @LocalServerPort
    private int springBootPort = 0;

    private final String userUrl = "/api/users/me";
    private final String loginUrl = "/api/users/login";
    private final String registerUrl = "/api/users/register";

    private User user = new User(null, "john@mail.com", "+380504559966",
            "John", "Doe", "12345678", null, null, null);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TokenProvider tokenProvider;

    private String token;


    @BeforeEach
    void beforeEach() {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user = userRepository.save(user);
        token = tokenProvider.generateToken(user.getEmail());
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }


    @Test
    void createUser_whenValidUserShouldCreate() {
        String userToCreateJson = """
                    {
                        "email": "jane@mail.com",
                        "firstName": "Jane",
                        "lastName": "Doe",
                        "phone": "+380504558899",
                        "password": "87654321"
                    }
                    """;
        given()
                .contentType("application/json")
                .port(springBootPort)
                .body(userToCreateJson)
        .when()
                .post(registerUrl)
        .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("email", equalTo("jane@mail.com"))
                .body("firstName", equalTo("Jane"))
                .body("lastName", equalTo("Doe"))
                .body("phone", equalTo("+380504558899"));
    }

    @Test
    void createUser_WhenEmailAlreadyUsedShouldReturnError() {
        String userToCreateJson = """
                    {
                        "email": "john@mail.com",
                        "firstName": "Jane",
                        "lastName": "Doe",
                        "phone": "+380504558899",
                        "password": "87654321"
                    }
                    """;
        given()
                .contentType("application/json")
                .port(springBootPort)
                .body(userToCreateJson)
        .when()
                .post(registerUrl)
        .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("message", equalTo("User with email john@mail.com already exists"));
    }

    @Test
    void login_WhenValidCredentialsShouldReturnToken() {
        String loginJson = """
            {
                "email": "john@mail.com",
                "password": "12345678"
            }
            """;

        given()
                .contentType("application/json")
                .port(springBootPort)
                .body(loginJson)
        .when()
                .post(loginUrl)
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("token", notNullValue());

    }

    @Test
    void login_WhenNotValidCredentialsShouldReturnError() {
        String loginJson = """
                {
                    "email": "mail@mail.com"
                    "password": "12345678"
                }
                    """;

        given()
                .contentType("application/json")
                .port(springBootPort)
                .body(loginJson)
        .when()
                .post(loginUrl)
        .then()
                .statusCode(HttpStatus.FORBIDDEN.value());


    }


    @Test
    void getUser_WhenUserLoggedInShouldReturnCreateUser() {
        given()
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")
                .port(springBootPort)
        .when()
                .get(userUrl)
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("email", equalTo(user.getEmail()))
                .body("firstName", equalTo(user.getFirstName()))
                .body("lastName", equalTo(user.getLastName()))
                .body("phone", equalTo(user.getPhone()));
    }

    @Test
    void getUser_WhenUserNotLoggedInShouldReturnError() {
        given()
                .contentType("application/json")
                .port(springBootPort)
        .when()
                .get(userUrl)
        .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }


    @Test
    void updateUser_WhenUserLoggedInShouldReturnUpdatedUser() {
        String updatedUserJSON = """
                {
                    "firstName": "Dan",
                    "lastName": "Do",
                    "phone": "+380"
                }
                """;

        given()
                .header("Authorization", "Bearer " + token)
                .body(updatedUserJSON)
                .contentType(ContentType.JSON)
                .port(springBootPort)
        .when()
                .put(userUrl)
        .then()
                .statusCode(HttpStatus.ACCEPTED.value())
                .body("firstName", equalTo("Dan"))
                .body("lastName", equalTo("Do"))
                .body("phone", equalTo("+380"));
    }

    @Test
    void updateUser_WhenUserNotLoggedInShouldReturnError() {
        String updatedUserJSON = """
                {
                    "firstName": "Dan",
                    "lastName": "Do",
                    "phone": "+380"
                }
                """;
        given()
                .contentType("application/json")
                .body(updatedUserJSON)
                .contentType(ContentType.JSON)
                .port(springBootPort)
        .when()
                .put(userUrl)
        .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }
}