package se.experis.com.case2020.lagalt.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import se.experis.com.case2020.lagalt.models.CommonResponse;
import se.experis.com.case2020.lagalt.services.ChatService;

@RestController
@RequestMapping(value = "/api/v1/projects/{projectOwner}/{projectName}/chat", produces = MediaType.APPLICATION_JSON_VALUE)
public class ChatController {

    @Autowired
    private ChatService chatService;

    @GetMapping("")
    public ResponseEntity<CommonResponse> getChatPath(@PathVariable String projectOwner, @PathVariable String projectName,
    @RequestHeader String Authorization) {
        return chatService.getChatDBPath(projectOwner, projectName, Authorization);
    }
    
    @PostMapping("")
    public ResponseEntity<CommonResponse> createChatMessage(@PathVariable String projectOwner, @PathVariable String projectName,
    @RequestBody ObjectNode message, @RequestHeader String Authorization) {
        return chatService.createChatMessage(projectOwner, projectName, message.get("text").asText(), Authorization);
    }
}
