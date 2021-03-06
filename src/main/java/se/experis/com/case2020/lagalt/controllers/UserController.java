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
import se.experis.com.case2020.lagalt.utils.RequestLimiter;

@RestController
@RequestMapping(value = "/api/v1/", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RequestLimiter requestLimiter;

    @GetMapping("/profile")
    public ResponseEntity<CommonResponse> getProfileUser(HttpServletRequest request,
            @RequestHeader String Authorization) {
        if (!requestLimiter.isRequestBlocked(request)) {
            return requestLimiter.filter(request, userService.getUserProfile(request, Authorization));
        }
        return requestLimiter.getBlockedResponse();
    }

    @GetMapping("/users/{username}")
    public ResponseEntity<CommonResponse> getPublicUser(HttpServletRequest request, @PathVariable String username, @RequestHeader(required = false) String Authorization, @RequestHeader(required = false) String projectId) {
        if (!requestLimiter.isRequestBlocked(request)) {
            return requestLimiter.filter(request, userService.getPublicUserDetails(request, username,Authorization,projectId));
        }
        return requestLimiter.getBlockedResponse();
    }

    @PutMapping("/profile")
    public ResponseEntity<CommonResponse> updateUser(HttpServletRequest request, @RequestBody UserProfileView user,
            @RequestHeader String Authorization) {
        if (!requestLimiter.isRequestBlocked(request)) {
            return requestLimiter.filter(request, userService.updateUserDetails(request, user, Authorization));
        }
        return requestLimiter.getBlockedResponse();
    }
}