package se.experis.com.case2020.lagalt.controllers;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import se.experis.com.case2020.lagalt.models.CommonResponse;
import se.experis.com.case2020.lagalt.services.AuthService;
import se.experis.com.case2020.lagalt.services.MockAuthService;
import se.experis.com.case2020.lagalt.utils.RequestLimiter;

@RestController
@RequestMapping(value = "/api/v1/")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private RequestLimiter requestLimiter;

    @GetMapping("/loggedInUser")
    public ResponseEntity<String> loggedInUser(@RequestHeader(required = false) String Authorization) {
        return new ResponseEntity<>(authService.getUsernameFromToken(Authorization), HttpStatus.OK);
    }

    @GetMapping("/isUsernameAvailable/{username}")
    public ResponseEntity<Boolean> isUsernameAvailable(@PathVariable String username) {
        return new ResponseEntity<>(authService.getUserNameAvailability(username).is2xxSuccessful(), HttpStatus.OK);
    }

    @GetMapping("/signin")
    public ResponseEntity<CommonResponse> signin(@RequestHeader String Authorization, HttpServletRequest request) {
        if (!requestLimiter.isRequestBlocked(request)) {
            return requestLimiter.filter(request, authService.signIn(request, Authorization));
        }
        return requestLimiter.getBlockedResponse();
    }

    @PostMapping("/signup")
    public ResponseEntity<CommonResponse> signup(@RequestHeader String Authorization, @RequestBody ObjectNode user,
            HttpServletRequest request) {
        if (!requestLimiter.isRequestBlocked(request)) {
            String username = user.get("username").asText();
            return requestLimiter.filter(request, authService.signUp(Authorization, username));
        }
        return requestLimiter.getBlockedResponse();
    }

    @GetMapping("/logout")
    public ResponseEntity<CommonResponse> logout(@RequestHeader String Authorization, HttpServletRequest request) {
        if (!requestLimiter.isRequestBlocked(request)) {
            return requestLimiter.filter(request, authService.signOut(Authorization));
        }
        return requestLimiter.getBlockedResponse();
    }
}
