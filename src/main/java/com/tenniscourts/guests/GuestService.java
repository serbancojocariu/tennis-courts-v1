package com.tenniscourts.guests;

import com.tenniscourts.exceptions.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GuestService {

    private final GuestRepository guestRepository;
    private final GuestMapper guestMapper;

    public GuestDTO findGuestById(final Long id) {
        return guestRepository.findById(id).map(guestMapper::map).orElseThrow(() -> new EntityNotFoundException(String.format("Guest not found for id = %s.", id)));
    }

    public GuestDTO findGuestByName(final String name) {
        return guestRepository.findGuestByName(name).map(guestMapper::map).orElseThrow(() -> new EntityNotFoundException(String.format("Guest not found for name = %s.", name)));
    }

    public List<GuestDTO> findAllGuests() {
        return guestRepository.findAll().stream().map(guestMapper::map).collect(Collectors.toList());
    }

    public GuestDTO createGuest(final GuestDTO guestDTO) {
        return guestMapper.map(guestRepository.save(guestMapper.map(guestDTO)));
    }

    public GuestDTO updateGuest(final GuestDTO guestDTO) {
        final Optional<Guest> optionalGuest = guestRepository.findById(guestDTO.getId());
        return optionalGuest.map(guest -> {
            guestMapper.updateGuestFromDTO(guestDTO, guest);
            return guestMapper.map(guestRepository.save(guest));
        }).orElseThrow(() -> new EntityNotFoundException(String.format("Guest not found for id = %s.", guestDTO.getId())));
    }

    public void deleteGuest(final Long id) {
        try {
            guestRepository.deleteById(id);
        } catch (final EmptyResultDataAccessException ex) {
            throw new EntityNotFoundException(String.format("Guest not found for id = %s.", id));
        }
    }
}
