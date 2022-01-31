package com.tenniscourts.reservations;

import com.tenniscourts.exceptions.AlreadyExistsEntityException;
import com.tenniscourts.exceptions.EntityNotFoundException;
import com.tenniscourts.guests.Guest;
import com.tenniscourts.guests.GuestRepository;
import com.tenniscourts.schedules.Schedule;
import com.tenniscourts.schedules.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final GuestRepository guestRepository;
    private final ScheduleRepository scheduleRepository;
    private final ReservationMapper reservationMapper;

    public ReservationDTO bookReservation(final CreateReservationRequestDTO dto) {

        final Guest guest = guestRepository.findById(dto.getGuestId()).orElseThrow(() -> new EntityNotFoundException(String.format("Guest not found for id = %s.", dto.getGuestId())));
        final Schedule schedule = scheduleRepository.findById(dto.getScheduleId()).orElseThrow(() -> new EntityNotFoundException(String.format("Schedule not found for id = %s.", dto.getScheduleId())));

        final Optional<Reservation> existingReservation = reservationRepository.findReservationByReservationStatusAndGuestIdAndScheduleId(ReservationStatus.READY_TO_PLAY, guest.getId(), schedule.getId());
        if (existingReservation.isPresent()) {
            throw new AlreadyExistsEntityException(String.format("Reservation already exists for reservation status = %s, guest id = %s, schedule id = %s.", ReservationStatus.READY_TO_PLAY, guest.getId(), schedule.getId()));
        }

        final Reservation reservation = new Reservation();
        reservation.setGuest(guest);
        reservation.setSchedule(schedule);
        reservation.setValue(new BigDecimal(0));
        reservation.setReservationStatus(ReservationStatus.READY_TO_PLAY);
        schedule.addReservation(reservation);
        return reservationMapper.map(reservationRepository.save(reservation));
    }

    public ReservationDTO findReservation(final Long id) {
        return reservationRepository.findById(id).map(reservationMapper::map).orElseThrow(() -> new EntityNotFoundException(String.format("Reservation not found for id =%s.", id)));
    }

    public ReservationDTO cancelReservation(Long reservationId) {
        return reservationMapper.map(this.cancel(reservationId));
    }

    private Reservation cancel(Long reservationId) {
        return reservationRepository.findById(reservationId).map(reservation -> {

            this.validateCancellation(reservation);

            BigDecimal refundValue = getRefundValue(reservation);
            return this.updateReservation(reservation, refundValue, ReservationStatus.CANCELLED);

        }).orElseThrow(() -> new EntityNotFoundException("Reservation not found."));
    }

    private Reservation updateReservation(Reservation reservation, BigDecimal refundValue, ReservationStatus status) {
        reservation.setReservationStatus(status);
        reservation.setValue(reservation.getValue().subtract(refundValue));
        reservation.setRefundValue(refundValue);

        return reservationRepository.save(reservation);
    }

    private void validateCancellation(Reservation reservation) {
        if (!ReservationStatus.READY_TO_PLAY.equals(reservation.getReservationStatus())) {
            throw new IllegalArgumentException("Cannot cancel/reschedule because it's not in ready to play status.");
        }

        if (reservation.getSchedule().getStartDateTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Can cancel/reschedule only future dates.");
        }
    }

    public BigDecimal getRefundValue(Reservation reservation) {
        long hours = ChronoUnit.HOURS.between(LocalDateTime.now(), reservation.getSchedule().getStartDateTime());

        if (hours >= 24) {
            return reservation.getValue();
        }

        return BigDecimal.ZERO;
    }

    /*TODO: This method actually not fully working, find a way to fix the issue when it's throwing the error:
            "Cannot reschedule to the same slot.*/
    public ReservationDTO rescheduleReservation(Long previousReservationId, Long scheduleId) {
        Reservation previousReservation = cancel(previousReservationId);

        if (scheduleId.equals(previousReservation.getSchedule().getId())) {
            throw new IllegalArgumentException("Cannot reschedule to the same slot.");
        }

        previousReservation.setReservationStatus(ReservationStatus.RESCHEDULED);
        reservationRepository.save(previousReservation);

        ReservationDTO newReservation = bookReservation(CreateReservationRequestDTO.builder()
                .guestId(previousReservation.getGuest().getId())
                .scheduleId(scheduleId)
                .build());
        newReservation.setPreviousReservation(reservationMapper.map(previousReservation));
        return newReservation;
    }
}
