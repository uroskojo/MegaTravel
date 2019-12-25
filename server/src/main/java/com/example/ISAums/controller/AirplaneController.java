package com.example.ISAums.controller;

import com.example.ISAums.dto.request.UpdateAirplaneRequest;
import com.example.ISAums.dto.response.GetAirplaneResponse;
import com.example.ISAums.model.Airplane;
import com.example.ISAums.service.AirplaneService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
import static com.example.ISAums.converter.AirplaneConverter.toGetAirplaneReponseFromAirplane;
import static com.example.ISAums.converter.AirplaneConverter.toGetAirplaneResponseFromAirplanes;

@RestController
@RequestMapping("/airplanes")
public class AirplaneController {

    private final AirplaneService airplaneService;

    public AirplaneController(AirplaneService airplaneService){
        this.airplaneService = airplaneService;
    }

    @PutMapping
    @PreAuthorize("hasAnyAuthority('AIRLINE_ADMIN')")
    public ResponseEntity<GetAirplaneResponse> update(@RequestBody UpdateAirplaneRequest request){

        Airplane airplane = airplaneService.update(request);
        return ResponseEntity.ok(toGetAirplaneReponseFromAirplane(airplane));
    }

    @GetMapping(value = "/{airlineId}")
    public ResponseEntity<List<GetAirplaneResponse>> getAirplanesByAirline(@PathVariable(name = "airlineId") UUID airlineId){
        List<Airplane> airplanes = airplaneService.getAirplanesByAirline(airlineId);
        return ResponseEntity.ok(toGetAirplaneResponseFromAirplanes(airplanes));
    }
}
