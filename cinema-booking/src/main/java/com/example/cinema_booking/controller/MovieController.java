// controller/MovieController.java
package com.example.cinema_booking.controller;

import com.example.cinema_booking.dto.request.MovieRequestDTO;
import com.example.cinema_booking.dto.response.MovieResponseDTO;
import com.example.cinema_booking.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {
    private final MovieService movieService;

    @GetMapping
    public CompletableFuture<ResponseEntity<List<MovieResponseDTO>>> getAllMovies() {
        return movieService.getAllMovies()
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/{id}")
    public CompletableFuture<ResponseEntity<MovieResponseDTO>> getMovie(@PathVariable Long id) {
        return movieService.getMovieById(id)
                .thenApply(ResponseEntity::ok);
    }

    @PostMapping
    public CompletableFuture<ResponseEntity<MovieResponseDTO>> createMovie(@RequestBody MovieRequestDTO request) {
        return movieService.createMovie(request)
                .thenApply(ResponseEntity::ok);
    }

    @PutMapping("/{id}")
    public CompletableFuture<ResponseEntity<MovieResponseDTO>> updateMovie(
            @PathVariable Long id,
            @RequestBody MovieRequestDTO request) {
        return movieService.updateMovie(id, request)
                .thenApply(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    public CompletableFuture<ResponseEntity<Void>> deleteMovie(@PathVariable Long id) {
        return movieService.deleteMovie(id)
                .thenApply(r -> ResponseEntity.ok().build());
    }
}
