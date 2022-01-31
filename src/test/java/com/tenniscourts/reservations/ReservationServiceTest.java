package com.tenniscourts.reservations;

import com.tenniscourts.exceptions.AlreadyExistsEntityException;
import com.tenniscourts.exceptions.EntityNotFoundException;
import com.tenniscourts.guests.Guest;
import com.tenniscourts.guests.GuestRepository;
import com.tenniscourts.schedules.Schedule;
import com.tenniscourts.schedules.ScheduleRepository;
import com.tenniscourts.tenniscourts.TennisCourt;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class ReservationServiceTest {

    private static final Long GUEST_ID = 1L;
    private static final Long SCHEDULE_ID = 2L;
    private static final Long RESERVATION_ID = 3L;
    private static final String START_DATE = "2022-02-29 21:00:00";
    private static final String BEFORE_START_DATE = "2021-01-29 21:00:00";
    private static final String END_DATE = "2022-02-29 22:00:00";

    @InjectMocks
    private ReservationService reservationService;
    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private GuestRepository guestRepository;
    @Mock
    private ScheduleRepository scheduleRepository;
    @Mock
    private ReservationMapper reservationMapper;

    @Test(expected = EntityNotFoundException.class)
    public void testBookReservationThrowEntityNotFoundExceptionForGuest() {
        final CreateReservationRequestDTO dto = new CreateReservationRequestDTO(GUEST_ID, SCHEDULE_ID);

        Mockito.when(guestRepository.findById(GUEST_ID)).thenReturn(Optional.empty());

        reservationService.bookReservation(dto);
    }

    @Test(expected = EntityNotFoundException.class)
    public void testBookReservationThrowEntityNotFoundExceptionForSchedule() {
        final CreateReservationRequestDTO dto = new CreateReservationRequestDTO(GUEST_ID, SCHEDULE_ID);
        final Guest guest = new Guest();
        guest.setId(GUEST_ID);
        guest.setName("Guest name");

        Mockito.when(guestRepository.findById(GUEST_ID)).thenReturn(Optional.of(guest));
        Mockito.when(scheduleRepository.findById(SCHEDULE_ID)).thenReturn(Optional.empty());

        reservationService.bookReservation(dto);
    }

    @Test(expected = AlreadyExistsEntityException.class)
    public void testBookReservationThrowAlreadyExistsEntityException() {
        final CreateReservationRequestDTO dto = new CreateReservationRequestDTO(GUEST_ID, SCHEDULE_ID);
        final Guest guest = new Guest();
        guest.setId(GUEST_ID);
        guest.setName("Guest name");

        final Schedule schedule = new Schedule();
        schedule.setId(SCHEDULE_ID);
        schedule.setTennisCourt(new TennisCourt());
        schedule.setStartDateTime(LocalDateTime.parse(START_DATE, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        schedule.setEndDateTime(LocalDateTime.parse(END_DATE, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        Mockito.when(guestRepository.findById(GUEST_ID)).thenReturn(Optional.of(guest));
        Mockito.when(scheduleRepository.findById(SCHEDULE_ID)).thenReturn(Optional.of(schedule));
        Mockito.when(reservationRepository.findReservationByReservationStatusAndGuestIdAndScheduleId(ReservationStatus.READY_TO_PLAY, GUEST_ID, SCHEDULE_ID)).thenReturn(Optional.of(new Reservation()));

        reservationService.bookReservation(dto);
    }

    @Test
    public void testBookReservation() {
        final CreateReservationRequestDTO dto = new CreateReservationRequestDTO(GUEST_ID, SCHEDULE_ID);
        final Guest guest = new Guest();
        guest.setId(GUEST_ID);
        guest.setName("Guest name");

        final Schedule schedule = new Schedule();
        schedule.setId(SCHEDULE_ID);
        schedule.setTennisCourt(new TennisCourt());
        schedule.setStartDateTime(LocalDateTime.parse(START_DATE, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        schedule.setEndDateTime(LocalDateTime.parse(END_DATE, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        Mockito.when(guestRepository.findById(GUEST_ID)).thenReturn(Optional.of(guest));
        Mockito.when(scheduleRepository.findById(SCHEDULE_ID)).thenReturn(Optional.of(schedule));
        Mockito.when(reservationRepository.findReservationByReservationStatusAndGuestIdAndScheduleId(ReservationStatus.READY_TO_PLAY, GUEST_ID, SCHEDULE_ID)).thenReturn(Optional.empty());
        Mockito.when(reservationMapper.map(Mockito.any(Reservation.class))).thenReturn(new ReservationDTO());

        Mockito.doAnswer(invocation -> {
            final Reservation reservation = invocation.getArgument(0);

            Assert.assertNotNull(reservation);
            Assert.assertNotNull(reservation.getGuest());
            Assert.assertNotNull(reservation.getSchedule());
            Assert.assertEquals(GUEST_ID, reservation.getGuest().getId());
            Assert.assertEquals(SCHEDULE_ID, reservation.getSchedule().getId());

            return reservation;
        }).when(reservationRepository).save(Mockito.any(Reservation.class));

        final ReservationDTO result = reservationService.bookReservation(dto);

        Assert.assertNotNull(result);
        Mockito.verify(reservationMapper, Mockito.times(1)).map(Mockito.any(Reservation.class));
        Mockito.verify(reservationRepository, Mockito.times(1)).save(Mockito.any(Reservation.class));
    }

    @Test
    public void testFindReservation() {
        Mockito.when(reservationRepository.findById(RESERVATION_ID)).thenReturn(Optional.of(new Reservation()));
        Mockito.when(reservationMapper.map(Mockito.any(Reservation.class))).thenReturn(new ReservationDTO());

        reservationService.findReservation(RESERVATION_ID);

        Mockito.verify(reservationRepository, Mockito.times(1)).findById(RESERVATION_ID);
    }

    @Test(expected = EntityNotFoundException.class)
    public void testFindReservationThrowEntityNotFoundException() {
        Mockito.when(reservationRepository.findById(RESERVATION_ID)).thenReturn(Optional.empty());

        reservationService.findReservation(RESERVATION_ID);
    }

    @Test(expected = EntityNotFoundException.class)
    public void testCancelReservationThrowEntityNotFoundException() {
        Mockito.when(reservationRepository.findById(RESERVATION_ID)).thenReturn(Optional.empty());

        reservationService.cancelReservation(RESERVATION_ID);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCancelReservationWrongReservationStatusThrowIllegalArgumentException() {
        final Guest guest = new Guest();
        guest.setId(GUEST_ID);
        guest.setName("Guest name");

        final Schedule schedule = new Schedule();
        schedule.setId(SCHEDULE_ID);
        schedule.setTennisCourt(new TennisCourt());
        schedule.setStartDateTime(LocalDateTime.parse(START_DATE, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        schedule.setEndDateTime(LocalDateTime.parse(END_DATE, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        final Reservation reservation = new Reservation();
        reservation.setSchedule(schedule);
        reservation.setGuest(guest);
        reservation.setValue(new BigDecimal(10));
        reservation.setRefundValue(new BigDecimal(0));
        reservation.setReservationStatus(ReservationStatus.CANCELLED);

        Mockito.when(reservationRepository.findById(RESERVATION_ID)).thenReturn(Optional.of(reservation));

        reservationService.cancelReservation(RESERVATION_ID);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCancelReservationWrongStartDateThrowIllegalArgumentException() {
        final Guest guest = new Guest();
        guest.setId(GUEST_ID);
        guest.setName("Guest name");

        final Schedule schedule = new Schedule();
        schedule.setId(SCHEDULE_ID);
        schedule.setTennisCourt(new TennisCourt());
        schedule.setStartDateTime(LocalDateTime.parse(BEFORE_START_DATE, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        schedule.setEndDateTime(schedule.getStartDateTime().plusHours(1));

        final Reservation reservation = new Reservation();
        reservation.setSchedule(schedule);
        reservation.setGuest(guest);
        reservation.setValue(new BigDecimal(10));
        reservation.setRefundValue(new BigDecimal(0));
        reservation.setReservationStatus(ReservationStatus.READY_TO_PLAY);

        Mockito.when(reservationRepository.findById(RESERVATION_ID)).thenReturn(Optional.of(reservation));

        reservationService.cancelReservation(RESERVATION_ID);
    }

    @Test
    public void testCancelReservation() {
        final Guest guest = new Guest();
        guest.setId(GUEST_ID);
        guest.setName("Guest name");

        final Schedule schedule = new Schedule();
        schedule.setId(SCHEDULE_ID);
        schedule.setTennisCourt(new TennisCourt());
        schedule.setStartDateTime(LocalDateTime.parse(START_DATE, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        schedule.setEndDateTime(schedule.getStartDateTime().plusHours(1));

        final Reservation reservation = new Reservation();
        reservation.setSchedule(schedule);
        reservation.setGuest(guest);
        reservation.setValue(new BigDecimal(10));
        reservation.setRefundValue(new BigDecimal(0));
        reservation.setReservationStatus(ReservationStatus.READY_TO_PLAY);

        Mockito.when(reservationRepository.findById(RESERVATION_ID)).thenReturn(Optional.of(reservation));
        Mockito.doAnswer(invocation -> {
            final Reservation invokedReservation = invocation.getArgument(0);

            Assert.assertNotNull(invokedReservation);
            Assert.assertNotNull(invokedReservation.getGuest());
            Assert.assertNotNull(invokedReservation.getSchedule());
            Assert.assertEquals(GUEST_ID, invokedReservation.getGuest().getId());
            Assert.assertEquals(SCHEDULE_ID, invokedReservation.getSchedule().getId());

            return invokedReservation;
        }).when(reservationRepository).save(Mockito.any(Reservation.class));

        reservationService.cancelReservation(RESERVATION_ID);

        Mockito.verify(reservationRepository, Mockito.times(1)).save(Mockito.any(Reservation.class));
    }
}