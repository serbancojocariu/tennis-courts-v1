package com.tenniscourts.schedules;

import com.tenniscourts.reservations.Reservation;
import com.tenniscourts.tenniscourts.TennisCourt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findByTennisCourt_IdOrderByStartDateTime(Long id);

    Optional<Schedule> findScheduleByTennisCourtAndStartDateTimeAndEndDateTime(TennisCourt tennisCourt, LocalDateTime startDateTime, LocalDateTime endDateTime);

    List<Schedule> findScheduleByReservationsNullOrReservationsIn(List<Reservation> reservations);

    List<Schedule> findScheduleByStartDateTimeAfterAndEndDateTimeBeforeAndReservationsNullOrReservationsIn(LocalDateTime startDateTime, LocalDateTime endDateTime, List<Reservation> reservations);
}