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
@RequestMapping(value = "/api/v1/projects/{owner}/{projectName}/messageboard", produces = MediaType.APPLICATION_JSON_VALUE)
public class MessageBoardController {

    @Autowired
    MessageBoardService messageBoardService;
    
    @PostMapping("")
    ResponseEntity<CommonResponse> createThread(HttpServletRequest request, HttpServletResponse response, @PathVariable String owner, @PathVariable String projectName,
    @RequestBody ObjectNode thread, @RequestHeader String Authorization) {
        return messageBoardService.createThread(request, response, owner, projectName, thread, Authorization);
    }
    
    @PostMapping("/{threadId}")
    ResponseEntity<CommonResponse> createPost(HttpServletRequest request, HttpServletResponse response, @PathVariable String owner, @PathVariable String projectName, 
    @PathVariable String threadId, @RequestBody ObjectNode post, @RequestHeader String Authorization) {
        return messageBoardService.createPost(request, response, owner, projectName, threadId, post, Authorization);
    }

    @DeleteMapping("/{threadId}/{messageId}")
    ResponseEntity<CommonResponse> deletePost(HttpServletRequest request, HttpServletResponse response,@PathVariable String owner, @PathVariable String projectName, 
    @PathVariable String threadId, @PathVariable String messageId, @RequestHeader String Authorization) {
        return messageBoardService.deletePost(request, response, owner, projectName, threadId, messageId, Authorization);
    }
}
