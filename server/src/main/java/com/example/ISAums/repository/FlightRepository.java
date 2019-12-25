package com.example.ISAums.repository;
import com.example.ISAums.model.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FlightRepository extends JpaRepository<Flight, UUID> {

    @Query(value = "select * from flight where flight.airline_destination_id = ?1", nativeQuery = true)
    List<Flight> getFlightsForDestination(String airlineDestinationId);

    @Query(value = "select f.id from isa_database.flight f left join isa_database.airplane airp on f.airplane_id = airp.id " +
            "join isa_database.airline airl on airp.airline_id = airl.id join isa_database.address a on airl.address_id = a.id " +
            "join isa_database.airline_destination ad on f.airline_destination_id = ad.id " +
            "join isa_database.destination d on ad.destination_id = d.id where a.city=?1 and d.city=?2", nativeQuery = true)
    List<UUID> search(String fromDestinationCity, String toDestinationCity);

    @Query(value = "select f.id from isa_database.flight f left join isa_database.airplane a on f.airplane_id = a.id where a.airline_id = ?1", nativeQuery = true)
    List<UUID> getFlightsByAirlineId(String airlineId);

    @Query(value = "select f.airline_destination_id from isa_database.flight f join isa_database.airplane a on f.airplane_id = a.id where a.airline_id = ?1", nativeQuery = true)
    List<UUID> getAirlineDestinations(String airlineId);

    Optional<Flight> findById(UUID id);
}
