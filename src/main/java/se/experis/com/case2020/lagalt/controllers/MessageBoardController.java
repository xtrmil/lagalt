package se.experis.com.case2020.lagalt.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import se.experis.com.case2020.lagalt.models.CommonResponse;
import se.experis.com.case2020.lagalt.services.MessageBoardService;
import se.experis.com.case2020.lagalt.utils.RequestLimiter;

@RestController
@RequestMapping(value = "/api/v1/projects/{projectOwner}/{projectName}/messageboard", produces = MediaType.APPLICATION_JSON_VALUE)
public class MessageBoardController {

    @Autowired
    private MessageBoardService messageBoardService;

    @Autowired
    private RequestLimiter requestLimiter;

    @GetMapping("")
    public ResponseEntity<CommonResponse> getAllThreads(HttpServletRequest request, @PathVariable String projectOwner,
            @PathVariable String projectName, @RequestHeader String Authorization) {
        if (!requestLimiter.isRequestBlocked(request)) {
            return requestLimiter.filter(request,
                    messageBoardService.getAllThreads(request, projectOwner, projectName, Authorization));
        }
        return requestLimiter.getBlockedResponse();
    }

    @PostMapping("")
    public ResponseEntity<CommonResponse> createThread(HttpServletRequest request, HttpServletResponse servletResponse,
            @PathVariable String projectOwner, @PathVariable String projectName, @RequestBody ObjectNode thread,
            @RequestHeader String Authorization) {
        if (!requestLimiter.isRequestBlocked(request)) {
            return requestLimiter.filter(request, messageBoardService.createThread(request, servletResponse,
                    projectOwner, projectName, thread, Authorization));
        }
        return requestLimiter.getBlockedResponse();
    }

    @PostMapping("/{threadId}")
    public ResponseEntity<CommonResponse> createPost(HttpServletRequest request, HttpServletResponse servletResponse,
            @PathVariable String projectOwner, @PathVariable String projectName, @PathVariable String threadId,
            @RequestBody ObjectNode post, @RequestHeader String Authorization) {
        if (!requestLimiter.isRequestBlocked(request)) {
            return requestLimiter.filter(request, messageBoardService.createPost(request, servletResponse, projectOwner,
                    projectName, threadId, post, Authorization));
        }
        return requestLimiter.getBlockedResponse();
    }

    @GetMapping("/{threadId}")
    public ResponseEntity<CommonResponse> getPosts(HttpServletRequest request, @PathVariable String projectOwner,
            @PathVariable String projectName, @PathVariable String threadId, @RequestHeader String Authorization) {
        if (!requestLimiter.isRequestBlocked(request)) {
            return requestLimiter.filter(request,
                    messageBoardService.getPosts(request, projectOwner, projectName, threadId, Authorization));
        }
        return requestLimiter.getBlockedResponse();
    }

    @DeleteMapping("/{threadId}/{messageId}")
    public ResponseEntity<CommonResponse> deletePost(HttpServletRequest request, @PathVariable String projectOwner,
            @PathVariable String projectName, @PathVariable String threadId, @PathVariable String messageId,
            @RequestHeader String Authorization) {
        if (!requestLimiter.isRequestBlocked(request)) {
            return requestLimiter.filter(request, messageBoardService.deletePost(request, projectOwner, projectName,
                    threadId, messageId, Authorization));
        }
        return requestLimiter.getBlockedResponse();
    }
}
