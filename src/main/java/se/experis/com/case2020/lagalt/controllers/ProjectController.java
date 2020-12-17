package se.experis.com.case2020.lagalt.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.experis.com.case2020.lagalt.models.CommonResponse;
import se.experis.com.case2020.lagalt.models.project.ProjectMemberView;
import se.experis.com.case2020.lagalt.models.project.ProjectNonMemberView;
import se.experis.com.case2020.lagalt.services.ProjectService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.ExecutionException;


@RestController
@RequestMapping(value = "/api/v1/projects", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProjectController {

    @Autowired
    ProjectService projectService;

    @GetMapping("")
    public ResponseEntity<CommonResponse> getProjectSearch(HttpServletRequest request, HttpServletResponse response, @RequestParam(required = false) String search)
    throws ExecutionException, InterruptedException {
        if(search == null ) {
            return projectService.getProjectsSearch(request, search);
        }
        return projectService.getProjects(request, response);
    }

    @GetMapping("/{owner}/{projectName}")
    public ResponseEntity<CommonResponse> getProjectDetails(HttpServletRequest request, @PathVariable String owner, 
    @PathVariable String projectName, @RequestHeader String Authorization) {
        return projectService.getProjectDetails(request, owner, projectName, Authorization);
    }

    @PostMapping("/new")
    public ResponseEntity<CommonResponse> createNewProject(HttpServletRequest request, HttpServletResponse response, @RequestBody ProjectNonMemberView project, 
    @RequestHeader String Authorization) {
        return projectService.createNewProject(request, project, Authorization);
    }

    @PutMapping("/{owner}/{projectName}")
    public ResponseEntity<CommonResponse> updateProjectDetails(HttpServletRequest request, HttpServletResponse response, @RequestHeader String Authorization,
    @PathVariable String owner, @PathVariable String projectName, @RequestBody ProjectMemberView project) {
        return projectService.updateProjectDetails(request, owner, projectName, project, Authorization);
    }
}
