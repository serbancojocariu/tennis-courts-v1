package com.tenniscourts.schedules;

import com.tenniscourts.config.BaseRestController;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping(path = "/schedules")
@RequiredArgsConstructor
public class ScheduleController extends BaseRestController {

    private final ScheduleService scheduleService;

    @PostMapping
    @ApiOperation(value = "Create schedule for a tennis court.")
    public ResponseEntity<Void> addScheduleTennisCourt(@RequestBody final CreateScheduleRequestDTO createScheduleRequestDTO) {
        return ResponseEntity.created(locationByEntity(scheduleService.addSchedule(createScheduleRequestDTO.getTennisCourtId(), createScheduleRequestDTO).getId())).build();
    }

    //TODO: implement rest and swagger
    public ResponseEntity<List<ScheduleDTO>> findSchedulesByDates(LocalDate startDate,
                                                                  LocalDate endDate) {
        return ResponseEntity.ok(scheduleService.findSchedulesByDates(LocalDateTime.of(startDate, LocalTime.of(0, 0)), LocalDateTime.of(endDate, LocalTime.of(23, 59))));
    }

    @GetMapping(path = "{id}")
    @ApiOperation(value = "Find schedule by id.")
    public ResponseEntity<ScheduleDTO> findByScheduleId(@PathVariable final Long id) {
        return ResponseEntity.ok(scheduleService.findScheduleById(id));
    }
}
