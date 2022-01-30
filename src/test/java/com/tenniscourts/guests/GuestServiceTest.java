package com.tenniscourts.guests;

import com.tenniscourts.exceptions.EntityNotFoundException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ContextConfiguration;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(classes = GuestService.class)
public class GuestServiceTest {

    @InjectMocks
    private GuestService guestService;
    @Mock
    private GuestRepository guestRepository;
    @Mock
    private GuestMapper guestMapper;

    @Test
    public void testFindGuestById() {
        Optional<Guest> optionalGuest = Optional.of(new Guest());
        GuestDTO guestDTO = new GuestDTO(1L, "test");

        Mockito.when(guestRepository.findById(Mockito.anyLong())).thenReturn(optionalGuest);
        Mockito.when(guestMapper.map(optionalGuest.get())).thenReturn(guestDTO);

        GuestDTO result = guestService.findGuestById(Mockito.anyLong());

        Mockito.verify(guestRepository, Mockito.times(1)).findById(Mockito.anyLong());
        Mockito.verify(guestMapper, Mockito.times(1)).map(optionalGuest.get());
        Assert.assertEquals(guestDTO.getId(), result.getId());
        Assert.assertEquals(guestDTO.getName(), result.getName());
    }

    @Test(expected = EntityNotFoundException.class)
    public void testFindGuestByIdThrowException() {
        Mockito.when(guestRepository.findById(Mockito.anyLong())).thenThrow(EntityNotFoundException.class);
        guestService.findGuestById(Mockito.anyLong());
    }

    @Test
    public void testFindGuestByName() {
        final Optional<Guest> optionalGuest = Optional.of(new Guest());
        final GuestDTO guestDTO = new GuestDTO(2L, "test2");

        Mockito.when(guestRepository.findGuestByName(Mockito.anyString())).thenReturn(optionalGuest);
        Mockito.when(guestMapper.map(optionalGuest.get())).thenReturn(guestDTO);

        final GuestDTO result = guestService.findGuestByName(Mockito.anyString());

        Assert.assertEquals(guestDTO.getId(), result.getId());
        Assert.assertEquals(guestDTO.getName(), result.getName());
        Mockito.verify(guestRepository, Mockito.times(1)).findGuestByName(Mockito.anyString());
        Mockito.verify(guestMapper, Mockito.times(1)).map(optionalGuest.get());
    }

    @Test(expected = EntityNotFoundException.class)
    public void testFindGuestByNameThrowException() {
        Mockito.when(guestRepository.findGuestByName(Mockito.anyString())).thenThrow(EntityNotFoundException.class);
        guestService.findGuestByName(Mockito.anyString());
    }

    @Test
    public void testFindAllGuests() {
        final GuestDTO guestDTO1 = new GuestDTO(6L, "test6");
        final GuestDTO guestDTO2 = new GuestDTO(7L, "test7");
        final List<Guest> guests = Arrays.asList(new Guest(), new Guest());
        final List<GuestDTO> guestDTOS = Arrays.asList(guestDTO1, guestDTO2);

        Mockito.when(guestRepository.findAll()).thenReturn(guests);
        Mockito.when(guestMapper.map(guests)).thenReturn(guestDTOS);

        final List<GuestDTO> result = guestService.findAllGuests();

        Assert.assertEquals(guestDTOS.size(), result.size());
        Mockito.verify(guestRepository, Mockito.times(1)).findAll();
        Mockito.verify(guestMapper, Mockito.times(1)).map(guests);
    }

    @Test
    public void testCreateGuest() {
        final Optional<Guest> optionalGuest = Optional.of(new Guest());
        final GuestDTO guestDTO = new GuestDTO(3L, "test3");

        Mockito.when(guestRepository.save(Mockito.any())).thenReturn(optionalGuest.get());
        Mockito.when(guestMapper.map(optionalGuest.get())).thenReturn(guestDTO);

        final GuestDTO result = guestService.createGuest(guestDTO);

        Assert.assertEquals(guestDTO.getId(), result.getId());
        Assert.assertEquals(guestDTO.getName(), result.getName());
        Mockito.verify(guestRepository, Mockito.times(1)).save(Mockito.any());
        Mockito.verify(guestMapper, Mockito.times(1)).map(optionalGuest.get());
    }

    @Test
    public void testUpdateGuest() {
        final Optional<Guest> optionalGuest = Optional.of(new Guest());
        final GuestDTO guestDTO = new GuestDTO(4L, "test4");

        Mockito.when(guestRepository.findById(Mockito.anyLong())).thenReturn(optionalGuest);
        Mockito.doNothing().when(guestMapper).updateGuestFromDTO(Mockito.any(), Mockito.any());
        Mockito.when(guestRepository.save(Mockito.any())).thenReturn(optionalGuest.get());
        Mockito.when(guestMapper.map(optionalGuest.get())).thenReturn(guestDTO);

        final GuestDTO result = guestService.updateGuest(guestDTO);

        Assert.assertEquals(guestDTO.getId(), result.getId());
        Assert.assertEquals(guestDTO.getName(), result.getName());
        Mockito.verify(guestRepository, Mockito.times(1)).save(Mockito.any());
        Mockito.verify(guestMapper, Mockito.times(1)).map(optionalGuest.get());
    }

    @Test(expected = EntityNotFoundException.class)
    public void testUpdateGuestThrowException() {
        final Optional<Guest> optionalGuest = Optional.of(new Guest());
        final GuestDTO guestDTO = new GuestDTO(4L, "test4");

        Mockito.when(guestRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        guestService.updateGuest(guestDTO);
    }

    @Test
    public void testDeleteGuest() {
        Mockito.doNothing().when(guestRepository).deleteById(Mockito.anyLong());
        guestService.deleteGuest(1L);
        Mockito.verify(guestRepository, Mockito.times(1)).deleteById(Mockito.anyLong());
    }

    @Test(expected = EntityNotFoundException.class)
    public void testDeleteGuestThrowException() {
        Mockito.doThrow(EmptyResultDataAccessException.class).when(guestRepository).deleteById(Mockito.anyLong());
        guestService.deleteGuest(1L);
    }
}
