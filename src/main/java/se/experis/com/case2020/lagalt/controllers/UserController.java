package se.experis.com.case2020.lagalt.controllers;

import java.util.AbstractMap;
import java.util.concurrent.ExecutionException;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import se.experis.com.case2020.lagalt.models.user.UserProfile;
import se.experis.com.case2020.lagalt.models.user.UserPublic;
import se.experis.com.case2020.lagalt.services.UserService;

@RestController
@RequestMapping(value = "/api/v1/", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/profile")
    public UserProfile getProfileUser(@RequestBody ObjectNode objectNode) throws InterruptedException, ExecutionException {
        return userService.getProfileUserDetails(objectNode.get("userId").asText());
    }


    @GetMapping("/users/{id}")
    public UserPublic getPublicUser(@PathVariable("id") String userId) throws InterruptedException, ExecutionException {
        return userService.getPublicUserDetails(userId);
    }


    @PostMapping("/users/new")
    public String postUser(@RequestBody UserProfile user) throws InterruptedException, ExecutionException {
        return userService.saveUserDetails(user);
    }

    @PutMapping("/profile")
    public String putUser(@RequestBody UserProfile user) throws InterruptedException, ExecutionException {
        return userService.updateUserDetails(user);
    }

    @PutMapping("/addToUser")
    public String addToUser(@RequestBody ObjectNode objectNode) throws ExecutionException, InterruptedException {
        return userService.addToUser(objectNode.get("userId").asText(), objectNode.get("category").asText(),
                objectNode.get("projectId").asText());
    }

    @DeleteMapping("/deleteFromUser")
    public String deleteFromUser(@RequestBody ObjectNode objectNode) throws ExecutionException, InterruptedException {
        return userService.deleteFromUser(objectNode.get("userId").asText(), objectNode.get("category").asText(),
                objectNode.get("projectId").asText());
    }

    @DeleteMapping("/deleteUser")
    public String deleteUser(@RequestHeader String userId) {
        return userService.deleteUser(userId);
    }
}