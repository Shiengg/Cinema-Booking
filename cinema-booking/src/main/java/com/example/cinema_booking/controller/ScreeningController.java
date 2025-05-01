package com.example.cinema_booking.controller;

import com.example.cinema_booking.dto.response.ScreeningResponseDTO;
import com.example.cinema_booking.service.ScreeningService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/screenings")
@RequiredArgsConstructor
public class ScreeningController {
    private final ScreeningService screeningService;

    @GetMapping("/{id}")
    public CompletableFuture<ResponseEntity<ScreeningResponseDTO>> getScreening(@PathVariable Long id) {
        return screeningService.getScreeningWithSeats(id)
                .thenApply(ResponseEntity::ok);
    }
}
