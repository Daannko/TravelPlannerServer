package com.example.travelplannerserver;

import com.example.travelplannerserver.place.Place;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service



public class GoogleService {

   /*"https://maps.googleapis.com/maps/api/place/nearbysearch/output?parameters"*/
   private static final String api = "AIzaSyDl8zk1Jv2eD3-u1RVdutitWTB2JlzrHwU";
   private final String  baseUrl = "https://maps.googleapis.com/maps/";
   private  static final int ratingTreshold = 500;

   public static Map<String, Place> getPlaces(double lat, double lng, String type, String placeName){

      String category = Arrays.stream(type.split("_")).map(e -> e.substring(0,1).toUpperCase() + e.substring(1)).collect(Collectors.joining(" "));

      Gson gson = new Gson();
      //Using map to prevent duplicates of places
      Map<String,Place> places = new HashMap<>();

      OkHttpClient client = new OkHttpClient().newBuilder()
              .build();



      /*   https://maps.googleapis.com/maps/api/place/textsearch/json?query=tourist%20atraction%20in%20Paris&key=AIzaSyDl8zk1Jv2eD3-u1RVdutitWTB2JlzrHwU*/

      String u2 = " https://maps.googleapis.com/maps/api/place/textsearch/json?query="
              + type.replaceAll("_","%20")
              + "%20in%20"
              + placeName
              + "&language=en"
              + "&key="
              + api;

      String u = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
              + lat +"%2C"
              + lng
              + "&language=en43&radius=20000&type="
              + type
              + "&key="
              + api;

      ArrayList<String> urls = new ArrayList<>(List.of(u2));

      try {

         for(String url : urls){
            Request request = new Request.Builder()
                    .url(url)
                    .method("GET", null)
                    .build();

            Response response = client.newCall(request).execute();
            if(response.body() == null) throw new IOException();

            JsonArray results = gson.fromJson( response.body().string(), JsonObject.class).getAsJsonArray("results");

            for(JsonElement place : results) {

               Place toFilter = Place.parsePlace(place,api,category);

               if(toFilter.getTotalRating() > ratingTreshold
                       && (!toFilter.getTypes().contains("lodging") || Objects.equals(toFilter.getCategory(), "Lodging"))
                       && !places.containsKey(toFilter.getId())
                       && (filterTuritstAtraction(toFilter))
               )
                  places.put(toFilter.getId(),toFilter);
            }
         }

      } catch (IOException e) {
         e.printStackTrace();
      }

      return places;
   }

   public static Boolean filterTuritstAtraction(Place place){
      if(place.getCategory().equals("Tourist Attraction")){
         if(place.getTypes().contains("church")) return false;
      }
      return true;
   }

   public static Boolean filterAnimals(Place place){
      if(place.getCategory().equals("Zoo")){
         if(place.getTypes().contains("church")) return false;
      }
      return true;
   }

}
