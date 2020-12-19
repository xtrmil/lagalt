package se.experis.com.case2020.lagalt.controllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import se.experis.com.case2020.lagalt.models.CommonResponse;
import se.experis.com.case2020.lagalt.models.user.UserProfileView;
import se.experis.com.case2020.lagalt.services.UserService;

@RestController
@RequestMapping(value = "/api/v1/", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<CommonResponse> getProfileUser(HttpServletRequest request, @RequestHeader String Authorization) {
        return userService.getUserProfile(request, Authorization);
    }

    @GetMapping("/users/{username}")
    public ResponseEntity<CommonResponse> getPublicUser(HttpServletRequest request, @PathVariable String username) {
        return userService.getPublicUserDetails(request, username);
    }

    @PutMapping("/profile")
    public ResponseEntity<CommonResponse> updateUser(HttpServletRequest request, @RequestBody UserProfileView user, @RequestHeader String Authorization) {
        return userService.updateUserDetails(request, user, Authorization);
    }
}