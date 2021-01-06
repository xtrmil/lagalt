package se.experis.com.case2020.lagalt.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import se.experis.com.case2020.lagalt.models.CommonResponse;
import se.experis.com.case2020.lagalt.models.project.ProjectMemberView;
import se.experis.com.case2020.lagalt.models.project.ProjectNonMemberView;
import se.experis.com.case2020.lagalt.services.ProjectService;
import se.experis.com.case2020.lagalt.utils.RequestLimiter;

@RestController
@RequestMapping(value = "/api/v1/projects", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private RequestLimiter requestLimiter;

    @GetMapping("")
    public ResponseEntity<CommonResponse> getProjectSearch(HttpServletRequest request, HttpServletResponse response,
            @RequestParam(required = false) String search, @RequestHeader(required = false) String Authorization,
            @RequestBody(required = false) ObjectNode timestamp) {
        if (search != null) {
            return projectService.getProjectsSearch(request, search);
        } else {
            if (Authorization != null) {
                return projectService.getProjectsBasedOnHistory(request, response, Authorization);
            }
            return projectService.getProjects(request, response, timestamp);
        }
    }

    @GetMapping("/{owner}/{projectName}")
    public ResponseEntity<CommonResponse> getProjectDetails(HttpServletRequest request, @PathVariable String owner,
            @PathVariable String projectName, @RequestHeader String Authorization) {
        return projectService.getProjectDetails(request, owner, projectName, Authorization);
    }

    @PostMapping("/new")
    public ResponseEntity<CommonResponse> createNewProject(HttpServletRequest request,
            HttpServletResponse servletResponse, @RequestBody ProjectNonMemberView project,
            @RequestHeader String Authorization) {
        if (!requestLimiter.isRequestBlocked(request)) {
            return requestLimiter.filter(request, projectService.createNewProject(request, project, Authorization));
        }
        return requestLimiter.getBlockedResponse();
    }

    @PutMapping("/{owner}/{projectName}")
    public ResponseEntity<CommonResponse> updateProjectDetails(HttpServletRequest request,
            HttpServletResponse servletResponse, @RequestHeader String Authorization, @PathVariable String owner,
            @PathVariable String projectName, @RequestBody ProjectMemberView project) {
        if (!requestLimiter.isRequestBlocked(request)) {
            return requestLimiter.filter(request,
                    projectService.updateProjectDetails(request, owner, projectName, project, Authorization));
        }
        return requestLimiter.getBlockedResponse();
    }
}
