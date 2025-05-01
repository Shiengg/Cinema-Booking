package com.example.cinema_booking.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScreeningRequestDTO {
    @NotNull(message = "Movie ID is required")
    private Long movieId;

    @NotNull(message = "Screening time is required")
    @Future(message = "Screening time must be in the future")
    private LocalDateTime screeningTime;

    @NotNull(message = "Total seats is required")
    @Min(value = 1, message = "Total seats must be at least 1")
    private Integer totalSeats;
} 