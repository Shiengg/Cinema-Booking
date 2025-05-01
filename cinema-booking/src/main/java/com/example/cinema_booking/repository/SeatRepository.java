package com.example.cinema_booking.repository;

import com.example.cinema_booking.entity.Seat;
import com.example.cinema_booking.enums.SeatStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Seat s WHERE s.id = :id")
    Optional<Seat> findByIdWithLock(@Param("id") Long id);

    @Query("SELECT s FROM Seat s WHERE s.screening.id = :screeningId AND s.status = :status")
    List<Seat> findByScreeningIdAndStatus(
            @Param("screeningId") Long screeningId,
            @Param("status") SeatStatus status);

    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("SELECT s FROM Seat s WHERE s.screening.id = :screeningId")
    List<Seat> findByScreeningIdWithLock(@Param("screeningId") Long screeningId);
}