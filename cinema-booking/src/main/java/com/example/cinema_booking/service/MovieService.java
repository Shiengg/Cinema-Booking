package com.example.cinema_booking.service;

import com.example.cinema_booking.dto.request.MovieRequestDTO;
import com.example.cinema_booking.dto.response.MovieResponseDTO;
import com.example.cinema_booking.entity.Movie;
import com.example.cinema_booking.exception.ResourceNotFoundException;
import com.example.cinema_booking.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieService {
    private final MovieRepository movieRepository;
    private final ExecutorService executorService = Executors.newFixedThreadPool(5);

    @Transactional
    public CompletableFuture<MovieResponseDTO> createMovie(MovieRequestDTO request) {
        return CompletableFuture.supplyAsync(() -> {
            Movie movie = Movie.builder()
                    .title(request.getTitle())
                    .genre(request.getGenre())
                    .description(request.getDescription())
                    .ticketPrice(request.getTicketPrice())
                    .build();
            Movie savedMovie = movieRepository.save(movie);
            return convertToResponse(savedMovie);
        }, executorService);
    }

    public CompletableFuture<MovieResponseDTO> getMovieById(Long id) {
        return CompletableFuture.supplyAsync(() -> {
            Movie movie = movieRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + id));
            return convertToResponse(movie);
        }, executorService);
    }

    public CompletableFuture<List<MovieResponseDTO>> getAllMovies() {
        return CompletableFuture.supplyAsync(() -> 
            movieRepository.findAll().stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList()),
            executorService
        );
    }

    @Transactional
    public CompletableFuture<MovieResponseDTO> updateMovie(Long id, MovieRequestDTO request) {
        return CompletableFuture.supplyAsync(() -> {
            Movie movie = movieRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + id));
            
            movie.setTitle(request.getTitle());
            movie.setGenre(request.getGenre());
            movie.setDescription(request.getDescription());
            movie.setTicketPrice(request.getTicketPrice());
            
            Movie updatedMovie = movieRepository.save(movie);
            return convertToResponse(updatedMovie);
        }, executorService);
    }

    @Transactional
    public CompletableFuture<Void> deleteMovie(Long id) {
        return CompletableFuture.runAsync(() -> {
            if (!movieRepository.existsById(id)) {
                throw new ResourceNotFoundException("Movie not found with id: " + id);
            }
            movieRepository.deleteById(id);
        }, executorService);
    }

    private MovieResponseDTO convertToResponse(Movie movie) {
        return MovieResponseDTO.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .genre(movie.getGenre())
                .description(movie.getDescription())
                .ticketPrice(movie.getTicketPrice())
                .build();
    }
}
