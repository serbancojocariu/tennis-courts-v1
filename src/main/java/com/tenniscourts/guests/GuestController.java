package com.tenniscourts.guests;

import com.tenniscourts.config.BaseRestController;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/guests")
@RequiredArgsConstructor
public class GuestController extends BaseRestController {

    private final GuestService guestService;

    @GetMapping(path = "{id}")
    @ApiOperation(value = "Find guest by id.")
    public ResponseEntity<GuestDTO> findById(@PathVariable final Long id) {
        return ResponseEntity.ok(guestService.findGuestById(id));
    }

    @GetMapping(path = "name", params = "name")
    @ApiOperation(value = "Find guest by name.")
    public ResponseEntity<GuestDTO> findByName(@RequestParam(name = "name") final String name) {
        return ResponseEntity.ok(guestService.findGuestByName(name));
    }

    @GetMapping
    @ApiOperation(value = "Find all guests.")
    public ResponseEntity<List<GuestDTO>> findAll() {
        return ResponseEntity.ok(guestService.findAllGuests());
    }

    @PostMapping
    @ApiOperation(value = "Create guest.")
    public ResponseEntity<Void> createGuest(@RequestBody final GuestDTO guestDTO) {
        return ResponseEntity.created(locationByEntity(guestService.createGuest(guestDTO).getId())).build();
    }

    @PutMapping
    @ApiOperation(value = "Update guest by id.")
    public ResponseEntity<GuestDTO> updateGuest(@RequestBody final GuestDTO guestDTO) {
        return ResponseEntity.ok(guestService.updateGuest(guestDTO));
    }

    @DeleteMapping(path = "/{id}")
    @ApiOperation(value = "Delete guest by id.")
    public ResponseEntity<Void> deleteGuest(@PathVariable final Long id) {
        guestService.deleteGuest(id);
        return ResponseEntity.noContent().build();
    }
}
