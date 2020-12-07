package se.experis.com.case2020.lagalt.controllers;

import java.util.concurrent.ExecutionException;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import se.experis.com.case2020.lagalt.models.UserProfile;
import se.experis.com.case2020.lagalt.services.UserService;

@RestController
@RequestMapping(value = "/api/v1/", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/getUserDetails")
    public UserProfile getUser(@RequestHeader String userId) throws InterruptedException, ExecutionException {
        return userService.getProfileUserDetails(userId);
    }

    @PostMapping("/createUser")
    public String postUser(@RequestBody UserProfile user) throws InterruptedException, ExecutionException {
        return userService.saveUserDetails(user);
    }

    @PutMapping("/updateUser")
    public String putUser(@RequestBody UserProfile user) throws InterruptedException, ExecutionException {
        return userService.updateUserDetails(user);
    }

    @PutMapping("/addToUser")
    public String addHistory(@RequestBody ObjectNode objectNode) throws ExecutionException, InterruptedException {
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