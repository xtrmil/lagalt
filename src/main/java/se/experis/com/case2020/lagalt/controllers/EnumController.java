package se.experis.com.case2020.lagalt.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.experis.com.case2020.lagalt.models.CommonResponse;
import se.experis.com.case2020.lagalt.models.enums.ApplicationStatus;
import se.experis.com.case2020.lagalt.models.enums.Industry;
import se.experis.com.case2020.lagalt.models.enums.ProjectStatus;
import se.experis.com.case2020.lagalt.models.enums.Tag;
import se.experis.com.case2020.lagalt.services.UserService;
import se.experis.com.case2020.lagalt.utils.Command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@RestController
@RequestMapping(value = "/api/v1/available", produces = MediaType.APPLICATION_JSON_VALUE)
public class EnumController {

//    @Autowired
//    UserService userService;

    @GetMapping("/industries")
    public ResponseEntity<CommonResponse> getIndustries(HttpServletRequest request, HttpServletResponse response) {
        Command cmd = new Command(request);
        CommonResponse cr = new CommonResponse();
        HttpStatus resp = HttpStatus.OK;
        response.addHeader("Location", "/available/industries/");
        cr.data = Stream.of(Industry.values())
                .collect(Collectors.toMap(i -> i.name(), i -> i.INDUSTRY_NAME));
        cr.message = "Successfully retrieved available Industries";
        cmd.setResult(resp);
        return new ResponseEntity<>(cr, resp);
    }

    @GetMapping("/tags")
    public ResponseEntity<CommonResponse> getTags(HttpServletRequest request, HttpServletResponse response) {
        Command cmd = new Command(request);
        CommonResponse cr = new CommonResponse();
        HttpStatus resp = HttpStatus.OK;
        response.addHeader("Location", "/available/tags/");
        cr.data = Stream.of(Tag.values())
                .collect(Collectors.toMap(i -> i.name(), i -> i.DISPLAY_TAG));
        cr.message = "Successfully retrieved available Tags";
        cmd.setResult(resp);
        return new ResponseEntity<>(cr, resp);
    }

    @GetMapping("/tags/{industry}")
    public ResponseEntity<CommonResponse> getTagsInIndustry(HttpServletRequest request, HttpServletResponse response, @PathVariable("industry") String industryName) {
        Command cmd = new Command(request);
        CommonResponse cr = new CommonResponse();
        HttpStatus resp = HttpStatus.OK;
        response.addHeader("Location", "/available/tags/" + industryName);

        cr.data = Stream.of(Tag.values())
                .filter(tag -> tag.INDUSTRY.equals(industryName))
                .collect(Collectors.toMap(i -> i.name(), i -> i.DISPLAY_TAG));
        cr.message = "Successfully retrieved available Tags from industry: " + industryName;
        cmd.setResult(resp);
        return new ResponseEntity<>(cr, resp);
    }

    @GetMapping("/projectStatuses")
    public ResponseEntity<CommonResponse> getProjectStatuses(HttpServletRequest request, HttpServletResponse response) {
        Command cmd = new Command(request);
        CommonResponse cr = new CommonResponse();
        HttpStatus resp = HttpStatus.OK;
        response.addHeader("Location", "/available/projectStatuses/");
        cr.data = Stream.of(ProjectStatus.values())
                .collect(Collectors.toMap(i -> i.name(), i -> i.STATUS));
        cr.message = "Successfully retrieved available ProjectStatuses";
        cmd.setResult(resp);
        return new ResponseEntity<>(cr, resp);
    }

    @GetMapping("/applicationStatuses")
    public ResponseEntity<CommonResponse> getApplicationStatuses(HttpServletRequest request, HttpServletResponse response) {
        Command cmd = new Command(request);
        CommonResponse cr = new CommonResponse();
        HttpStatus resp = HttpStatus.OK;
        response.addHeader("Location", "/available/applicationStatuses/");

        cr.data = Stream.of(ApplicationStatus.values())
                .collect(Collectors.toMap(i -> i.name(), i -> i.STATUS));
        cr.message = "Successfully retrieved available ApplicationStatuses";
        cmd.setResult(resp);
        return new ResponseEntity<>(cr, resp);
    }
}
