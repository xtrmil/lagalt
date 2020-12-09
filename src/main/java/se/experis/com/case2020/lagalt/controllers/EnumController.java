package se.experis.com.case2020.lagalt.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import se.experis.com.case2020.lagalt.models.enums.ApplicationStatus;
import se.experis.com.case2020.lagalt.models.enums.Industry;
import se.experis.com.case2020.lagalt.models.enums.ProjectStatus;
import se.experis.com.case2020.lagalt.models.enums.Tag;
import se.experis.com.case2020.lagalt.models.user.UserProfile;
import se.experis.com.case2020.lagalt.services.UserService;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.EnumUtils;

@RestController
@RequestMapping(value = "/api/v1/available", produces = MediaType.APPLICATION_JSON_VALUE)
public class EnumController {

    @Autowired
    UserService userService;

    @GetMapping("/industries")
    public Map getIndustries(){
        return Stream.of(Industry.values())
                .collect(Collectors.toMap(i -> i.name(), i -> i.INDUSTRY_NAME));
    }

    @GetMapping("/tags")
    public Map getTags(){
        return Stream.of(Tag.values())
                .collect(Collectors.toMap(i -> i.name(), i -> i.DISPLAY_TAG));
    }

    @GetMapping("/tags/{industry}")
    public Map getTagsInIndustry(@PathVariable("industry") String industryName){
        return Stream.of(Tag.values())
                .filter(tag -> tag.INDUSTRY.equals(industryName))
                .collect(Collectors.toMap(i -> i.name(), i -> i.DISPLAY_TAG));
    }

    @GetMapping("/projectStatuses")
    public Map getProjectStatuses(){
        return Stream.of(ProjectStatus.values())
                .collect(Collectors.toMap(i -> i.name(), i -> i.STATUS));
    }

    @GetMapping("/applicationStatuses")
    public Map getApplicationStatuses(){
        return Stream.of(ApplicationStatus.values())
                .collect(Collectors.toMap(i -> i.name(), i -> i.STATUS));
    }

}
