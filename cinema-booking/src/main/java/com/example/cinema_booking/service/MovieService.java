package com.example.cinema_booking.service;

import com.example.cinema_booking.dto.request.MovieRequestDTO;
import com.example.cinema_booking.dto.response.MovieResponseDTO;
import com.example.cinema_booking.entity.Movie;
import com.example.cinema_booking.repository.MovieRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MovieService {
    MovieRepository movieRepository;

    public MovieResponseDTO createMovie(MovieRequestDTO request){
        Movie movie = Movie.builder()
                .title(request.getTitle())
                .genre(request.getGenre())
                .description(request.getDescription())
                .ticketPrice(request.getTicketPrice())
                .build();
        Movie savedMovie = movieRepository.save(movie);
        return convertToResponse(savedMovie);
    }

    public String getMovieById(){
        return "Getted";
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
