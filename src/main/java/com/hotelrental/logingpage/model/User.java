package com.hotelrental.logingpage.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {

    @Id
    private String id;

    private String username;
    private String email;
    private String password;
    private String country;
    private String city;
    private String phone;

    @Field("isAdmin")
    private Boolean isAdmin = false;

    private String img;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Field("__v")
    private Integer version = 0;

    public User(String username, String email, String password, String country, String city, String phone) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.country = country;
        this.city = city;
        this.phone = phone;
        this.isAdmin = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}