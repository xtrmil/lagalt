package se.experis.com.case2020.lagalt.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.experis.com.case2020.lagalt.models.CommonResponse;
import se.experis.com.case2020.lagalt.models.project.ProjectCreate;
import se.experis.com.case2020.lagalt.models.project.ProjectMember;
import se.experis.com.case2020.lagalt.services.ProjectService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.MalformedURLException;
import java.util.concurrent.ExecutionException;


@RestController
@RequestMapping(value = "/api/v1/", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProjectController {

    @Autowired
    ProjectService projectService;

    @GetMapping("/projects")
    public ResponseEntity<CommonResponse> getProjectSummarized(HttpServletRequest request, HttpServletResponse response, @RequestParam String search) throws ExecutionException, InterruptedException {
        return projectService.getProjectSearch(request, response, search);
    }
    @GetMapping("/projects/{projectId}")
    public ResponseEntity<CommonResponse> getProjectDetails(HttpServletRequest request, HttpServletResponse response,@PathVariable("projectId") String projectId,@RequestHeader String userId)
            throws ExecutionException, InterruptedException { return projectService.getProjectDetails(request, response, projectId, userId); }

    @PostMapping("/projects/new")
    public ResponseEntity<CommonResponse> createNewProject(HttpServletRequest request, HttpServletResponse response, @RequestBody ProjectCreate project) throws MalformedURLException {
        return projectService.createNewProject(request,response,project);
    }
}
