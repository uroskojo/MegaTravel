package com.example.ISAums.service;

import com.example.ISAums.dto.request.CreateFlightRequest;
import com.example.ISAums.dto.request.CreateRatingRequest;
import com.example.ISAums.dto.response.GetFlightAverageRatingResponse;
import com.example.ISAums.exception.CustomException;
import com.example.ISAums.exception.EntityWithIdDoesNotExist;
import com.example.ISAums.model.*;
import com.example.ISAums.model.enumeration.RatingType;
import com.example.ISAums.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.example.ISAums.converter.RatingConverter.toRatingFromCreateRequest;

@Service
@RequiredArgsConstructor
public class FlightService {

    private final FlightRepository flightRepository;
    private final AirlineDestinationRepository airlineDestinationRepository;
    private final AirplaneRepository airplaneRepository;
    private final AirplaneTicketRepository airplaneTicketRepository;
    private final RatingRepository ratingRepository;

    @Transactional(rollbackFor = Exception.class)
    public Flight createFlight(CreateFlightRequest request) {

        Optional<AirlineDestination> airlineDestination = airlineDestinationRepository.findById(request.getAirlineDestination().getId());
        Optional<Airplane> airplane = airplaneRepository.findById(request.getAirplane().getId());

     if(airlineDestination.get() == null){
            throw new EntityWithIdDoesNotExist("AirlineDestination", request.getAirlineDestination().getId());
        }

        if(airplane.get() == null){
            throw new EntityWithIdDoesNotExist("Airplane", request.getAirplane().getId());
        }

        Flight flight = Flight.builder()
                .departureTime(request.getDepartureTime())
                .arrivalTime(request.getArrivalTime())
                .duration(request.getDuration())
                .length(request.getLength())
                .price(request.getPrice())
                .airlineDestination(airlineDestination.get())
                .airplane(airplane.get())
                .build();

        flightRepository.save(flight);

        return flight;
    }

    @Transactional(readOnly = true)
    public List<Flight> getFlightsForDestination(UUID destinationId) {

       return flightRepository.getFlightsForDestination(String.valueOf(destinationId));
    }

    @Transactional(readOnly = true)
    public List<Flight> searchFlights(String fromDestinationCity, String toDestinationCity, String departureDate, String arrivalDate) {

        List<UUID> flightIDs = flightRepository.search(fromDestinationCity, toDestinationCity);
        List<Flight> flights = new ArrayList<>(flightIDs.size());

        for(int i = 0; i < flightIDs.size(); i++){
            Optional<Flight> flight = flightRepository.findById(UUID.fromString(String.valueOf(flightIDs.get(i))));
            String depDate = flight.get().getDepartureTime().toLocalDate().toString();
            String arrDate = flight.get().getArrivalTime().toLocalDate().toString();

            if(depDate.equals(departureDate) && arrDate.equals(arrivalDate))
              flights.add(flight.get());
        }

        return flights;
    }

    public Double getAverageRatingOfFlights(UUID flightId) {

        double sum = 0;
        List<Integer> marks = ratingRepository.getMarksByEntityId(String.valueOf(flightId), RatingType.FLIGHT.name());

        for(int i : marks)
            sum += i;

        return sum/marks.size();
    }

    @Transactional(readOnly = true)
    public List<GetFlightAverageRatingResponse> getFlightsWithRatings(List<Flight> flights){

        List<GetFlightAverageRatingResponse> flightWithRatings = new ArrayList<>(flights.size());

        for(int i = 0; i < flights.size(); i++){
            Optional<Flight> flight = flightRepository.findById(flights.get(i).getId());
            double rating = getAverageRatingOfFlights(flights.get(i).getId());

            GetFlightAverageRatingResponse getFlightAverageRatingResponse = GetFlightAverageRatingResponse.builder()
                                                                    .airlineDestination(flight.get().getAirlineDestination())
                                                                    .airplane(flight.get().getAirplane())
                                                                    .arrivalTime(flight.get().getArrivalTime().toLocalDate().toString())
                                                                    .departureTime(flight.get().getDepartureTime().toLocalDate().toString())
                                                                    .duration(flight.get().getDuration())
                                                                    .id(flight.get().getId())
                                                                    .length(flight.get().getLength())
                                                                    .price(flight.get().getPrice())
                                                                    .avgRating(rating)
                                                                    .build();
            flightWithRatings.add(getFlightAverageRatingResponse);
        }

        return flightWithRatings;
    }

    @Transactional(readOnly = true)
    public List<Flight> getQuickBooking(UUID airlineId) {

        List<UUID> flightIDs = flightRepository.getFlightsByAirlineId(String.valueOf(airlineId));
        List<Flight> flights = new ArrayList<>(flightIDs.size());
        double discount = 10;
        double currentPrice;

        for(int i = 0; i < flightIDs.size(); i++){
            Optional<Flight> tmp = flightRepository.findById(UUID.fromString(String.valueOf(flightIDs.get(i))));

            LocalDate departureDate = tmp.get().getDepartureTime().toLocalDate();
            LocalDate threeDaysFromNow = LocalDate.now().plusDays(3);
            if(departureDate.equals(threeDaysFromNow))
                flights.add(tmp.get());
            continue;
        }

        for(int i = 0; i < flights.size(); i++){

            currentPrice = flights.get(i).getPrice();
            flights.get(i).setPrice(currentPrice - currentPrice/100 * discount);
        }

        return  flights;
    }


    public List<Flight> getFlightsOfAirline(UUID airlineId) {

        List<UUID> flightIDs = flightRepository.getFlightsByAirlineId(String.valueOf(airlineId));
        List<Flight> flights = new ArrayList<>(flightIDs.size());

        for(int i = 0; i < flightIDs.size(); i++){
            Optional<Flight> tmp = flightRepository.findById(UUID.fromString(String.valueOf(flightIDs.get(i))));
            flights.add(tmp.get());
        }

        return flights;
    }

    @Transactional(readOnly = true)
    public List<AirlineDestination> getDestinations(UUID airlineId) {

        List<UUID> airlineDestinationIDs = flightRepository.getAirlineDestinations(String.valueOf(airlineId));
        List<AirlineDestination> airlineDestinations = new ArrayList<>(airlineDestinationIDs.size());

        for(int i = 0; i < airlineDestinationIDs.size(); i++){
            Optional<AirlineDestination> tmp = airlineDestinationRepository.findById(UUID.fromString(String.valueOf(airlineDestinationIDs.get(i))));
            if(!airlineDestinations.contains(tmp.get()))
                airlineDestinations.add(tmp.get());
        }

        return airlineDestinations;
    }

    @Transactional(rollbackFor = Exception.class)
    public void rate(CreateRatingRequest request) {
        AirplaneTicket airplaneTicket = airplaneTicketRepository.getOne(request.getReservationId());

        if (airplaneTicket == null)
            throw new EntityWithIdDoesNotExist("airplane ticket",request.getReservationId());

        Flight flight = airplaneTicket.getFlight();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();


        if (ratingRepository.checkIfUserAlreadyRateEntity(authentication.getName(), request.getReservationId().toString(), RatingType.FLIGHT.name()) != null)
            throw new CustomException("You already rate this flight!");

        Rating rating = toRatingFromCreateRequest(flight.getId(), request, RatingType.FLIGHT);
        rating.setUserID(UUID.fromString(authentication.getName()));
        ratingRepository.save(rating);

        flight.setRating(ratingRepository.getAverageMarkForEntity(flight.getId().toString(), RatingType.FLIGHT.name()));
        flightRepository.save(flight);
    }

    @Transactional(readOnly = true)
    public Flight findFlightById(UUID id) {
        return flightRepository.findById(id).get();
    }
}
