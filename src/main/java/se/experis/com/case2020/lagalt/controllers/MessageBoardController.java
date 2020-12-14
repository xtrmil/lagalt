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
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping(value = "/api/v1/projects", produces = MediaType.APPLICATION_JSON_VALUE)
public class MessageBoardController {

    @Autowired
    MessageBoardService messageBoardService;
    @PostMapping("/{projectId}/messageBoard/thread/")
        //userId saknas änsålänge + auth
    ResponseEntity<CommonResponse> createThread(HttpServletRequest request, HttpServletResponse response, @PathVariable("projectId") String projectId, @RequestBody ObjectNode objectNode) throws ExecutionException, InterruptedException {
        return messageBoardService.createThread(request,response,projectId,objectNode);
    }
    //userId saknas änsålänge + auth
    @PostMapping("/{projectId}/messageBoard/{threadId}")
    ResponseEntity<CommonResponse> createPost(HttpServletRequest request, HttpServletResponse response, @PathVariable("projectId") String projectId, @PathVariable("threadId") String threadId, @RequestBody ObjectNode objectNode) throws ExecutionException, InterruptedException {
        return messageBoardService.createPost(request,response,projectId,threadId,objectNode);

    }

    @DeleteMapping("/{projectId}/messageBoard/{threadId}")
    ResponseEntity<CommonResponse> deletePost(HttpServletRequest request, HttpServletResponse response, @PathVariable("projectId") String projectId, @PathVariable("threadId") String threadId, @RequestBody ObjectNode objectNode) throws ExecutionException, InterruptedException {
        return messageBoardService.deletePost(request, response, projectId, threadId, objectNode);
    }
}
