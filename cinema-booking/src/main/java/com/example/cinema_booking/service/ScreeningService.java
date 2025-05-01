package com.example.cinema_booking.service;

import com.example.cinema_booking.dto.request.ScreeningRequestDTO;
import com.example.cinema_booking.dto.response.ScreeningResponseDTO;
import com.example.cinema_booking.dto.response.SeatResponseDTO;
import com.example.cinema_booking.entity.Movie;
import com.example.cinema_booking.entity.Screening;
import com.example.cinema_booking.entity.Seat;
import com.example.cinema_booking.enums.SeatStatus;
import com.example.cinema_booking.exception.ResourceNotFoundException;
import com.example.cinema_booking.repository.MovieRepository;
import com.example.cinema_booking.repository.ScreeningRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class ScreeningService {
    private final ScreeningRepository screeningRepository;
    private final MovieRepository movieRepository;
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public CompletableFuture<List<ScreeningResponseDTO>> getAllScreenings() {
        return CompletableFuture.supplyAsync(() -> {
            lock.readLock().lock();
            try {
                return screeningRepository.findAll().stream()
                        .map(this::convertToDTO)
                        .collect(Collectors.toList());
            } finally {
                lock.readLock().unlock();
            }
        }, executorService);
    }

    public CompletableFuture<ScreeningResponseDTO> getScreeningWithSeats(Long id) {
        return CompletableFuture.supplyAsync(() -> {
            lock.readLock().lock();
            try {
                Screening screening = screeningRepository.findByIdWithLock(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Screening not found"));
                return convertToDTO(screening);
            } finally {
                lock.readLock().unlock();
            }
        }, executorService);
    }

    @Transactional
    public CompletableFuture<ScreeningResponseDTO> createScreening(ScreeningRequestDTO request) {
        return CompletableFuture.supplyAsync(() -> {
            lock.writeLock().lock();
            try {
                Movie movie = movieRepository.findById(request.getMovieId())
                        .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));

                Screening screening = Screening.builder()
                        .movie(movie)
                        .screeningTime(request.getScreeningTime())
                        .totalSeats(request.getTotalSeats())
                        .availableSeats(request.getTotalSeats())
                        .build();

                // Create seats for the screening
                createSeats(screening, request.getTotalSeats());

                screening = screeningRepository.save(screening);
                return convertToDTO(screening);
            } finally {
                lock.writeLock().unlock();
            }
        }, executorService);
    }

    @Transactional
    public CompletableFuture<ScreeningResponseDTO> updateScreening(Long id, ScreeningRequestDTO request) {
        return CompletableFuture.supplyAsync(() -> {
            lock.writeLock().lock();
            try {
                Screening screening = screeningRepository.findByIdWithLock(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Screening not found"));

                Movie movie = movieRepository.findById(request.getMovieId())
                        .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));

                screening.setMovie(movie);
                screening.setScreeningTime(request.getScreeningTime());

                // Only update seats if the total number has changed
                if (!screening.getTotalSeats().equals(request.getTotalSeats())) {
                    screening.setTotalSeats(request.getTotalSeats());
                    screening.setAvailableSeats(request.getTotalSeats());
                    screening.getSeats().clear();
                    createSeats(screening, request.getTotalSeats());
                }

                screening = screeningRepository.save(screening);
                return convertToDTO(screening);
            } finally {
                lock.writeLock().unlock();
            }
        }, executorService);
    }

    @Transactional
    public CompletableFuture<Void> deleteScreening(Long id) {
        return CompletableFuture.runAsync(() -> {
            lock.writeLock().lock();
            try {
                if (!screeningRepository.existsById(id)) {
                    throw new ResourceNotFoundException("Screening not found");
                }
                screeningRepository.deleteById(id);
            } finally {
                lock.writeLock().unlock();
            }
        }, executorService);
    }

    private void createSeats(Screening screening, int totalSeats) {
        IntStream.range(0, totalSeats)
                .parallel()
                .forEach(i -> {
                    Seat seat = Seat.builder()
                            .screening(screening)
                            .seatNumber(String.format("%d", i + 1))
                            .status(SeatStatus.AVAILABLE)
                            .build();
                    screening.getSeats().add(seat);
                });
    }

    private ScreeningResponseDTO convertToDTO(Screening screening) {
        List<SeatResponseDTO> seatDTOs = screening.getSeats().stream()
                .map(seat -> SeatResponseDTO.builder()
                        .id(seat.getId())
                        .seatNumber(seat.getSeatNumber())
                        .status(seat.getStatus())
                        .build())
                .collect(Collectors.toList());

        return ScreeningResponseDTO.builder()
                .id(screening.getId())
                .movieTitle(screening.getMovie().getTitle())
                .screeningTime(screening.getScreeningTime())
                .totalSeats(screening.getTotalSeats())
                .availableSeats(screening.getAvailableSeats())
                .seats(seatDTOs)
                .build();
    }
} 