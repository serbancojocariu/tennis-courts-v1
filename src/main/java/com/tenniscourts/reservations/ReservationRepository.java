package com.tenniscourts.reservations;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findBySchedule_Id(Long scheduleId);

    List<Reservation> findByReservationStatusAndSchedule_StartDateTimeGreaterThanEqualAndSchedule_EndDateTimeLessThanEqual(ReservationStatus reservationStatus, LocalDateTime startDateTime, LocalDateTime endDateTime);

//    List<Reservation> findByStartDateTimeGreaterThanEqualAndEndDateTimeLessThanEqualAndTennisCourt(LocalDateTime startDateTime, LocalDateTime endDateTime, TennisCourt tennisCourt);

    Optional<Reservation> findReservationByReservationStatusAndGuestIdAndScheduleId(ReservationStatus status, Long guestId, Long scheduleId);

    List<Reservation> findAllByReservationStatusIn(List<ReservationStatus> status);

    @Query(value = "select r from Reservation as r join Schedule as s on r.schedule.id = s.id where s.endDateTime < ?1")
    List<Reservation> findReservationsByScheduleStartDateBeforeNow(LocalDateTime localDateTime);
}
