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
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
@NoArgsConstructor
@Setter
public class Place implements Comparable<Place>{

    String id;
    String name;
    double lat, lng;
    List<String> imageReference;
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

        String placeDetails = String.format("https://maps.googleapis.com/maps/api/place/details/json?place_id=%s&fields=photo,editorial_summary&key=AIzaSyDl8zk1Jv2eD3-u1RVdutitWTB2JlzrHwU", newPlace.getId());
        Request request = new Request.Builder()
                .url(placeDetails)
                .method("GET", null)
                .build();

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Response response = null;
        try {

            response = client.newCall(request).execute();
            if(response.body() == null) throw new IOException();
            Gson gson = new Gson();
            JsonObject results = gson.fromJson( response.body().string(), JsonObject.class).getAsJsonObject("result");

            newPlace.setImageReference(new ArrayList<>());

            if(results.getAsJsonArray("photos") != null)
            for(JsonElement reference : results.getAsJsonArray("photos")){
                newPlace.getImageReference().add(
                        "https://maps.googleapis.com/maps/api/place/photo?"
                                + "?maxheight=800"
                                + "&maxwidth=800"
                                + "&photo_reference="
                                +  reference.getAsJsonObject().getAsJsonPrimitive("photo_reference").getAsString()
                                + "&key="
                                + api);
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
