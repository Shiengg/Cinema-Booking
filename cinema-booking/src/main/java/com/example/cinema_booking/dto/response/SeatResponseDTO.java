package com.example.cinema_booking.dto.response;

import com.example.cinema_booking.enums.SeatStatus;
import lombok.*;

@Data @Builder
@NoArgsConstructor @AllArgsConstructor
public class SeatResponseDTO {
    private Long id;
    private String seatNumber;
    private String rowNumber;
    private SeatStatus status;
}