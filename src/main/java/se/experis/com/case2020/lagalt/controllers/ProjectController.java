package se.experis.com.case2020.lagalt.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.experis.com.case2020.lagalt.models.CommonResponse;
import se.experis.com.case2020.lagalt.models.project.ProjectMember;
import se.experis.com.case2020.lagalt.models.project.ProjectNonMember;
import se.experis.com.case2020.lagalt.services.ProjectService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.ExecutionException;


@RestController
@RequestMapping(value = "/api/v1/projects", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProjectController {

    @Autowired
    ProjectService projectService;


    @GetMapping("/test")
    public String smt() {
        return projectService.testQuery();
    }

    @GetMapping("/")
    public ResponseEntity<CommonResponse> getProjectSearch(HttpServletRequest request, HttpServletResponse response, @RequestParam String search) throws ExecutionException, InterruptedException {
        return projectService.getProjectSearch(request, response, search);
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<CommonResponse> getProjectDetails(HttpServletRequest request, HttpServletResponse response, @PathVariable("projectId") String projectId, @RequestHeader String Authorization)
            throws ExecutionException, InterruptedException {
        return projectService.getProjectDetails(request, response, projectId, Authorization);
    }

    @PostMapping("/new")
    public ResponseEntity<CommonResponse> createNewProject(HttpServletRequest request, HttpServletResponse response, @RequestBody ProjectNonMember project, @RequestHeader String Authorization) {
        return projectService.createNewProject(request, project, Authorization);
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<CommonResponse> updateProjectDetails(HttpServletRequest request, HttpServletResponse response, @RequestHeader String Authorization, @PathVariable("projectId") String projectId, @RequestBody ProjectMember project) throws ExecutionException, InterruptedException {
        return projectService.updateProjectDetails(request, project, Authorization);
    }
}
