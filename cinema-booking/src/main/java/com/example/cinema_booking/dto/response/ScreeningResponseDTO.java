package com.example.cinema_booking.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data @Builder
@NoArgsConstructor @AllArgsConstructor
public class ScreeningResponseDTO {
    private Long id;
    private String movieTitle;
    private LocalDateTime screeningTime;
    private Integer totalSeats;
    private Integer availableSeats;
    private List<SeatResponseDTO> seats;
}