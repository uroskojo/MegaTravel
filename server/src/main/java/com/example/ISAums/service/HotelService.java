package com.example.ISAums.service;

import com.example.ISAums.dto.request.CreateHotelRequest;
import com.example.ISAums.dto.request.CreateRatingRequest;
import com.example.ISAums.dto.request.UpdateHotelRequest;
import com.example.ISAums.exception.CustomException;
import com.example.ISAums.exception.EntityAlreadyExistsException;
import com.example.ISAums.exception.EntityWithIdDoesNotExist;
import com.example.ISAums.model.*;
import com.example.ISAums.model.enumeration.RatingType;
import com.example.ISAums.repository.AddressRepository;
import com.example.ISAums.repository.HotelRepository;
import com.example.ISAums.repository.HotelReservationRepository;
import com.example.ISAums.repository.RatingRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.example.ISAums.converter.HotelConverter.toAddressFromRequest;
import static com.example.ISAums.converter.RatingConverter.toRatingFromCreateRequest;
import static com.example.ISAums.util.UtilService.copyNonNullProperties;

@Service
public class HotelService {
    private final HotelRepository hotelRepository;
    private final AddressRepository addressRepository;
    private final HotelReservationRepository hotelReservationRepository;
    private final RatingRepository ratingRepository;

    public HotelService(HotelRepository hotelRepository, AddressRepository addressRepository, HotelReservationRepository hotelReservationRepository, RatingRepository ratingRepository) {
        this.hotelRepository = hotelRepository;
        this.addressRepository = addressRepository;
        this.hotelReservationRepository = hotelReservationRepository;
        this.ratingRepository = ratingRepository;
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public List<Hotel> get(LocalDate startDate, LocalDate endDate, String name, String city, String state) {
        return hotelRepository.findAllByFilters(startDate, endDate, name, city, state);
    }

    @Transactional(rollbackFor = Exception.class , isolation = Isolation.REPEATABLE_READ)
    public Hotel createHotel(CreateHotelRequest request) {
        if (hotelRepository.existsByName(request.getName())) {
            throw new EntityAlreadyExistsException(request.getName());
        }

        Address address = toAddressFromRequest(request);
        addressRepository.save(address);

        Hotel hotel = Hotel.builder()
                .name(request.getName())
                .description(request.getDescription())
                .address(address)
                .build();

        hotelRepository.save(hotel);

        return hotel;
    }


    @Transactional(rollbackFor = Exception.class,  isolation = Isolation.REPEATABLE_READ)
    public Hotel updateHotel(@Valid UUID id, UpdateHotelRequest request) {
        if (!hotelRepository.existsById(id)) {
            throw new EntityWithIdDoesNotExist("Hotel", id);
        }

        Hotel checkHotel = hotelRepository.findByName(request.getName());
        if ((checkHotel != null && !checkHotel.getId().equals(id)) && hotelRepository.existsByName(request.getName())) {
            throw new EntityAlreadyExistsException(request.getName());
        }

        Optional<Address> optionalAddress = addressRepository.findById(request.getAddress().getId());

        if (!optionalAddress.isPresent()) {
            throw new EntityWithIdDoesNotExist("Address", request.getAddress().getId());
        }

        Address address = optionalAddress.get();

        // TODO: Check this, may be problem because you have two different classes
        copyNonNullProperties(request.getAddress(), address);
        addressRepository.save(address);

        Optional<Hotel> optionalHotel = hotelRepository.findById(id);
        Hotel hotel = optionalHotel.get();

        copyNonNullProperties(request, hotel, "address");
        hotelRepository.save(hotel);

        return hotel;
    }

    // TODO: When authentication is finished, use signed-in user to get to his hotel id
    @Transactional(readOnly = true,  isolation = Isolation.REPEATABLE_READ)
    public Double getHotelIncomeForDate(Date startDate, Date endDate) {

        // TODO: Delete this line when u get user from token
        HotelAdmin admin = new HotelAdmin();

        List<HotelReservation> hotelReservation = hotelReservationRepository.findAllWhereDateBetweenStartAndEndDate(startDate, endDate, admin.getHotel().getId().toString());
        return hotelReservation.stream()
                .mapToDouble(currReservations -> {
                    return currReservations.getPrice();
                })
                .reduce(0, (subtotal, price) -> subtotal + price);
    }

    @Transactional(rollbackFor = Exception.class,  isolation = Isolation.SERIALIZABLE)
    public void deleteHotel(UUID hotelId) {
        Optional<Hotel> hotel = hotelRepository.findById(hotelId);

        if (hotel.get() == null) {
            throw new EntityWithIdDoesNotExist("Hotel", hotelId);
        }

        hotelRepository.delete(hotel.get());
    }

    @Transactional(rollbackFor = Exception.class,  isolation = Isolation.SERIALIZABLE)
    public Hotel getHotel(UUID hotelId) {
        Optional<Hotel> hotel = hotelRepository.findById(hotelId);

        if (!hotel.isPresent()) {
            throw new EntityWithIdDoesNotExist("Hotel", hotelId);
        }

        return hotel.get();
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public void rate(CreateRatingRequest request) {
        HotelReservation hotelReservation = hotelReservationRepository.findById(request.getReservationId()).orElse(null);

        if (hotelReservation == null)
            throw new EntityWithIdDoesNotExist("hotel reservation",request.getReservationId());

      if (hotelReservation.getEndDate().compareTo(LocalDate.now()) >= 0)
            throw new CustomException("You did not left hotel yet!");

        Hotel hotel = hotelReservation.getRoom().getHotel();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (ratingRepository.checkIfUserAlreadyRateEntity(authentication.getName(), hotel.getId().toString(), RatingType.HOTEL.name()) != null)
            throw new CustomException("You already rate this hotel!");

        Rating rating = toRatingFromCreateRequest(hotel.getId(), request, RatingType.HOTEL);
        rating.setUserID(UUID.fromString(authentication.getName()));
        ratingRepository.save(rating);

        hotel.setRating(ratingRepository.getAverageMarkForEntity(hotel.getId().toString(), RatingType.HOTEL.name()));
        hotelRepository.save(hotel);

    }

    @Transactional(readOnly = true)
    public List<Hotel> sort(String by) {
        if (by.equals("name"))
            return hotelRepository.sortByName();
        else if(by.equals("rating"))
            return hotelRepository.sortByRating();
        else if(by.equals("address"))
            return hotelRepository.sortByAddress();
        else
            throw  new CustomException("Unknown attribute!");
    }
}
