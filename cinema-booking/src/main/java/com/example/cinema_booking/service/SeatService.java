package com.example.cinema_booking.service;

import com.example.cinema_booking.dto.response.SeatResponseDTO;
import com.example.cinema_booking.entity.Seat;
import com.example.cinema_booking.enums.SeatStatus;
import com.example.cinema_booking.exception.BookingException;
import com.example.cinema_booking.exception.ResourceNotFoundException;
import com.example.cinema_booking.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeatService {
    private final SeatRepository seatRepository;
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);
    private final ConcurrentHashMap<Long, ReentrantLock> seatLocks = new ConcurrentHashMap<>();

    public CompletableFuture<List<SeatResponseDTO>> getAvailableSeats(Long screeningId) {
        return CompletableFuture.supplyAsync(() ->
            seatRepository.findByScreeningIdAndStatus(screeningId, SeatStatus.AVAILABLE)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList()),
            executorService
        );
    }

    @Transactional
    public CompletableFuture<SeatResponseDTO> lockSeat(Long seatId) {
        return CompletableFuture.supplyAsync(() -> {
            ReentrantLock seatLock = seatLocks.computeIfAbsent(seatId, k -> new ReentrantLock());
            
            try {
                if (!seatLock.tryLock(5, TimeUnit.SECONDS)) {
                    throw new BookingException("Unable to acquire lock for seat");
                }

                Seat seat = seatRepository.findByIdWithLock(seatId)
                    .orElseThrow(() -> new ResourceNotFoundException("Seat not found"));

                if (seat.getStatus() != SeatStatus.AVAILABLE) {
                    throw new BookingException("Seat is not available");
                }

                seat.setStatus(SeatStatus.LOCKED);
                seat.getIsLoocked().set(true);
                
                seat = seatRepository.save(seat);
                return convertToDTO(seat);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new BookingException("Seat locking was interrupted");
            } finally {
                if (seatLock.isHeldByCurrentThread()) {
                    seatLock.unlock();
                }
            }
        }, executorService);
    }

    @Transactional
    public CompletableFuture<SeatResponseDTO> unlockSeat(Long seatId) {
        return CompletableFuture.supplyAsync(() -> {
            ReentrantLock seatLock = seatLocks.computeIfAbsent(seatId, k -> new ReentrantLock());
            
            try {
                if (!seatLock.tryLock(5, TimeUnit.SECONDS)) {
                    throw new BookingException("Unable to acquire lock for seat");
                }

                Seat seat = seatRepository.findByIdWithLock(seatId)
                    .orElseThrow(() -> new ResourceNotFoundException("Seat not found"));

                if (!seat.getIsLoocked().get()) {
                    throw new BookingException("Seat is not locked");
                }

                seat.setStatus(SeatStatus.AVAILABLE);
                seat.getIsLoocked().set(false);
                
                seat = seatRepository.save(seat);
                return convertToDTO(seat);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new BookingException("Seat unlocking was interrupted");
            } finally {
                if (seatLock.isHeldByCurrentThread()) {
                    seatLock.unlock();
                }
            }
        }, executorService);
    }

    @Transactional
    public CompletableFuture<SeatResponseDTO> updateSeatStatus(Long seatId, SeatStatus status) {
        return CompletableFuture.supplyAsync(() -> {
            ReentrantLock seatLock = seatLocks.computeIfAbsent(seatId, k -> new ReentrantLock());
            
            try {
                if (!seatLock.tryLock(5, TimeUnit.SECONDS)) {
                    throw new BookingException("Unable to acquire lock for seat");
                }

                Seat seat = seatRepository.findByIdWithLock(seatId)
                    .orElseThrow(() -> new ResourceNotFoundException("Seat not found"));

                seat.setStatus(status);
                if (status == SeatStatus.AVAILABLE) {
                    seat.getIsLoocked().set(false);
                }
                
                seat = seatRepository.save(seat);
                return convertToDTO(seat);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new BookingException("Seat status update was interrupted");
            } finally {
                if (seatLock.isHeldByCurrentThread()) {
                    seatLock.unlock();
                }
            }
        }, executorService);
    }

    private SeatResponseDTO convertToDTO(Seat seat) {
        return SeatResponseDTO.builder()
                .id(seat.getId())
                .seatNumber(seat.getSeatNumber())
                .rowNumber(seat.getRowNumber())
                .status(seat.getStatus())
                .build();
    }
} 