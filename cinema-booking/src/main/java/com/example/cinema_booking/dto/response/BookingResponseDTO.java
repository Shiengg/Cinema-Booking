package com.example.cinema_booking.dto.response;

import com.example.cinema_booking.enums.BookingStatus;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponseDTO {
    private Long id;
    private String customerName;
    private String movieTitle;
    private LocalDateTime screeningTime;
    private String seatNumber;
    private BookingStatus status;
    private double totalPrice;
    private LocalDateTime bookingTime;
    private LocalDateTime expirationTime;
}
