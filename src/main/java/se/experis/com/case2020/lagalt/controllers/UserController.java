package se.experis.com.case2020.lagalt.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import se.experis.com.case2020.lagalt.models.User;
import se.experis.com.case2020.lagalt.services.UserService;

import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping(value = "/api/v1/", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/getUserDetails")
    public User getUser(@RequestHeader String userId) throws InterruptedException, ExecutionException {
        return userService.getUserDetails(userId);
    }

    @PostMapping("/createUser")
    public String postUser(@RequestBody User user) throws InterruptedException, ExecutionException {
        return userService.saveUserDetails(user);
    }

    @PutMapping("/updateUser")
    public String putUser(@RequestBody User user) throws InterruptedException, ExecutionException {
        return userService.updateUserDetails(user);
    }

    @PutMapping("/addToUser")
    public String addHistory(@RequestBody ObjectNode objectNode) throws ExecutionException, InterruptedException {
        return userService.addToUser(objectNode.get("userId").asText(),objectNode.get("category").asText(),objectNode.get("projectId").asText());
    }

    @DeleteMapping("/deleteFromUser")
    public String deleteFromUser(@RequestBody ObjectNode objectNode) throws ExecutionException, InterruptedException {
        return userService.deleteFromUser(objectNode.get("userId").asText(),objectNode.get("category").asText(),objectNode.get("projectId").asText());
    }

    @DeleteMapping("/deleteUser")
    public String deleteUser(@RequestHeader String userId) {
        return userService.deleteUser(userId);
    }
}