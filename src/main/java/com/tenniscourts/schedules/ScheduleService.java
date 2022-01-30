package com.tenniscourts.schedules;

import com.tenniscourts.exceptions.AlreadyExistsEntityException;
import com.tenniscourts.exceptions.EntityNotFoundException;
import com.tenniscourts.reservations.Reservation;
import com.tenniscourts.reservations.ReservationRepository;
import com.tenniscourts.reservations.ReservationStatus;
import com.tenniscourts.tenniscourts.TennisCourt;
import com.tenniscourts.tenniscourts.TennisCourtRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ReservationRepository reservationRepository;
    private final TennisCourtRepository tennisCourtRepository;
    private final ScheduleMapper scheduleMapper;

    public ScheduleDTO addSchedule(final Long tennisCourtId, final CreateScheduleRequestDTO createScheduleRequestDTO) {

        final Optional<TennisCourt> optionalTennisCourt = tennisCourtRepository.findById(tennisCourtId);
        return optionalTennisCourt.map(tennisCourt -> {
            final Schedule schedule = new Schedule();
            schedule.setTennisCourt(tennisCourt);
            schedule.setStartDateTime(createScheduleRequestDTO.getStartDateTime().truncatedTo(ChronoUnit.MINUTES));
            schedule.setEndDateTime(schedule.getStartDateTime().plusHours(1));

            final Optional<Schedule> existingSchedule = scheduleRepository.findScheduleByTennisCourtAndStartDateTimeAndEndDateTime(tennisCourt, schedule.getStartDateTime(), schedule.getEndDateTime());
            if (existingSchedule.isPresent()) {
                throw new AlreadyExistsEntityException(String.format("Schedule already exists for tennis court id = %s, startDateTime = %s and endDateTime = %s.", tennisCourtId, schedule.getStartDateTime(), schedule.getEndDateTime()));
            }
            return scheduleMapper.map(scheduleRepository.save(schedule));
        }).orElseThrow(() -> new EntityNotFoundException(String.format("Tennis court not found for id = %s.", tennisCourtId)));
    }

    public List<ScheduleDTO> findSchedulesByDates(final LocalDateTime startDate, final LocalDateTime endDate) {
        final List<Reservation> reservations = reservationRepository.findAllByReservationStatusIn(Arrays.asList(ReservationStatus.RESCHEDULED, ReservationStatus.CANCELLED));
        return scheduleMapper.map(scheduleRepository.findScheduleByStartDateTimeAfterAndEndDateTimeBeforeAndReservationsNullOrReservationsIn(startDate, endDate, reservations));
    }

    public List<ScheduleDTO> findAllFreeSchedules() {
        final List<Reservation> reservations = reservationRepository.findAllByReservationStatusIn(Arrays.asList(ReservationStatus.RESCHEDULED, ReservationStatus.CANCELLED));
        return scheduleMapper.map(scheduleRepository.findScheduleByReservationsNullOrReservationsIn(reservations));
    }

    public ScheduleDTO findScheduleById(final Long id) {
        return scheduleRepository.findById(id).map(scheduleMapper::map).orElseThrow(() -> new EntityNotFoundException(String.format("Schedule not found for id = %s.", id)));
    }

    public List<ScheduleDTO> findSchedulesByTennisCourtId(Long tennisCourtId) {
        return scheduleMapper.map(scheduleRepository.findByTennisCourt_IdOrderByStartDateTime(tennisCourtId));
    }
}
