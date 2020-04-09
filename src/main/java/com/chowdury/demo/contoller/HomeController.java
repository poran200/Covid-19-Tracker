package com.chowdury.demo.contoller;

import com.chowdury.demo.models.LocationStats;
import com.chowdury.demo.services.CoronaVirusDataServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {
    @Autowired
    CoronaVirusDataServices dataServices;

    @GetMapping(value = "/")
    public String hello(Model model){
        List<LocationStats> allStats = dataServices.getAllStats();
        int totalReportedCases = allStats.stream().mapToInt(LocationStats::getLatestTotalCase).sum();
        int totalNewCases = allStats.stream().mapToInt(LocationStats::getDiffFromPreviousDay).sum();
        model.addAttribute("locationStats",allStats);
        model.addAttribute("totalReportedCases", totalReportedCases);
        model.addAttribute("totalNewCases",totalNewCases);
        return "index";
    }
}
