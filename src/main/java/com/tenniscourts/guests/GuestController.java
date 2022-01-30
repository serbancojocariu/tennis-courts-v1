package com.tenniscourts.guests;

import com.tenniscourts.config.BaseRestController;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/guests")
@RequiredArgsConstructor
public class GuestController extends BaseRestController {

    private final GuestService guestService;

    @GetMapping(path = "{id}")
    public GuestDTO findById(@PathVariable final Long id) {
        return guestService.findGuestById(id);
    }

    @GetMapping(params = "name")
    public GuestDTO findByName(@RequestParam(name = "name") final String name) {
        return guestService.findGuestByName(name);
    }

    @GetMapping
    public List<GuestDTO> findAll() {
        return guestService.findAllGuests();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GuestDTO createGuest(@RequestBody final GuestDTO guestDTO) {
        return guestService.createGuest(guestDTO);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public GuestDTO updateGuest(@RequestBody final GuestDTO guestDTO) {
        return guestService.updateGuest(guestDTO);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGuest(@PathVariable final Long id) {
        guestService.deleteGuest(id);
    }
}
