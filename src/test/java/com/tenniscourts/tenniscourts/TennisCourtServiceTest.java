package com.tenniscourts.tenniscourts;

import com.tenniscourts.exceptions.EntityNotFoundException;
import com.tenniscourts.schedules.ScheduleDTO;
import com.tenniscourts.schedules.ScheduleService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class TennisCourtServiceTest {

    private static final Long TENNIS_COURT_ID = 1L;
    private static final String START_DATE = "2022-01-29 21:00:00";
    private static final String END_DATE = "2022-01-29 22:00:00";

    @InjectMocks
    private TennisCourtService tennisCourtService;
    @Mock
    private TennisCourtRepository tennisCourtRepository;
    @Mock
    private ScheduleService scheduleService;
    @Mock
    private TennisCourtMapper tennisCourtMapper;

    @Test
    public void testAddTennisCourt() {

        final TennisCourt tennisCourt = new TennisCourt();
        tennisCourt.setName("Test Court Name");
        final TennisCourtDTO tennisCourtDTO = new TennisCourtDTO();
        tennisCourtDTO.setName("Test Court Name");

        Mockito.when(tennisCourtMapper.map(Mockito.any(TennisCourtDTO.class))).thenReturn(tennisCourt);
        Mockito.when(tennisCourtRepository.saveAndFlush(Mockito.any(TennisCourt.class))).thenReturn(tennisCourt);
        Mockito.when(tennisCourtMapper.map(tennisCourt)).thenReturn(tennisCourtDTO);

        tennisCourtService.addTennisCourt(tennisCourtDTO);

        Mockito.verify(tennisCourtRepository, Mockito.times(1)).saveAndFlush(tennisCourt);
    }

    @Test
    public void testFindTennisCourtById() {
        Mockito.when(tennisCourtRepository.findById(TENNIS_COURT_ID)).thenReturn(Optional.of(new TennisCourt()));
        Mockito.when(tennisCourtMapper.map(Mockito.any(TennisCourt.class))).thenReturn(new TennisCourtDTO());

        tennisCourtService.findTennisCourtById(TENNIS_COURT_ID);

        Mockito.verify(tennisCourtRepository, Mockito.times(1)).findById(TENNIS_COURT_ID);
    }

    @Test(expected = EntityNotFoundException.class)
    public void testFindTennisCourtByIdThrowEntityNotFoundException() {
        Mockito.when(tennisCourtRepository.findById(TENNIS_COURT_ID)).thenReturn(Optional.empty());

        tennisCourtService.findTennisCourtById(TENNIS_COURT_ID);
    }

    @Test
    public void testFindTennisCourtWithSchedulesById() {
        final TennisCourtDTO tennisCourtDTO = new TennisCourtDTO();
        tennisCourtDTO.setName("Test Tennis Court");

        final ScheduleDTO scheduleDTO = new ScheduleDTO();
        scheduleDTO.setTennisCourt(tennisCourtDTO);
        scheduleDTO.setStartDateTime(LocalDateTime.parse(START_DATE, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        scheduleDTO.setEndDateTime(LocalDateTime.parse(END_DATE, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        Mockito.when(tennisCourtRepository.findById(TENNIS_COURT_ID)).thenReturn(Optional.of(new TennisCourt()));
        Mockito.when(tennisCourtMapper.map(Mockito.any(TennisCourt.class))).thenReturn(tennisCourtDTO);
        Mockito.when(scheduleService.findSchedulesByTennisCourtId(TENNIS_COURT_ID)).thenReturn(Collections.singletonList(scheduleDTO));

        final TennisCourtDTO result = tennisCourtService.findTennisCourtWithSchedulesById(TENNIS_COURT_ID);

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getTennisCourtSchedules());
        Assert.assertEquals(1L, result.getTennisCourtSchedules().size());
        Assert.assertEquals(result.getTennisCourtSchedules().get(0).getStartDateTime(), scheduleDTO.getStartDateTime());
        Assert.assertEquals(result.getTennisCourtSchedules().get(0).getEndDateTime(), scheduleDTO.getEndDateTime());
        Mockito.verify(scheduleService, Mockito.times(1)).findSchedulesByTennisCourtId(TENNIS_COURT_ID);
    }
}
