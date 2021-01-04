package se.experis.com.case2020.lagalt.controllers;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import se.experis.com.case2020.lagalt.models.CommonResponse;
import se.experis.com.case2020.lagalt.services.AuthService;
import se.experis.com.case2020.lagalt.utils.RequestLimiter;

@RestController
@RequestMapping(value = "/api/v1/")
public class AuthController {

    private final String allowedHost = "http://localhost:3000"; // temp
    private final String usernameRules = "A username must have between 3 and 20 characters and contain letters (a-z) and numbers. Underline is also permitted";

    @Autowired
    private AuthService authService;

    @Autowired
    private RequestLimiter requestLimiter;

    @CrossOrigin(origins = allowedHost)
    @GetMapping("/loggedInUser")
    public ResponseEntity<String> loggedInUser(@RequestHeader(required = false) String Authorization) {
        return new ResponseEntity<>(authService.getUsernameFromToken(Authorization), HttpStatus.OK);
    }

    @CrossOrigin(origins = allowedHost)
    @GetMapping("/isUsernameAvailable/{username}")
    public ResponseEntity<Boolean> isUserIdAvailable(@PathVariable String username) {
        HttpStatus status = authService.getUserNameAvailability(username);
        if (status == HttpStatus.INTERNAL_SERVER_ERROR) {
            return new ResponseEntity<>(status);
        }
        return new ResponseEntity<>(status == HttpStatus.OK, HttpStatus.OK);
    }

    @CrossOrigin(origins = allowedHost)
    @GetMapping("/signin")
    public ResponseEntity<CommonResponse> signin(@RequestHeader String Authorization, HttpServletRequest request) {
        if (!requestLimiter.isRequestBlocked(request)) {
            var cr = new CommonResponse();
            var username = authService.getUsernameFromToken(Authorization);
            if (username == null) {
                requestLimiter.addCustomFailedAttempt(request);
                cr.message = "User does not exist";
                return new ResponseEntity<>(cr, HttpStatus.UNAUTHORIZED);
            } else {
                cr.data = username;
                return new ResponseEntity<>(cr, HttpStatus.OK);
            }
        }
        return requestLimiter.getBlockedResponse();
    }

    @CrossOrigin(origins = allowedHost)
    @PostMapping("/signup")
    public ResponseEntity<CommonResponse> signup(@RequestHeader String Authorization, @RequestBody ObjectNode user,
            HttpServletRequest request) {
        if (!requestLimiter.isRequestBlocked(request)) {
            String username = user.get("username").asText();
            if (!isValidUsername(username)) {
                requestLimiter.addCustomFailedAttempt(request);
                var cr = new CommonResponse();
                cr.message = "Invalid user name. " + usernameRules;
                return new ResponseEntity<>(cr, HttpStatus.BAD_REQUEST);
            }

            return requestLimiter.filter(request, authService.addUserRecord(username.trim(), Authorization));
        }
        return requestLimiter.getBlockedResponse();
    }

    @CrossOrigin(origins = allowedHost)
    @GetMapping("/logout")
    public ResponseEntity<CommonResponse> logout(@RequestHeader String Authorization, HttpServletRequest request) {
        if (!requestLimiter.isRequestBlocked(request)) {
            var response = authService.signOut(Authorization);
            if (response.getStatusCode().is4xxClientError()) {
                requestLimiter.addCustomFailedAttempt(request);
            }
            return response;
        }
        return requestLimiter.getBlockedResponse();
    }

    // public for unit test
    public boolean isValidUsername(String username) {
        String regex = "[_0-9a-zA-Z]{3,20}";
        return username.matches(regex);
    }
}
