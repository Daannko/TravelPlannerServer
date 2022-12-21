package com.example.travelplannerserver.plan;

import com.example.travelplannerserver.dto.MyText;
import com.example.travelplannerserver.dto.PlacesRequest;
import com.example.travelplannerserver.dto.MyText;
import com.example.travelplannerserver.place.Place;
import okhttp3.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
        return planService.getPlanFromGoogleApi(request);

    }

    @PostMapping("/text")
    public PlacesRequest getText(@RequestBody PlacesRequest request){
        System.out.println(request.getLat());
        return request;
    }

    @GetMapping("/photo/{imageReference}")
    public

}
