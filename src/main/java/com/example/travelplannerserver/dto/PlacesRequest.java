package com.example.travelplannerserver.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PlacesRequest {
    double lat,lng;
    String placeName;
    String placeLongName;
    List<String> types;
}
