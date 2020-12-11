package se.experis.com.case2020.lagalt.controllers;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.experis.com.case2020.lagalt.models.CommonResponse;
import se.experis.com.case2020.lagalt.models.user.UserPrivate;
import se.experis.com.case2020.lagalt.services.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(value = "/api/v1/", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<CommonResponse> getProfileUser(HttpServletRequest request, HttpServletResponse response, @RequestHeader String Authorization) throws InterruptedException, ExecutionException {
        return userService.getExtendedUserDetails(request, response, Authorization);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<CommonResponse> getPublicUser(HttpServletRequest request, HttpServletResponse response, @PathVariable("id") String userId) throws InterruptedException, ExecutionException {
        return userService.getUserDetails(request, response, userId);
    }

    @PutMapping("/profile")
    public ResponseEntity<CommonResponse> updateUser(HttpServletRequest request, HttpServletResponse response, @RequestBody UserPrivate user) throws InterruptedException, ExecutionException {
        return userService.updateUserDetails(request, response, user);
    }
}