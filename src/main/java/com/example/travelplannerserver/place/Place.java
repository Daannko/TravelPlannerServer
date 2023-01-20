package com.example.travelplannerserver.place;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
@NoArgsConstructor
@Setter
public class Place implements Comparable<Place>{

    String id;
    String name;
    String category;
    String types;
    String description;
    double lat, lng;
    int pricing;
    float rating;
    int totalRating;
    List<String> imageReference;
    List<String> open;



    public static Place parsePlace(JsonObject place,String category){

        StringBuilder desc = new StringBuilder();
        for(JsonElement t : place.getAsJsonObject().getAsJsonArray("types")) {
            desc.append(t.getAsString()).append(" ");
        }

        Place newPlace = new Place();
        newPlace.setOpen(new ArrayList<>());
        newPlace.setImageReference(new ArrayList<>());

        newPlace.setId(place.getAsJsonObject().get("place_id").getAsString());
        newPlace.setName(place.getAsJsonObject().get("name").getAsString());
        newPlace.setCategory(category);
        newPlace.setLat(place.getAsJsonObject("geometry").getAsJsonObject("location").get("lat").getAsFloat());
        newPlace.setLng(place.getAsJsonObject("geometry").getAsJsonObject("location").get("lng").getAsFloat());
        newPlace.setPricing(place.get("price_level") == null ? -1 : place.get("price_level").getAsInt());
        newPlace.setRating(place.get("rating") == null ? -1 : place.get("rating").getAsFloat());
        newPlace.setTotalRating(place.get("user_ratings_total") == null ? -1 : place.get("user_ratings_total").getAsInt());
        newPlace.setTypes(desc.toString());

        String placeDetails = String.format("https://maps.googleapis.com/maps/api/place/details/json?place_id=%s&fields=photo,opening_hours,editorial_summary&key=AIzaSyDl8zk1Jv2eD3-u1RVdutitWTB2JlzrHwU", newPlace.getId());
        Request request = new Request.Builder()
                .url(placeDetails)
                .method("GET", null)
                .build();

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Response response;
        try {
            response = client.newCall(request).execute();
            if(response.body() == null) throw new IOException();
            Gson gson = new Gson();
            JsonObject results = gson.fromJson( response.body().string(), JsonObject.class).getAsJsonObject("result");
            if(results.getAsJsonArray("photos") != null)
            for(JsonElement reference : results.getAsJsonArray("photos")){
                newPlace.getImageReference().add(reference.getAsJsonObject().getAsJsonPrimitive("photo_reference").getAsString());
            }
            if(results.getAsJsonObject("opening_hours") != null)
                if(results.getAsJsonObject("opening_hours").getAsJsonArray("weekday_text") != null)
                    for(JsonElement reference : results.getAsJsonObject("opening_hours").getAsJsonArray("weekday_text")){
                        newPlace.getOpen().add(reference.getAsString());
                    }
            newPlace.setDescription( results.getAsJsonObject("editorial_summary") == null
                    ? ""
                    :results.getAsJsonObject("editorial_summary").get("overview").getAsString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newPlace;
    }

    public static Place getPlaceFromId(String id){

        Place newPlace = new Place();
        newPlace.setImageReference(new ArrayList<>());
        newPlace.setOpen(new ArrayList<>());
        Response response;

        String placeDetails = String.format("https://maps.googleapis.com/maps/api/place/details/json?place_id=%s&fields=opening_hours,geometry,user_ratings_total,photo,rating,editorial_summary,name,price_level,type&language=en&key=AIzaSyDl8zk1Jv2eD3-u1RVdutitWTB2JlzrHwU", id);
        Request request = new Request.Builder()
                .url(placeDetails)
                .method("GET", null)
                .build();

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();


        try {
            response = client.newCall(request).execute();
            if(response.body() == null) throw new IOException();
            Gson gson = new Gson();
            JsonObject results = gson.fromJson( response.body().string(), JsonObject.class).getAsJsonObject("result");

            StringBuilder types = new StringBuilder();
            for(JsonElement t : results.getAsJsonObject().getAsJsonArray("types")) {
                types.append(Arrays.stream(t.getAsString().replaceAll("_", " ").split(" "))
                        .map(e -> e.substring(0,1)
                        .toUpperCase() + e.substring(1))
                        .collect(Collectors.joining(" ")))
                        .append(" ");
            }

            newPlace.setId(id);
            newPlace.setName(results.getAsJsonObject().get("name").getAsString());
            newPlace.setCategory(Arrays.stream(results.getAsJsonArray("types").get(0).getAsString().replaceAll("_", " ").split(" "))
                    .map(e -> e.substring(0,1)
                            .toUpperCase() + e.substring(1))
                    .collect(Collectors.joining(" ")));
            newPlace.setLat(results.getAsJsonObject().getAsJsonObject("geometry").getAsJsonObject("location").get("lat").getAsFloat());
            newPlace.setLng(results.getAsJsonObject().getAsJsonObject("geometry").getAsJsonObject("location").get("lng").getAsFloat());
            newPlace.setTypes(types.toString());
            newPlace.setPricing(results.getAsJsonObject().get("price_level") == null
                    ? -1
                    : results.getAsJsonObject().get("price_level").getAsInt());
            newPlace.setRating(results.getAsJsonObject().get("rating") == null
                    ? -1
                    : results.getAsJsonObject().get("rating").getAsFloat());
            newPlace.setTotalRating(results.getAsJsonObject().get("user_ratings_total") == null
                    ? -1
                    : results.getAsJsonObject().get("user_ratings_total").getAsInt());
            newPlace.setDescription( results.getAsJsonObject("editorial_summary") == null
                    ? ""
                    :results.getAsJsonObject("editorial_summary").get("overview").getAsString());

            if(results.getAsJsonArray("photos") != null){
                for(JsonElement reference : results.getAsJsonArray("photos")){
                    newPlace.getImageReference().add(reference.getAsJsonObject().getAsJsonPrimitive("photo_reference").getAsString());
                }
            }

            if(results.getAsJsonObject("opening_hours") != null){
                if(results.getAsJsonObject("opening_hours").getAsJsonArray("weekday_text") != null){
                    for(JsonElement reference : results.getAsJsonObject("opening_hours").getAsJsonArray("weekday_text")){
                        newPlace.getOpen().add(reference.getAsString());
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return newPlace;
    }

    @Override
    public int compareTo(Place o) {
        int categoryCompare = this.getCategory().compareTo(o.getCategory());
        return categoryCompare != 0 ? categoryCompare : o.getTotalRating() - this.getTotalRating();
    }
}
