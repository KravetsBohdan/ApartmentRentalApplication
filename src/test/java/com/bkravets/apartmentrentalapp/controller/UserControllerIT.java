package com.bkravets.apartmentrentalapp.controller;

import com.bkravets.apartmentrentalapp.entity.User;
import com.bkravets.apartmentrentalapp.repository.ApartmentRepository;
import com.bkravets.apartmentrentalapp.repository.BookingRepository;
import com.bkravets.apartmentrentalapp.repository.UserRepository;
import com.bkravets.apartmentrentalapp.security.TokenProvider;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class UserControllerIT {

    @LocalServerPort
    private int springBootPort = 0;

    private static final String USERS_URL = "/api/users";
    private static final String USER_URL = "/api/users/me";
    private static final String LOGIN_URL = "/api/users/login";
    private static final String REGISTER_URL = "/api/users/register";

    private static final User USER_ENTITY = new User(1L, "john@mail.com", "+38", "John", "Doe", "12345678", null, null, null);
    private static final String USER_JSON = """
            {
                "email": "john@mail.com",
                "firstName": "John",
                "lastName": "Doe",
                "phone": "+38",
                "password": "12345678"
            }
            """;

    private static final String LOGIN_JSON = """
            {
                "email": "john@mail.com",
                "password": "12345678"
            }
            """;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TokenProvider tokenProvider;

    private String token;


    @BeforeEach
    void beforeEach() {
//        User savedUser = userRepository.save(USER_ENTITY);
//
//        savedUserId = savedUser.getId();
    }

    @AfterEach
    void tearDown() {
//        bookingRepository.deleteAll();
//        apartmentRepository.deleteAll();
//        userRepository.deleteAll();
    }


    @Test
    @Order(1)
    void shouldCreateUser() {
        given()
                .contentType("application/json")
                .port(springBootPort)
                .body(USER_JSON)
                .when()
                .post(REGISTER_URL)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("email", equalTo(USER_ENTITY.getEmail()))
                .body("firstName", equalTo(USER_ENTITY.getFirstName()))
                .body("lastName", equalTo(USER_ENTITY.getLastName()))
                .body("phone", equalTo(USER_ENTITY.getPhone()));
    }

    @Test
    @Order(2)
    void shouldLogin() {
        USER_ENTITY.setPassword(passwordEncoder.encode(USER_ENTITY.getPassword()));
        userRepository.save(USER_ENTITY);
        RestAssured.registerParser("text/plain", Parser.JSON);
        given()
                .contentType("application/json")
                .port(springBootPort)
                .body(LOGIN_JSON)
                .when()
                .post(LOGIN_URL)
                .then()
                .statusCode(HttpStatus.OK.value());
        userRepository.delete(USER_ENTITY);
    }


    @Test
    @Order(3)
    void shouldGetUser() {
        USER_ENTITY.setPassword(passwordEncoder.encode(USER_ENTITY.getPassword()));
        userRepository.save(USER_ENTITY);
        token = tokenProvider.generateToken(USER_ENTITY.getEmail());
        given()
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")
                .port(springBootPort)
                .when()
                .get(USER_URL)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("email", equalTo(USER_ENTITY.getEmail()))
                .body("firstName", equalTo(USER_ENTITY.getFirstName()))
                .body("lastName", equalTo(USER_ENTITY.getLastName()))
                .body("phone", equalTo(USER_ENTITY.getPhone()));
        userRepository.delete(USER_ENTITY);
    }


    @Test
    @Order(4)
    void shouldUpdateUser() {
        String updatedUserJSON = """
                {
                    "firstName": "Dan",
                    "lastName": "Do",
                    "phone": "+380"
                }
                """;

        USER_ENTITY.setPassword(passwordEncoder.encode(USER_ENTITY.getPassword()));
        userRepository.save(USER_ENTITY);
        token = tokenProvider.generateToken(USER_ENTITY.getEmail());

        given()
                .header("Authorization", "Bearer " + token)
                .body(updatedUserJSON)
                .contentType(ContentType.JSON)
                .port(springBootPort)
                .when()
                .put(USER_URL)
                .then()
                .statusCode(HttpStatus.ACCEPTED.value())
                .body("firstName", equalTo("Dan"))
                .body("lastName", equalTo("Do"))
                .body("phone", equalTo("+380"));
        userRepository.delete(USER_ENTITY);
    }

}