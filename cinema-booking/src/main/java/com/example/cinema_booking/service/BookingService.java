package com.example.cinema_booking.service;

import com.example.cinema_booking.constants.BookingConstants;
import com.example.cinema_booking.dto.request.BookingRequestDTO;
import com.example.cinema_booking.dto.response.BookingResponseDTO;
import com.example.cinema_booking.entity.Booking;
import com.example.cinema_booking.entity.Seat;
import com.example.cinema_booking.enums.BookingStatus;
import com.example.cinema_booking.enums.SeatStatus;
import com.example.cinema_booking.exception.BookingException;
import com.example.cinema_booking.repository.BookingRepository;
import com.example.cinema_booking.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final SeatRepository seatRepository;
    private final BookingRepository bookingRepository;
    private final ExecutorService bookingExecutor =
            Executors.newFixedThreadPool(BookingConstants.MAX_CONCURRENT_BOOKINGS);
    private final ConcurrentHashMap<Long, ReentrantLock> seatLocks = new ConcurrentHashMap<>();

    @Transactional
    public CompletableFuture<BookingResponseDTO> createBooking(BookingRequestDTO request) {
        return CompletableFuture.supplyAsync(() -> {
            ReentrantLock seatLock = seatLocks.computeIfAbsent(
                    request.getSeatId(),
                    k -> new ReentrantLock()
            );

            try {
                if (!seatLock.tryLock(BookingConstants.SEAT_LOCK_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                    throw new BookingException("Unable to acquire lock for seat");
                }

                return processBooking(request);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new BookingException("Booking process was interrupted");
            } finally {
                if (seatLock.isHeldByCurrentThread()) {
                    seatLock.unlock();
                }
            }
        }, bookingExecutor);
    }

    private BookingResponseDTO processBooking(BookingRequestDTO request) {
        Seat seat = seatRepository.findByIdWithLock(request.getSeatId())
                .orElseThrow(() -> new BookingException("Seat not found"));

        if (seat.getStatus() != SeatStatus.AVAILABLE) {
            throw new BookingException("Seat is not available");
        }

        try {
            seat.setStatus(SeatStatus.LOCKED);
            seatRepository.save(seat);

            Booking booking = createBookingEntity(request, seat);
            booking = bookingRepository.save(booking);

            seat.setStatus(SeatStatus.BOOKED);
            seat.setCurrentBooking(booking);
            seatRepository.save(seat);

            return convertToDTO(booking);
        } catch (Exception e) {
            seat.setStatus(SeatStatus.AVAILABLE);
            seatRepository.save(seat);
            throw new BookingException("Failed to process booking");
        }
    }

    private Booking createBookingEntity(BookingRequestDTO request, Seat seat) {
        return Booking.builder()
                .seat(seat)
                .customerName(request.getCustomerName())
                .customerEmail(request.getCustomerEmail())
                .customerPhone(request.getCustomerPhone())
                .bookingTime(LocalDateTime.now())
                .expirationTime(LocalDateTime.now().plusMinutes(BookingConstants.BOOKING_TIMEOUT_MINUTES))
                .status(BookingStatus.CONFIRMED)
                .totalPrice(seat.getScreening().getMovie().getTicketPrice())
                .build();
    }

    private BookingResponseDTO convertToDTO(Booking booking) {
        return BookingResponseDTO.builder()
                .id(booking.getId())
                .customerName(booking.getCustomerName())
                .movieTitle(booking.getSeat().getScreening().getMovie().getTitle())
                .screeningTime(booking.getSeat().getScreening().getScreeningTime())
                .seatNumber(booking.getSeat().getSeatNumber())
                .status(booking.getStatus())
                .totalPrice(booking.getTotalPrice())
                .bookingTime(booking.getBookingTime())
                .expirationTime(booking.getExpirationTime())
                .build();
    }
}