package com.example.ISAums.controller;

import com.example.ISAums.converter.VehicleConverter;
import com.example.ISAums.dto.request.CreateRatingRequest;
import com.example.ISAums.dto.request.CreateRentACarVehicleRequest;
import com.example.ISAums.dto.request.CreateVehicleRequest;
import com.example.ISAums.dto.response.*;
import com.example.ISAums.dto.request.UpdateVehicleRequest;
import com.example.ISAums.model.Vehicle;
import com.example.ISAums.service.VehicleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.example.ISAums.converter.VehicleConverter.*;

@RestController
@RequestMapping("/vehicles")
public class VehicleController {
    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping
    public ResponseEntity<List<GetVehicleResponse>> get() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(toGetVehicleResponseFromVehicles(vehicleService.findAll()));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('RENT_A_CAR_ADMIN')")
    public ResponseEntity<List<GetVehicleResponse>> create(@RequestBody CreateRentACarVehicleRequest request) {
        return ResponseEntity.ok(toGetVehicleResponseFromVehicles(vehicleService.create(request)));
    }

    @PutMapping
    @PreAuthorize("hasAnyAuthority('RENT_A_CAR_ADMIN')")
    public ResponseEntity<UpdateVehicleResponse> update(@RequestBody UpdateVehicleRequest request) {
        return ResponseEntity.ok(toUpdateVehicleResponseFromVehicle(vehicleService.update(request)));
    }

    @DeleteMapping
    @RequestMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('RENT_A_CAR_ADMIN')")
    public ResponseEntity<List<GetVehicleResponse>> delete(@PathVariable(name = "id") UUID vehicleId) {
         return ResponseEntity.ok(toGetVehicleResponseFromVehicles(vehicleService.delete(vehicleId)));
    }

    @PostMapping
    @RequestMapping("/rating")
    public ResponseEntity<List<GetVehicleResponse>> rating(@RequestBody CreateRatingRequest request) {
        return ResponseEntity.ok(VehicleConverter.toGetVehicleResponseFromVehicles(vehicleService.rate(request)));
    }

    @GetMapping("/search")
    public ResponseEntity<List<SearchVehicleResponse>> search(
            @RequestParam(name = "pickUpDate", required = true) String pickUpDate,
            @RequestParam(name = "dropOffDate", required = true) String dropOffDate,
            @RequestParam(name = "pickUpLocation", required = true) String pickUpLocation,
            @RequestParam(name = "dropOffLocation", required = true) String dropOffLocation,
            @RequestParam(name = "type", required = true) String type,
            @RequestParam(name = "seats"    , required = true) int seats,
            @RequestParam(name = "startRange", required = false) double startRange,
            @RequestParam(name = "endRange", required = false) double endRange,
            @RequestParam(name = "rentACarId", required = false) String rentACarId
    ) throws ParseException {
        List<Vehicle> searchResult = vehicleService.search(pickUpDate, dropOffDate, pickUpLocation, dropOffLocation, type, seats, startRange, endRange, rentACarId);
        return ResponseEntity.ok(toSearchVehicleResponseFromVehicles(searchResult, pickUpDate, dropOffDate));
    }

    @GetMapping("/sort")
    public ResponseEntity<List<GetVehicleResponse>> sort(@RequestParam(name = "by", required = true) String by,  @RequestParam(name = "rentACarId", required = true) String rentACarId) {
        return ResponseEntity.ok(toGetVehicleResponseFromVehicles(vehicleService.sort(by, rentACarId)));
    }

}
