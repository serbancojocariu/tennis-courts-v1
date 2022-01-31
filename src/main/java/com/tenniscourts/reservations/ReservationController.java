package com.tenniscourts.reservations;

import com.tenniscourts.config.BaseRestController;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/reservations")
@RequiredArgsConstructor
public class ReservationController extends BaseRestController {

    private final ReservationService reservationService;

    @PostMapping
    @ApiOperation(value = "Create new reservation.")
    public ResponseEntity<Void> bookReservation(@RequestBody final CreateReservationRequestDTO createReservationRequestDTO) {
        return ResponseEntity.created(locationByEntity(reservationService.bookReservation(createReservationRequestDTO).getId())).build();
    }

    @GetMapping(path = "{id}")
    @ApiOperation(value = "Find reservation by id.")
    public ResponseEntity<ReservationDTO> findReservation(@PathVariable final Long id) {
        return ResponseEntity.ok(reservationService.findReservation(id));
    }

    @PutMapping(path = "cancel/{id}")
    @ApiOperation(value = "Cancel reservation by id.")
    public ResponseEntity<ReservationDTO> cancelReservation(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.cancelReservation(id));
    }

    @PutMapping(path = "reschedule/{reservationId}/{scheduleId}")
    public ResponseEntity<ReservationDTO> rescheduleReservation(@PathVariable final Long reservationId, @PathVariable final Long scheduleId) {
        return ResponseEntity.ok(reservationService.rescheduleReservation(reservationId, scheduleId));
    }

    @PutMapping("complete/{id}")
    @ApiOperation(value = "Complete reservation by id.")
    public ResponseEntity<ReservationDTO> completeReservation(@PathVariable final Long id) {
        return ResponseEntity.ok(reservationService.completeReservation(id));
    }
}
