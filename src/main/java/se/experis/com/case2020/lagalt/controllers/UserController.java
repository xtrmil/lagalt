package se.experis.com.case2020.lagalt.controllers;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.experis.com.case2020.lagalt.models.CommonResponse;
import se.experis.com.case2020.lagalt.models.user.UserProfileView;
import se.experis.com.case2020.lagalt.services.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(value = "/api/v1/", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<CommonResponse> getProfileUser(HttpServletRequest request, @RequestHeader String Authorization) throws InterruptedException, ExecutionException {
        return userService.getUserProfile(request, Authorization);
    }

    @GetMapping("/users/{username}")
    public ResponseEntity<CommonResponse> getPublicUser(HttpServletRequest request, @PathVariable("username") String username) throws InterruptedException, ExecutionException {
        return userService.getPublicUserDetails(request, username);
    }

    @PutMapping("/profile")
    public ResponseEntity<CommonResponse> updateUser(HttpServletRequest request, HttpServletResponse response, @RequestBody UserProfileView user, @RequestHeader String Authorization) throws InterruptedException, ExecutionException {
        return userService.updateUserDetails(request, response, user, Authorization);
    }
}