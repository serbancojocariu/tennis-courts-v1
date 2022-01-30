package com.tenniscourts.tenniscourts;

import com.tenniscourts.config.BaseRestController;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/tennis-courts")
@RequiredArgsConstructor
public class TennisCourtController extends BaseRestController {

    private final TennisCourtService tennisCourtService;

    @PostMapping
    @ApiOperation(value = "Create tennis court.")
    public ResponseEntity<Void> addTennisCourt(@RequestBody final TennisCourtDTO tennisCourtDTO) {
        return ResponseEntity.created(locationByEntity(tennisCourtService.addTennisCourt(tennisCourtDTO).getId())).build();
    }

    @GetMapping(path = "{id}")
    @ApiOperation(value = "Find tennis court by id.")
    public ResponseEntity<TennisCourtDTO> findTennisCourtById(@PathVariable final Long id) {
        return ResponseEntity.ok(tennisCourtService.findTennisCourtById(id));
    }

    @GetMapping(path = "with-schedules/{id}")
    @ApiOperation(value = "Find tennis court with schedules by id.")
    public ResponseEntity<TennisCourtDTO> findTennisCourtWithSchedulesById(@PathVariable final Long id) {
        return ResponseEntity.ok(tennisCourtService.findTennisCourtWithSchedulesById(id));
    }
}
