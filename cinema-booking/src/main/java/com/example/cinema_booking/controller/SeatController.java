package com.example.cinema_booking.controller;

import com.example.cinema_booking.dto.response.SeatResponseDTO;
import com.example.cinema_booking.enums.SeatStatus;
import com.example.cinema_booking.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/seats")
@RequiredArgsConstructor
public class SeatController {
    private final SeatService seatService;

    @GetMapping("/screening/{screeningId}/available")
    public CompletableFuture<ResponseEntity<List<SeatResponseDTO>>> getAvailableSeats(
            @PathVariable Long screeningId) {
        return seatService.getAvailableSeats(screeningId)
                .thenApply(ResponseEntity::ok);
    }

    @PostMapping("/{id}/lock")
    public CompletableFuture<ResponseEntity<SeatResponseDTO>> lockSeat(@PathVariable Long id) {
        return seatService.lockSeat(id)
                .thenApply(ResponseEntity::ok);
    }

    @PostMapping("/{id}/unlock")
    public CompletableFuture<ResponseEntity<SeatResponseDTO>> unlockSeat(@PathVariable Long id) {
        return seatService.unlockSeat(id)
                .thenApply(ResponseEntity::ok);
    }

    @PutMapping("/{id}/status")
    public CompletableFuture<ResponseEntity<SeatResponseDTO>> updateSeatStatus(
            @PathVariable Long id,
            @RequestParam SeatStatus status) {
        return seatService.updateSeatStatus(id, status)
                .thenApply(ResponseEntity::ok);
    }
} 