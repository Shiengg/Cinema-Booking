package com.example.cinema_booking.controller;

import com.example.cinema_booking.dto.request.ScreeningRequestDTO;
import com.example.cinema_booking.dto.response.ScreeningResponseDTO;
import com.example.cinema_booking.service.ScreeningService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/screenings")
@RequiredArgsConstructor
public class ScreeningController {
    private final ScreeningService screeningService;

    @GetMapping
    public CompletableFuture<ResponseEntity<List<ScreeningResponseDTO>>> getAllScreenings() {
        return screeningService.getAllScreenings()
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/{id}")
    public CompletableFuture<ResponseEntity<ScreeningResponseDTO>> getScreening(@PathVariable Long id) {
        return screeningService.getScreeningWithSeats(id)
                .thenApply(ResponseEntity::ok);
    }

    @PostMapping
    public CompletableFuture<ResponseEntity<ScreeningResponseDTO>> createScreening(
            @Valid @RequestBody ScreeningRequestDTO request) {
        return screeningService.createScreening(request)
                .thenApply(ResponseEntity::ok);
    }

    @PutMapping("/{id}")
    public CompletableFuture<ResponseEntity<ScreeningResponseDTO>> updateScreening(
            @PathVariable Long id,
            @Valid @RequestBody ScreeningRequestDTO request) {
        return screeningService.updateScreening(id, request)
                .thenApply(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    public CompletableFuture<ResponseEntity<Void>> deleteScreening(@PathVariable Long id) {
        return screeningService.deleteScreening(id)
                .thenApply(r -> ResponseEntity.ok().build());
    }
}
