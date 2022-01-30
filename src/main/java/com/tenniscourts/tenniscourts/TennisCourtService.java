package com.tenniscourts.tenniscourts;

import com.tenniscourts.exceptions.EntityNotFoundException;
import com.tenniscourts.schedules.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TennisCourtService {

    private final TennisCourtRepository tennisCourtRepository;
    private final ScheduleService scheduleService;
    private final TennisCourtMapper tennisCourtMapper;

    public TennisCourtDTO addTennisCourt(final TennisCourtDTO tennisCourtDTO) {
        return tennisCourtMapper.map(tennisCourtRepository.saveAndFlush(tennisCourtMapper.map(tennisCourtDTO)));
    }

    public TennisCourtDTO findTennisCourtById(final Long id) {
        return tennisCourtRepository.findById(id).map(tennisCourtMapper::map).orElseThrow(() -> new EntityNotFoundException("Tennis Court not found."));
    }

    public TennisCourtDTO findTennisCourtWithSchedulesById(final Long tennisCourtId) {
        final TennisCourtDTO tennisCourtDTO = findTennisCourtById(tennisCourtId);
        tennisCourtDTO.setTennisCourtSchedules(scheduleService.findSchedulesByTennisCourtId(tennisCourtId));
        return tennisCourtDTO;
    }
}
