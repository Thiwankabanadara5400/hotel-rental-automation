/*package com.hotelrental.logingpage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LogingpageApplication {
    public static void main(String[] args) {
        SpringApplication.run(LogingpageApplication.class, args);
    }
}*/
package com.hotelrental.logingpage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LogingpageApplication {
    public static void main(String[] args) {
        System.out.println("Java version: " + System.getProperty("java.version"));
        System.out.println("Starting Spring Boot application...");
        SpringApplication.run(LogingpageApplication.class, args);
    }
}