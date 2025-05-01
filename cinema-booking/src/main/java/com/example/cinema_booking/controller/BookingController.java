package com.example.cinema_booking.controller;

import com.example.cinema_booking.dto.request.BookingRequestDTO;
import com.example.cinema_booking.dto.response.BookingResponseDTO;
import com.example.cinema_booking.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @GetMapping
    public CompletableFuture<ResponseEntity<List<BookingResponseDTO>>> getAllBookings() {
        return bookingService.getAllBookings()
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/{id}")
    public CompletableFuture<ResponseEntity<BookingResponseDTO>> getBooking(@PathVariable Long id) {
        return bookingService.getBookingById(id)
                .thenApply(ResponseEntity::ok);
    }

    @PostMapping
    public CompletableFuture<ResponseEntity<BookingResponseDTO>> createBooking(
            @Valid @RequestBody BookingRequestDTO request) {
        return bookingService.createBooking(request)
                .thenApply(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    public CompletableFuture<ResponseEntity<BookingResponseDTO>> cancelBooking(
            @PathVariable Long id) {
        return bookingService.cancelBooking(id)
                .thenApply(ResponseEntity::ok);
    }
}