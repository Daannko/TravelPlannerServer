package com.example.travelplannerserver.plan;

import com.example.travelplannerserver.GoogleService;
import com.example.travelplannerserver.dto.PlacesRequest;
import com.example.travelplannerserver.place.Place;
import com.google.gson.Gson;
import okhttp3.ResponseBody;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PlanService {

    public List<Place> getPlanFromGoogleApi(PlacesRequest placesRequest) {
        Map<String,Place> placesList = new TreeMap<>();
        for(String type : placesRequest.getTypes()){
            placesList.putAll(GoogleService.getPlaces(placesRequest.getLat(), placesRequest.getLng(), type,placesRequest.getPlaceName()));
        }

        List<Place> sortedList = new ArrayList<>( placesList.values().stream().toList());
        Collections.sort(sortedList);
        return sortedList;
    }

}
