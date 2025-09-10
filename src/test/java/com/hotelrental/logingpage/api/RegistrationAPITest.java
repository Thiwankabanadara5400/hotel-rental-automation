package com.hotelrental.logingpage.api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class RegistrationAPITest {

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "http://localhost:8080";
        RestAssured.basePath = "/api/auth";
    }

    @Test
    @DisplayName("API Test 1: Successful Registration")
    public void testSuccessfulRegistration() {
        Random random = new Random();

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("username", "apiuser" + random.nextInt(10000));
        requestBody.put("email", "api" + random.nextInt(10000) + "@test.com");
        requestBody.put("password", "Test@1234");
        requestBody.put("country", "Sri Lanka");
        requestBody.put("city", "Colombo");
        requestBody.put("phone", "0771234567");

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/register")
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("message", equalTo("Registration successful"))
                .body("data.username", equalTo(requestBody.get("username")))
                .body("data.email", equalTo(requestBody.get("email")));
    }

    @Test
    @DisplayName("API Test 2: Registration with Invalid Email")
    public void testRegistrationWithInvalidEmail() {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("username", "testuser" + new Random().nextInt(10000));
        requestBody.put("email", "invalid-email");
        requestBody.put("password", "Test@1234");
        requestBody.put("country", "USA");
        requestBody.put("city", "New York");
        requestBody.put("phone", "1234567890");

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/register")
                .then()
                .statusCode(200)
                .body("success", equalTo(false))
                .body("message", containsString("Invalid"));
    }
}