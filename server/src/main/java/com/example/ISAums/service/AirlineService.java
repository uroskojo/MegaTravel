package com.example.ISAums.service;

import com.example.ISAums.dto.request.CreateRatingRequest;
import com.example.ISAums.dto.request.UpdateAirlineRequest;
import com.example.ISAums.dto.request.UpdateSeatConfigurationRequest;
import com.example.ISAums.exception.CustomException;
import com.example.ISAums.exception.EntityAlreadyExistsException;
import com.example.ISAums.exception.EntityWithIdDoesNotExist;
import com.example.ISAums.model.*;
import com.example.ISAums.model.enumeration.RatingType;
import com.example.ISAums.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.example.ISAums.converter.RatingConverter.toRatingFromCreateRequest;
import static com.example.ISAums.util.UtilService.copyNonNullProperties;
import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;

@Service
@RequiredArgsConstructor
public class AirlineService {

    private final RatingRepository ratingRepository;
    private final AirlineRepository airlineRepository;
    private final AddressRepository addressRepository;
    private final AirplaneRepository airplaneRepository;
    private final AirplaneTicketRepository airplaneTicketRepository;
    private final AirlineAdminRepository airlineAdminRepository;

    public Double getAverageRating(UUID airlineId) {

        double sum = 0;
        List<Integer> marks = ratingRepository.getMarksByEntityId(String.valueOf(airlineId) , RatingType.AIRLINE.name());

        for(int i : marks)
            sum += i;

        return sum/marks.size();
    }

    @Transactional(rollbackFor = Exception.class)
    public Airline update(UpdateAirlineRequest request){

        Airline tmpAirline = airlineRepository.getAnotherWithThisName(request.getName(), String.valueOf(request.getId()));

        if(tmpAirline != null){
            throw new EntityAlreadyExistsException(request.getName());
        }

        Optional<Address> address = addressRepository.findById(request.getAddress().getId());

        if (address.get() == null) {
            throw new EntityWithIdDoesNotExist("Address", request.getAddress().getId());
        }

        copyNonNullProperties(request.getAddress(), address.get());
        addressRepository.save(address.get());

        Optional<Airline> airline = airlineRepository.findById(request.getId());
        airline.get().setCheckingInSuitcasePrice(request.getCheckingInSuitcasePrice());
        airline.get().setHandLuggagePrice(request.getHandLuggagePrice());
        copyNonNullProperties(request, airline.get(), "address");

        return airlineRepository.save(airline.get());
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateSeatConfiguration(UpdateSeatConfigurationRequest request, UUID airlineId){

        List<Airplane> airplane = airplaneRepository.findAllByAirlineId(airlineId);

        for(int i = 0; i < airplane.size(); i++){
            airplane.get(i).setNumberOfColumnsPerSegment(request.getNumberOfColumnsPerSegment());
            airplane.get(i).setNumberOfRows(request.getNumberOfRows());
            airplane.get(i).setNumberOfSegments(request.getNumberOfSegments());
            airplaneRepository.save(airplane.get(i));
        }
    }
    @Transactional(readOnly = true, isolation = READ_COMMITTED)
    public Airline getAirline(String airlineId) {
        return airlineRepository.findById(UUID.fromString(airlineId)).get();
    }

    @Transactional(rollbackFor = Exception.class)
    public void rate(CreateRatingRequest request) {
        AirplaneTicket airplaneTicket = airplaneTicketRepository.getOne(request.getReservationId());

        if (airplaneTicket == null)
            throw new EntityWithIdDoesNotExist("airplane ticket",request.getReservationId());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Airline airline = airplaneTicket.getFlight().getAirplane().getAirline();
        if (ratingRepository.checkIfUserAlreadyRateEntity(authentication.getName(), airline.getId().toString(), RatingType.AIRLINE.name()) != null)
            throw new CustomException("You already rate this airline!");

        Rating rating = toRatingFromCreateRequest(airline.getId(), request, RatingType.AIRLINE);
        rating.setUserID(UUID.fromString(authentication.getName()));
        ratingRepository.save(rating);

        airline.setRating(ratingRepository.getAverageMarkForEntity(airline.getId().toString(), RatingType.AIRLINE.name()));
        airlineRepository.save(airline);
    }

    @Transactional(readOnly = true)
    public List<Airline> sort(String by) {
        if (by.equals("name"))
            return airlineRepository.sortByName();
        else if (by.equals("handLuggage"))
            return airlineRepository.sortByHandLuggagePrice();
        else if (by.equals("suitcasePrice"))
            return airlineRepository.sortBySuitcasePrice();
        else if (by.equals("rating"))
            return airlineRepository.sortByRating();
        else if (by.equals("address"))
            return airlineRepository.sortByAddress();
        else
            throw new CustomException("Unknown attribute!");

    }
    @Transactional(readOnly = true)
    public List<Airline> getAll() {
        return airlineRepository.findAll();
    }

    @Transactional(readOnly = true)
    public AirlineAdmin getAirlineAdmin(UUID id) {
        return airlineAdminRepository.findByUser_Id(id);
    }
}
