package se.experis.com.case2020.lagalt.controllers;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/projects/{projectId}/chat", produces = MediaType.APPLICATION_JSON_VALUE)
public class ChatController {
}
