package com.tenniscourts.schedules;

import com.tenniscourts.config.BaseRestController;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/schedules")
@RequiredArgsConstructor
public class ScheduleController extends BaseRestController {

    private final ScheduleService scheduleService;

    @PostMapping
    @ApiOperation(value = "Create schedule for a tennis court.")
    public ResponseEntity<Void> addScheduleTennisCourt(@RequestBody final CreateScheduleRequestDTO createScheduleRequestDTO) {
        return ResponseEntity.created(locationByEntity(scheduleService.addSchedule(createScheduleRequestDTO).getId())).build();
    }

    @GetMapping(path = "dates")
    @ApiOperation(value = "Find free schedules by dates.")
    public ResponseEntity<List<ScheduleDTO>> findSchedulesByDates(@RequestParam(name = "startDate") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startDate,
                                                                  @RequestParam(name = "endDate") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endDate) {
        return ResponseEntity.ok(scheduleService.findSchedulesByDates(startDate.minusMinutes(1), endDate.plusMinutes(1)));
    }

    @GetMapping(path = "all-free-schedules")
    @ApiOperation(value = "Find all free schedules.")
    public ResponseEntity<List<ScheduleDTO>> findAllFreeSchedules() {
        return ResponseEntity.ok(scheduleService.findAllFreeSchedules());
    }

    @GetMapping(path = "{id}")
    @ApiOperation(value = "Find schedule by id.")
    public ResponseEntity<ScheduleDTO> findByScheduleId(@PathVariable final Long id) {
        return ResponseEntity.ok(scheduleService.findScheduleById(id));
    }
}
