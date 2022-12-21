package com.example.travelplannerserver.plan;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Plan {

    Long id;
    String placeName;
    String placeDescription;
}
