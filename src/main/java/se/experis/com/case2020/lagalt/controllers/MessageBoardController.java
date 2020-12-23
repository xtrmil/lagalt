package se.experis.com.case2020.lagalt.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.experis.com.case2020.lagalt.models.CommonResponse;
import se.experis.com.case2020.lagalt.services.MessageBoardService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(value = "/api/v1/projects/{projectOwner}/{projectName}/messageboard", produces = MediaType.APPLICATION_JSON_VALUE)
public class MessageBoardController {

    @Autowired
    private MessageBoardService messageBoardService;
    
    @GetMapping("")
    public ResponseEntity<CommonResponse> getAllThreads(HttpServletRequest request, @PathVariable String projectOwner, @PathVariable String projectName,
    @RequestHeader String Authorization) {
        return messageBoardService.getAllThreads(request, projectOwner, projectName, Authorization);
    }

    @PostMapping("")
    public ResponseEntity<CommonResponse> createThread(HttpServletRequest request, HttpServletResponse response, @PathVariable String projectOwner, @PathVariable String projectName,
    @RequestBody ObjectNode thread, @RequestHeader String Authorization) {
        return messageBoardService.createThread(request, response, projectOwner, projectName, thread, Authorization);
    }
    
    @PostMapping("/{threadId}")
    public ResponseEntity<CommonResponse> createPost(HttpServletRequest request, HttpServletResponse response, @PathVariable String projectOwner, @PathVariable String projectName, 
    @PathVariable String threadId, @RequestBody ObjectNode post, @RequestHeader String Authorization) {
        return messageBoardService.createPost(request, response, projectOwner, projectName, threadId, post, Authorization);
    }

    @GetMapping("/{threadId}")
    public ResponseEntity<CommonResponse> getPosts(HttpServletRequest request, @PathVariable String projectOwner, @PathVariable String projectName, 
    @PathVariable String threadId, @RequestHeader String Authorization){
        return messageBoardService.getPosts(request, projectOwner, projectName, threadId, Authorization);
    }

    @DeleteMapping("/{threadId}/{messageId}")
    public ResponseEntity<CommonResponse> deletePost(HttpServletRequest request, @PathVariable String projectOwner, @PathVariable String projectName, 
    @PathVariable String threadId, @PathVariable String messageId, @RequestHeader String Authorization) {
        return messageBoardService.deletePost(request, projectOwner, projectName, threadId, messageId, Authorization);
    }
}
