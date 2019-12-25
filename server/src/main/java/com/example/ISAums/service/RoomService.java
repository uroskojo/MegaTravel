package com.example.ISAums.service;

import com.example.ISAums.dto.request.CreateRatingRequest;
import com.example.ISAums.dto.request.CreateRoomRequest;
import com.example.ISAums.dto.request.UpdateRoomRequest;
import com.example.ISAums.exception.CustomException;
import com.example.ISAums.exception.EntityWithIdDoesNotExist;
import com.example.ISAums.model.*;
import com.example.ISAums.model.enumeration.RatingType;
import com.example.ISAums.repository.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.example.ISAums.converter.RatingConverter.toRatingFromCreateRequest;
import static com.example.ISAums.converter.RoomConverter.toRoomFromRequest;
import static com.example.ISAums.util.UtilService.copyNonNullProperties;

@Service
public class RoomService {
    private final RoomRepository roomRepository;
    private final HotelReservationRepository hotelReservationRepository;
    private final HotelRepository hotelRepository;
    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;

    public RoomService(RoomRepository roomRepository, HotelReservationRepository hotelReservationRepository, HotelRepository hotelRepository, UserRepository userRepository, RatingRepository ratingRepository, UserRepository userRepository1) {
        this.roomRepository = roomRepository;
        this.hotelReservationRepository = hotelReservationRepository;
        this.hotelRepository = hotelRepository;
        this.ratingRepository = ratingRepository;
        this.userRepository = userRepository1;
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public List<Room> get(UUID hotelId, LocalDate startDate, Integer nights, Integer people, Double fromPrice, Double toPrice) {
        LocalDate endDate = startDate != null ? startDate.plusDays(nights) : null;
        return this.roomRepository.getRooms(hotelId, startDate, endDate, people, fromPrice, toPrice);
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public List<Room> getRooms() {
        return roomRepository.findAll();
    }

    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
    public Room createRoom(CreateRoomRequest request, UUID userId) {
        if (roomRepository.existsByFloorAndNumberAndHotelId(request.getFloor(), request.getNumber(), request.getHotelId())) {
            throw new CustomException("Room at this floor " + request.getFloor() + " and with this room number " + request.getNumber() + " already exist!");
        }

        Optional<Hotel> optionalHotel = this.hotelRepository.findById(request.getHotelId());

        if (!optionalHotel.isPresent()) {
            throw  new EntityWithIdDoesNotExist("Hotel", request.getHotelId());
        }

        Optional<User> user = this.userRepository.findById(userId);

        if (user.get().getHotelAdmin() == null || !optionalHotel.get().getId().equals(user.get().getHotelAdmin().getHotel().getId()) ) {
            throw new CustomException("Not Hotel of user with id " + userId);
        }

        Room room = toRoomFromRequest(request, optionalHotel.get());

        roomRepository.save(room);

        return room;
    }

    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
    public Room updateRoom(@Valid UUID roomId, UpdateRoomRequest request) {
        Optional<Room> optionalRoom = roomRepository.findById(roomId);

        if (!optionalRoom.isPresent()) {
            throw new EntityWithIdDoesNotExist("Room", roomId);
        }

        Room room = optionalRoom.get();

        // TODO: Check this, may be problem because you have two different classes
        copyNonNullProperties(request, room, "hotel");
        roomRepository.save(room);

        return room;
    }

    @Transactional(rollbackFor = Exception.class, isolation = Isolation.SERIALIZABLE)
    public Boolean deleteRoom(UUID roomId) {
        Optional<Room> room = roomRepository.findById(roomId);
        if (!room.isPresent()) {
            throw new EntityWithIdDoesNotExist("Room", roomId);
        }
//        Room roomToDelete = hotelReservationRepository.existsByRoomWhereEndDateIsAfterToday(roomId.toString());

        roomRepository.delete(room.get());

        return true;
    }

    @Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_UNCOMMITTED)
    public void rate(CreateRatingRequest request) {
        HotelReservation hotelReservation = hotelReservationRepository.findById(request.getReservationId()).orElse(null);

        if (hotelReservation == null)
            throw new EntityWithIdDoesNotExist("hotel reservation",request.getReservationId());

        if (hotelReservation.getEndDate().compareTo(LocalDate.now()) >= 0)
            throw new CustomException("You did not left hotel yet!");

        Room room = hotelReservation.getRoom();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (ratingRepository.checkIfUserAlreadyRateEntity(authentication.getName(), room.getId().toString(), RatingType.ROOM.name()) != null)
            throw new CustomException("You already rate this room!");

        Rating rating = toRatingFromCreateRequest(room.getId(), request, RatingType.ROOM);
        ratingRepository.save(rating);

        room.setRating(ratingRepository.getAverageMarkForEntity(room.getId().toString(), RatingType.ROOM.name()));
        roomRepository.save(room);


    }
}
