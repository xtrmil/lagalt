package se.experis.com.case2020.lagalt.controllers;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import se.experis.com.case2020.lagalt.models.CommonResponse;
import se.experis.com.case2020.lagalt.services.ApplicationService;

@RestController
@RequestMapping(value = "/api/v1/projects/{owner}/{projectName}/applications", produces = MediaType.APPLICATION_JSON_VALUE)
public class ApplicationController {

     @Autowired
    ApplicationService applicationService;
    @GetMapping("")
    public ResponseEntity<CommonResponse> getApplications(HttpServletRequest request, @PathVariable String owner, @PathVariable String projectName,
    @RequestHeader String Authorization) {
        return applicationService.getApplications(request, owner, projectName, Authorization);
    }

    @PostMapping("")
    ResponseEntity<CommonResponse> createApplication(HttpServletRequest request, @PathVariable String owner, @PathVariable String projectName, @RequestHeader String Authorization, 
    @RequestBody ObjectNode motivation) {
        return applicationService.createApplication(request, owner, projectName, Authorization, motivation);
    }

    @PutMapping("/{applicationId}")
    ResponseEntity<CommonResponse> answerApplication(HttpServletRequest request, @PathVariable String owner, @PathVariable String projectName, @PathVariable String applicationId, 
    @RequestBody ObjectNode application, @RequestHeader String Authorization) {
        return applicationService.answerApplication(request, owner, projectName, applicationId, application, Authorization);
    }
}
