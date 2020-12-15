package se.experis.com.case2020.lagalt.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.firebase.auth.FirebaseAuth;

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

import se.experis.com.case2020.lagalt.services.AuthService;

@RestController
@RequestMapping(value = "/api/v1/")
public class AuthController {

    private final String allowedHost = "http://localhost:3000"; // temp
    private final String usernameRules = "A username must have between 3 and 20 characters and contain letters (a-z) and numbers. Underline is also permitted";

    @Autowired
    private AuthService authService;

    @CrossOrigin(origins = allowedHost)
    @GetMapping("/loggedInUser")
    public ResponseEntity<String> loggedInUser(@RequestHeader String Authorization) {
        return new ResponseEntity<>(authService.getUsername(Authorization), HttpStatus.OK);
    }

    @CrossOrigin(origins = allowedHost)
    @GetMapping("/isUsernameAvailable/{username}")
    public ResponseEntity<Boolean> isUserIdAvailable(@PathVariable String username) {
        HttpStatus status = authService.getUserNameAvailability(username);
        if (status == HttpStatus.INTERNAL_SERVER_ERROR) {
            return new ResponseEntity<>(status);
        }
        return new ResponseEntity<>(status == HttpStatus.OK ? true : false, HttpStatus.OK);
    }

    @CrossOrigin(origins = allowedHost)
    @GetMapping("/signin")
    public ResponseEntity<String> signin(@RequestHeader String Authorization) {
        var username = authService.getUsername(Authorization);
        if (username == null) {
            return new ResponseEntity<>("User does not exist", HttpStatus.UNAUTHORIZED);
        } else {
            return new ResponseEntity<>(username, HttpStatus.OK);
        }
    }

    @CrossOrigin(origins = allowedHost)
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestHeader String Authorization, @RequestBody ObjectNode user) {
        String username = user.get("username").asText();
        if (!isValidUsername(username)) {
            return new ResponseEntity<>("Invalid user name. " + usernameRules, HttpStatus.BAD_REQUEST);
        }
        
        return authService.addUserRecord(username.trim(), Authorization);
    }

    @CrossOrigin(origins = allowedHost)
    @GetMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader String Authorization) {
        try {
            var auth = FirebaseAuth.getInstance();
            var foundToken = auth.verifyIdToken(Authorization, true);

            if (foundToken != null) {
                var foundUser = auth.getUser(foundToken.getUid());
                if (foundUser != null) {
                    auth.revokeRefreshTokens(foundUser.getUid());
                }
            }
            return new ResponseEntity<>("You have been signed out", HttpStatus.OK);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>("Error: Could not sign out", HttpStatus.BAD_REQUEST);
        }
    }

    // public for unit test
    public boolean isValidUsername(String username) {
        String regex = "[_0-9a-zA-Z]{3,20}";
        return username.matches(regex);
    }
}
