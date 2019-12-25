package com.example.ISAums.dto.request;

import com.example.ISAums.model.Airline;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateAirplaneRequest {

    private UUID id;

    private String mark;

    private Integer numberOfRows;

    private Integer numberOfColumnsPerSegment;

    private Integer numberOfSegments;

    private Airline airline;
}
