package com.hotelrental.logingpage.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private String id;
    private String username;
    private String email;
    private String country;
    private String city;
    private String phone;
    private Boolean isAdmin;
    private String img;
}