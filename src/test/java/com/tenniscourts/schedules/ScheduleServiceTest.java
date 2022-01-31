package com.tenniscourts.schedules;

import com.tenniscourts.exceptions.AlreadyExistsEntityException;
import com.tenniscourts.exceptions.EntityNotFoundException;
import com.tenniscourts.reservations.Reservation;
import com.tenniscourts.reservations.ReservationRepository;
import com.tenniscourts.tenniscourts.TennisCourt;
import com.tenniscourts.tenniscourts.TennisCourtDTO;
import com.tenniscourts.tenniscourts.TennisCourtRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RunWith(MockitoJUnitRunner.class)
public class ScheduleServiceTest {

    private static final Long TENNIS_COURT_ID = 1L;
    private static final Long SCHEDULE_ID = 2L;
    private static final String START_DATE = "2022-01-29 21:00:00";
    private static final String END_DATE = "2022-01-29 22:00:00";

    @InjectMocks
    private ScheduleService scheduleService;
    @Mock
    private ScheduleRepository scheduleRepository;
    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private TennisCourtRepository tennisCourtRepository;
    @Mock
    private ScheduleMapper scheduleMapper;

    @Test
    public void testAddSchedule() {
        final CreateScheduleRequestDTO dto = new CreateScheduleRequestDTO();
        dto.setTennisCourtId(TENNIS_COURT_ID);
        dto.setStartDateTime(LocalDateTime.parse(START_DATE, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        final TennisCourt tennisCourt =  new TennisCourt();
        tennisCourt.setId(1L);
        tennisCourt.setName("Tennis court name");
        final Optional<TennisCourt> optionalTennisCourt = Optional.of(tennisCourt);

        final ScheduleDTO scheduleDTO = new ScheduleDTO();
        scheduleDTO.setId(TENNIS_COURT_ID);
        scheduleDTO.setStartDateTime(dto.getStartDateTime());

        Mockito.when(tennisCourtRepository.findById(Mockito.anyLong())).thenReturn(optionalTennisCourt);
        Mockito.when(scheduleRepository.findScheduleByTennisCourtAndStartDateTimeAndEndDateTime(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(Optional.empty());
        Mockito.when(scheduleMapper.map(Mockito.any(Schedule.class))).thenReturn(scheduleDTO);
        Mockito.when(scheduleRepository.save(Mockito.any())).thenReturn(new Schedule());

        scheduleService.addSchedule(dto);

        Mockito.verify(scheduleRepository, Mockito.times(1)).save(Mockito.any());
        Mockito.verify(scheduleMapper, Mockito.times(1)).map(Mockito.any(Schedule.class));
    }

    @Test(expected = EntityNotFoundException.class)
    public void testAddScheduleThrowEntityNotFoundException() {
        final CreateScheduleRequestDTO dto = new CreateScheduleRequestDTO();
        dto.setTennisCourtId(TENNIS_COURT_ID);
        dto.setStartDateTime(LocalDateTime.parse(START_DATE, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        Mockito.when(tennisCourtRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        scheduleService.addSchedule(dto);
    }

    @Test(expected = AlreadyExistsEntityException.class)
    public void testAddScheduleThrowAlreadyExistsEntityException() {
        final CreateScheduleRequestDTO dto = new CreateScheduleRequestDTO();
        dto.setTennisCourtId(TENNIS_COURT_ID);
        dto.setStartDateTime(LocalDateTime.parse(START_DATE, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        final TennisCourt tennisCourt =  new TennisCourt();
        tennisCourt.setId(1L);
        tennisCourt.setName("Tennis court name");
        final Optional<TennisCourt> optionalTennisCourt = Optional.of(tennisCourt);

        Mockito.when(tennisCourtRepository.findById(Mockito.anyLong())).thenReturn(optionalTennisCourt);
        Mockito.when(scheduleRepository.findScheduleByTennisCourtAndStartDateTimeAndEndDateTime(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(Optional.of(new Schedule()));

        scheduleService.addSchedule(dto);
    }

    @Test
    public void testFindSchedulesByDates() {
        final List<Reservation> reservations = Arrays.asList(new Reservation(), new Reservation());
        final List<Schedule> schedules = Arrays.asList(new Schedule(), new Schedule());

        Mockito.when(reservationRepository.findAllByReservationStatusIn(Mockito.any())).thenReturn(reservations);
        Mockito.when(scheduleRepository.findScheduleByStartDateTimeAfterAndEndDateTimeBeforeAndReservationsNullOrReservationsIn(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(schedules);
        Mockito.when(scheduleMapper.map(schedules)).thenReturn(new ArrayList<>());

        final LocalDateTime startDate = LocalDateTime.parse(START_DATE, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        final LocalDateTime endDate = startDate.plusHours(1);
        scheduleService.findSchedulesByDates(startDate, endDate);

        Mockito.verify(scheduleRepository, Mockito.times(1)).findScheduleByStartDateTimeAfterAndEndDateTimeBeforeAndReservationsNullOrReservationsIn(startDate, endDate, reservations);
    }

    @Test
    public void testFindAllFreeSchedules() {
        final List<Reservation> reservations = Arrays.asList(new Reservation(), new Reservation());
        final List<Schedule> schedules = Arrays.asList(new Schedule(), new Schedule());

        Mockito.when(reservationRepository.findAllByReservationStatusIn(Mockito.any())).thenReturn(reservations);
        Mockito.when(scheduleRepository.findScheduleByReservationsNullOrReservationsIn(reservations)).thenReturn(schedules);
        Mockito.when(scheduleMapper.map(schedules)).thenReturn(new ArrayList<>());

        scheduleService.findAllFreeSchedules();

        Mockito.verify(scheduleRepository, Mockito.times(1)).findScheduleByReservationsNullOrReservationsIn(reservations);
    }

    @Test
    public void testFindById() {
        final Schedule schedule = new Schedule();
        schedule.setTennisCourt(new TennisCourt("Test"));

        final ScheduleDTO dto = new ScheduleDTO();
        final TennisCourtDTO tennisCourtDTO = new TennisCourtDTO();
        tennisCourtDTO.setName("Test");
        dto.setTennisCourt(tennisCourtDTO);

        Mockito.when(scheduleRepository.findById(SCHEDULE_ID)).thenReturn(Optional.of(schedule));
        Mockito.when(scheduleMapper.map(schedule)).thenReturn(dto);

        final ScheduleDTO result = scheduleService.findScheduleById(SCHEDULE_ID);

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getTennisCourt());
        Assert.assertEquals(dto.getTennisCourt().getName(), result.getTennisCourt().getName());
        Mockito.verify(scheduleRepository, Mockito.times(1)).findById(SCHEDULE_ID);
    }

    @Test(expected = EntityNotFoundException.class)
    public void testFindByIdThrowEntityNotFoundException() {
        Mockito.when(scheduleRepository.findById(SCHEDULE_ID)).thenReturn(Optional.empty());
        scheduleService.findScheduleById(SCHEDULE_ID);
    }

    @Test
    public void testFindSchedulesByTennisCourtId() {
        final TennisCourt tennisCourt = new TennisCourt();
        tennisCourt.setId(TENNIS_COURT_ID);
        tennisCourt.setName("Test Tennis Court");

        final Schedule schedule1 = new Schedule();
        schedule1.setTennisCourt(tennisCourt);
        schedule1.setStartDateTime(LocalDateTime.parse(START_DATE, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        schedule1.setEndDateTime(LocalDateTime.parse(END_DATE, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        final List<Schedule> schedules = Collections.singletonList(schedule1);

        Mockito.when(scheduleRepository.findByTennisCourt_IdOrderByStartDateTime(TENNIS_COURT_ID)).thenReturn(schedules);
        Mockito.when(scheduleMapper.map(schedules)).thenReturn(Collections.singletonList(new ScheduleDTO()));

        final List<ScheduleDTO> result = scheduleService.findSchedulesByTennisCourtId(TENNIS_COURT_ID);

        Assert.assertNotNull(result);
        Assert.assertEquals(schedules.size(), result.size());
        Mockito.verify(scheduleRepository, Mockito.times(1)).findByTennisCourt_IdOrderByStartDateTime(TENNIS_COURT_ID);
    }
}
