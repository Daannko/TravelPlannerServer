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
    double lat, lng;
    List<String> imageReference;
    List<String> open;
    int pricing;
    float rating;
    int totalRating;
    String category;
    String types;
    String description;

    public static Place parsePlace(JsonElement place, String api,String category){

        StringBuilder desc = new StringBuilder();
        for(JsonElement t : place.getAsJsonObject().getAsJsonArray("types")) {
            desc.append(t.getAsString()).append(" ");
        }

        Place newPlace = new Place();
        newPlace.setId(place.getAsJsonObject().get("place_id").getAsString());
        newPlace.setName(place.getAsJsonObject().get("name").getAsString());
        newPlace.setCategory(category);
        newPlace.setLat(place.getAsJsonObject().getAsJsonObject("geometry").getAsJsonObject("location").get("lat").getAsFloat());
        newPlace.setLng(place.getAsJsonObject().getAsJsonObject("geometry").getAsJsonObject("location").get("lng").getAsFloat());

        newPlace.setPricing(place.getAsJsonObject().get("price_level") == null ? -1 : place.getAsJsonObject().get("price_level").getAsInt());
        newPlace.setRating(place.getAsJsonObject().get("rating") == null ? -1 : place.getAsJsonObject().get("rating").getAsFloat());
        newPlace.setTotalRating(place.getAsJsonObject().get("user_ratings_total") == null ? -1 : place.getAsJsonObject().get("user_ratings_total").getAsInt());
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

            newPlace.setImageReference(new ArrayList<>());

            if(results.getAsJsonArray("photos") != null)
            for(JsonElement reference : results.getAsJsonArray("photos")){

                newPlace.getImageReference().add(reference.getAsJsonObject().getAsJsonPrimitive("photo_reference").getAsString());

            }

            newPlace.setOpen(new ArrayList<>());
            if(results.getAsJsonObject("opening_hours") != null)
                if(results.getAsJsonObject("opening_hours").getAsJsonArray("weekday_text") != null)
                    for(JsonElement reference : results.getAsJsonObject("opening_hours").getAsJsonArray("weekday_text")){

                        newPlace.getOpen().add(reference.getAsString());

                    }

            newPlace.setDescription( results.getAsJsonObject("editorial_summary") == null ? "" :results.getAsJsonObject("editorial_summary").get("overview").getAsString());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return newPlace;
    }

    public static Place getPlaceFromId(String id){

        Place newPlace = new Place();




        String placeDetails = String.format("https://maps.googleapis.com/maps/api/place/details/json?place_id=%s&fields=opening_hours,geometry,user_ratings_total,photo,rating,editorial_summary,name,price_level,type&language=en&key=AIzaSyDl8zk1Jv2eD3-u1RVdutitWTB2JlzrHwU", id);
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

            StringBuilder desc = new StringBuilder();
            for(JsonElement t : results.getAsJsonObject().getAsJsonArray("types")) {
                desc.append(Arrays.stream(t.getAsString().replaceAll("_", " ").split(" ")).map(e -> e.substring(0,1).toUpperCase() + e.substring(1)).collect(Collectors.joining(" "))).append(" ");
            }


            newPlace.setId(id);
            newPlace.setName(results.getAsJsonObject().get("name").getAsString());
            newPlace.setCategory(Arrays.stream(results.getAsJsonArray("types").get(0).getAsString().replaceAll("_", " ").split(" ")).map(e -> e.substring(0,1).toUpperCase() + e.substring(1)).collect(Collectors.joining(" ")));
            newPlace.setLat(results.getAsJsonObject().getAsJsonObject("geometry").getAsJsonObject("location").get("lat").getAsFloat());
            newPlace.setLng(results.getAsJsonObject().getAsJsonObject("geometry").getAsJsonObject("location").get("lng").getAsFloat());
            newPlace.setPricing(results.getAsJsonObject().get("price_level") == null ? -1 : results.getAsJsonObject().get("price_level").getAsInt());
            newPlace.setRating(results.getAsJsonObject().get("rating") == null ? -1 : results.getAsJsonObject().get("rating").getAsFloat());
            newPlace.setTotalRating(results.getAsJsonObject().get("user_ratings_total") == null ? -1 : results.getAsJsonObject().get("user_ratings_total").getAsInt());
            newPlace.setTypes(desc.toString());


            newPlace.setImageReference(new ArrayList<>());
            if(results.getAsJsonArray("photos") != null)
                for(JsonElement reference : results.getAsJsonArray("photos")){

                    String url = "https://maps.googleapis.com/maps/api/place/photo"
                            + "?maxheight=800"
                            + "&maxwidth=800"
                            + "&photo_reference="
                            +  reference.getAsJsonObject().getAsJsonPrimitive("photo_reference").getAsString()
                            + "&key="
                            + "AIzaSyDl8zk1Jv2eD3-u1RVdutitWTB2JlzrHwU";

                    newPlace.getImageReference().add(reference.getAsJsonObject().getAsJsonPrimitive("photo_reference").getAsString());

                }

            newPlace.setOpen(new ArrayList<>());
            if(results.getAsJsonObject("opening_hours") != null)
                if(results.getAsJsonObject("opening_hours").getAsJsonArray("weekday_text") != null)
                for(JsonElement reference : results.getAsJsonObject("opening_hours").getAsJsonArray("weekday_text")){

                    newPlace.getOpen().add(reference.getAsString());

                }

            newPlace.setDescription( results.getAsJsonObject("editorial_summary") == null ? "" :results.getAsJsonObject("editorial_summary").get("overview").getAsString());

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
