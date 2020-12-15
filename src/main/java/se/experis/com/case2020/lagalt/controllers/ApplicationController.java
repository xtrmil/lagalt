package se.experis.com.case2020.lagalt.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.experis.com.case2020.lagalt.models.CommonResponse;
import se.experis.com.case2020.lagalt.services.ApplicationService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping(value = "/api/v1/projects", produces = MediaType.APPLICATION_JSON_VALUE)
public class ApplicationController {

     @Autowired
    ApplicationService applicationService;
    @GetMapping("/{projectId}/applications")
    public ResponseEntity<CommonResponse> getApplications(HttpServletRequest request, HttpServletResponse response, @PathVariable("projectId") String projectId, @RequestHeader String Authorization){
        return applicationService.getApplications(request, response, projectId, Authorization);
    }

    @PostMapping("/{projectId}/application")
    ResponseEntity<CommonResponse> createApplication(HttpServletRequest request, HttpServletResponse response, @PathVariable("projectId") String projectId, @RequestHeader String Authorization, @RequestBody ObjectNode motivation){
        return applicationService.createApplication(request, response, projectId, Authorization, motivation);
    }

    @PutMapping("/{projectId}/application")
    ResponseEntity<CommonResponse> updateApplication(HttpServletRequest request, HttpServletResponse response, @PathVariable("projectId") String projectId, @RequestBody ObjectNode application, @RequestHeader String Authorization) throws ExecutionException, InterruptedException {
        return applicationService.updateApplication(request, response, projectId, application, Authorization);
    }
}
