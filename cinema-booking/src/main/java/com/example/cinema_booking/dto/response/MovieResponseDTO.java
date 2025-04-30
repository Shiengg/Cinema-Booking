package com.example.cinema_booking.dto.response;

import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MovieResponseDTO {
    Long id;
    String title;
    String genre;
    String description;
    double ticketPrice;
}
