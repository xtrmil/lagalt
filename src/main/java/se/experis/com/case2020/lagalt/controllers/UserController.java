package se.experis.com.case2020.lagalt.controllers;


import java.util.concurrent.ExecutionException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.experis.com.case2020.lagalt.models.CommonResponse;
import se.experis.com.case2020.lagalt.models.user.UserProfile;
import se.experis.com.case2020.lagalt.services.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(value = "/api/v1/", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<CommonResponse> getProfileUser(HttpServletRequest request, HttpServletResponse response, @RequestBody ObjectNode objectNode) throws InterruptedException, ExecutionException {
        return userService.getProfileUserDetails(request, response, objectNode.get("userId").asText());
}


    @GetMapping("/users/{id}")
    public ResponseEntity<CommonResponse> getPublicUser(HttpServletRequest request, HttpServletResponse response,@PathVariable("id") String userId) throws InterruptedException, ExecutionException {
        return userService.getPublicUserDetails(request, response, userId);
    }


    @PostMapping("/users/new")
    public String postUser(@RequestBody UserProfile user) throws InterruptedException, ExecutionException {
        return userService.saveUserDetails(user);
    }

    @PutMapping("/profile")
    public ResponseEntity<CommonResponse> updateUser(HttpServletRequest request, HttpServletResponse response, @RequestBody UserProfile user) throws InterruptedException, ExecutionException {
        return userService.updateUserDetails(request, response,user);
    }

    @PutMapping("/addToUser")
    public ResponseEntity<CommonResponse> addToUser(HttpServletRequest request, HttpServletResponse response, @RequestBody ObjectNode objectNode){
        return userService.addToUser(request, response, objectNode.get("userId").asText(), objectNode.get("category").asText(),
                objectNode.get("projectId").asText());
    }

    @DeleteMapping("/deleteFromUser")
    public ResponseEntity<CommonResponse> deleteFromUser(HttpServletRequest request, HttpServletResponse response, @RequestBody ObjectNode objectNode) throws ExecutionException, InterruptedException {
        return userService.deleteFromUser(request ,response, objectNode.get("userId").asText(), objectNode.get("category").asText(),
                objectNode.get("projectId").asText());
    }

    @DeleteMapping("/deleteUser")
    public ResponseEntity<CommonResponse> deleteUser(HttpServletRequest request, HttpServletResponse response, @RequestHeader String userId) throws ExecutionException, InterruptedException {
        return userService.deleteUser(request, response,userId);
    }
}