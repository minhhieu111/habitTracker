package com.example.habittracker.Controller.client;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("challenge_community/")
public class ChallengeCommunityController {
    @GetMapping("")
    public String challengeCommunityPage(){
        return "client/challengeCommunity";
    }
}
