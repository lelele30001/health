package com.cl.controller;

import com.cl.annotation.IgnoreAuth;
import com.cl.service.StatsService;
import com.cl.utils.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/stats")
public class StatsController {
    
    @Resource
    private StatsService statsService;
    
    @GetMapping(value = "/userStats", produces = "application/json; charset=utf-8")
    @IgnoreAuth
    public R getUserStats() {
        return statsService.getUserStats();
    }
    
    @GetMapping(value = "/userGenderStats", produces = "application/json; charset=utf-8")
    @IgnoreAuth
    public R getUserGenderStats() {
        return statsService.getUserGenderStats();
    }
    
    @GetMapping(value = "/userAgeStats", produces = "application/json; charset=utf-8")
    @IgnoreAuth
    public R getUserAgeStats() {
        return statsService.getUserAgeStats();
    }
    
    @GetMapping(value = "/userGoalStats", produces = "application/json; charset=utf-8")
    @IgnoreAuth
    public R getUserGoalStats() {
        return statsService.getUserGoalStats();
    }
}
