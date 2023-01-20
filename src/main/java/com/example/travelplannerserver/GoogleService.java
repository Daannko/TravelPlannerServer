package com.example.travelplannerserver;

import com.example.travelplannerserver.dto.PlacesRequest;
import com.example.travelplannerserver.place.Place;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.stream.Collectors;

@Service



public class GoogleService {

   private static final String apiKey = "AIzaSyDl8zk1Jv2eD3-u1RVdutitWTB2JlzrHwU";
   private  static final int ratingTreshold = 20;

   public static Map<String, Place> getPlaces(String type, PlacesRequest placesRequest){

      String category = Arrays.stream(type.split("_"))
             .map(e -> e.substring(0,1)
             .toUpperCase() + e.substring(1))
             .collect(Collectors.joining(" "));
      Gson gson = new Gson();
      Map<String,Place> places = new HashMap<>();
      OkHttpClient client = new OkHttpClient().newBuilder()
              .build();

      String u2 = " https://maps.googleapis.com/maps/api/place/textsearch/json?query="
              + type.replaceAll("_","%20") + "%20"
              + placesRequest.getPlaceLongName().replaceAll(",","").replaceAll(" ", "%20")
              + "&language=en"
              + "&key="
              + apiKey;

      String u = " https://maps.googleapis.com/maps/api/place/textsearch/json?query="
              + type.replaceAll("_","%20")
              + "%20near%20"
              + placesRequest.getPlaceLongName().replaceAll(",","").replaceAll(" ", "%20")
              + placesRequest.getPlaceName()
              + "&location=" + placesRequest.getLat() + "%2C" + placesRequest.getLng()
              + "&language=en"
              + "&key="
              + apiKey;

      ArrayList<String> urls = new ArrayList<>(List.of(u2,u));
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

               // uniemożliwa dodawanie duplikatów
               if(places.containsKey(place.getAsJsonObject().get("place_id").getAsString())) continue;
               // pomiń miejsce jeżeli nie spełnia wymagania ilści opini
               if(place.getAsJsonObject().get("user_ratings_total").getAsInt() < ratingTreshold) continue;

               Place toFilter = Place.parsePlace(place.getAsJsonObject(),category);
               places.put(toFilter.getId(),toFilter);
            }
         }
      } catch (IOException e) {
         e.printStackTrace();
      }
      return places;
   }

   public static List<Place> getPlacesByIds(List<String> placesIds){
      List<Place> places = new ArrayList<>();
      for(String placeId : placesIds){
         places.add(Place.getPlaceFromId(placeId));
      }
      return places;
   }

   public static String getPhotoUrlAfterRedirect(String imageReference){
      String url = "https://maps.googleapis.com/maps/api/place/photo"
              + "?maxheight=800"
              + "&maxwidth=800"
              + "&photo_reference="
              +  imageReference
              + "&key="
              + "AIzaSyDl8zk1Jv2eD3-u1RVdutitWTB2JlzrHwU";

      URLConnection con;
      String newurl;
      try {

         con = new URL( url ).openConnection();
         con.connect();
         InputStream is = con.getInputStream();
         newurl = con.getURL().toString();
         is.close();

      } catch (IOException e) {
         throw new RuntimeException(e);
      }
      return newurl;
   }
}
