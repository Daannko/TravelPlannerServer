package com.example.travelplannerserver.plan;

import com.example.travelplannerserver.dto.ListOfStrings;
import com.example.travelplannerserver.dto.MyText;
import com.example.travelplannerserver.dto.PlacesRequest;
import com.example.travelplannerserver.place.Place;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

@RestController
@RequestMapping(path = "/plan")
public class PlanController {

    private final PlanService planService;

    @Autowired
    public PlanController(PlanService planService) {
        this.planService = planService;
    }

    @PostMapping("/test")
    public List<Place> getTestPlan(@RequestBody PlacesRequest request){
        long start =System.currentTimeMillis();
        List<Place> places = planService.getPlanFromGoogleApi(request);
        long finish =System.currentTimeMillis();
        System.out.println(finish - start);
        return places;
    }

    @PostMapping("/ids")
    public List<Place> getPlans(@RequestBody ListOfStrings request){
        return planService.getPlacesById(request.getIds());
    }

    @PostMapping("/text")
    public PlacesRequest getText(@RequestBody PlacesRequest request){
        return request;
    }

    @GetMapping("/photo/{imageReference}")
    public String getPhoto(@PathVariable(value="imageReference") String imageReference){

        String url = "https://maps.googleapis.com/maps/api/place/photo"
                + "?maxheight=800"
                + "&maxwidth=800"
                + "&photo_reference="
                +  imageReference
                + "&key="
                + "AIzaSyDl8zk1Jv2eD3-u1RVdutitWTB2JlzrHwU";

        URLConnection con = null;
        String newurl;
        try {

            con = new URL( url ).openConnection();
            System.out.println(con.getHeaderFields());
            con.connect();
            InputStream is = con.getInputStream();
            newurl = con.getURL().toString();
            is.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println(newurl);
        return newurl;
    }

}
