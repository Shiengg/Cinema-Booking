package com.example.cinema_booking.controller;

import com.example.cinema_booking.CinemaBookingApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloSpring {
    @GetMapping("/hello")
    String sayHello(){
        return "Hello, World!!!!";
    }
    public static void main(String[] args) {
        SpringApplication.run(CinemaBookingApplication.class, args);
    }
}

