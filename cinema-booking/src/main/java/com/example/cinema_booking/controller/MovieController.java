package com.example.cinema_booking.controller;

import com.example.cinema_booking.dto.request.MovieRequestDTO;
import com.example.cinema_booking.dto.response.MovieResponseDTO;
import com.example.cinema_booking.service.MovieService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid; // Spring Boot 3
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movies")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MovieController {
    MovieService movieService;

    @PostMapping
    public ResponseEntity<MovieResponseDTO> createMovie(@Valid @RequestBody MovieRequestDTO request){
        return ResponseEntity.status(HttpStatus.CREATED).body(movieService.createMovie(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieResponseDTO> getMovie(@PathVariable Long id){
        return ResponseEntity.ok(movieService.getMovieById(id));
    }

    @GetMapping
    public ResponseEntity<List<MovieResponseDTO>> getAllMovie(){
        return ResponseEntity.ok(movieService.getAllMovie());
    }
}
