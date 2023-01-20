package com.example.travelplannerserver.plan;

import com.example.travelplannerserver.dto.IdsList;
import com.example.travelplannerserver.dto.PlacesRequest;
import com.example.travelplannerserver.place.Place;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

@RestController
@RequestMapping(path = "/plans")
public class PlanController {

    private final PlanService planService;

    @Autowired
    public PlanController(PlanService planService) {
        this.planService = planService;
    }

    @PostMapping("/location")
    public List<Place> getTestPlan(@RequestBody PlacesRequest request){
        return planService.getPlanFromGoogleApi(request);
    }

    @PostMapping("/ids")
    public List<Place> getPlans(@RequestBody IdsList request){
        return planService.getPlacesById(request.getIds());
    }

    @GetMapping("/photo/{imageReference}")
    public String getPhoto(@PathVariable(value="imageReference") String imageReference){
        return planService.getPhotoUrl(imageReference);
    }

}
