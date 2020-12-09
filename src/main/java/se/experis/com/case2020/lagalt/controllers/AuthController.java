package se.experis.com.case2020.lagalt.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.cloud.FirestoreClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import se.experis.com.case2020.lagalt.models.user.UserProfile;
import se.experis.com.case2020.lagalt.services.AuthService;
import se.experis.com.case2020.lagalt.services.UserService;

@RestController
@RequestMapping(value = "/api/v1/")
public class AuthController {

    private final String allowedHost = "http://localhost:3000"; // temp
    private final String usernameRules = "";

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @CrossOrigin(origins = allowedHost)
    @GetMapping("/loggedInUser")
    public String loggedInUser(@RequestHeader String Authorization) {
        try {
            var fbToken = FirebaseAuth.getInstance().verifyIdToken(Authorization);

            var db = FirestoreClient.getFirestore();
            var user = db.collection("userRecords").document(fbToken.getUid()).get().get();
            if(user.exists()) {
                System.out.println("/loggedInUser: found user " + user.get("userId"));
               return (String) user.get("userId");
            } else {
                System.out.println("/loggedInUser: user not found");
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        return null;
    }

    @CrossOrigin(origins = allowedHost)
    @PostMapping("/auth")
    public ResponseEntity<String> auth(@RequestHeader String Authorization, @RequestBody ObjectNode body) {
        try {
            var auth = FirebaseAuth.getInstance();
            var foundToken = auth.verifyIdToken(Authorization, true);
            
            var db = FirestoreClient.getFirestore();
            var userRecordDocument = db.collection("userRecords").document(foundToken.getUid());
            var existingUser = userRecordDocument.get().get();
            String userId = null;

            if(!existingUser.exists()) {
                userId = body.get("userId").asText().trim();
                var authUser = auth.getUser(foundToken.getUid());
                // new user
                if(!isValidUsername(userId)) {
                    return new ResponseEntity<>("Invalid user name. " + usernameRules, HttpStatus.BAD_REQUEST);
                }

                userRecordDocument.set(body);
                UserProfile userProfile = new UserProfile();
                userProfile.setEmail(authUser.getEmail());
                userProfile.setName(authUser.getDisplayName());
                userProfile.setUserId(userId);
                userService.saveUserDetails(userProfile);
                System.out.println("/auth: New db user created");
            } else {
                // existing user
                userId = existingUser.get("userId").toString();
                System.out.println("/auth: No user created. User already exists");
                System.out.println("/auth: " + existingUser.getData());
            }
            return new ResponseEntity<>(userId, HttpStatus.OK);

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @CrossOrigin(origins = allowedHost)
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader String Authorization) {
        try {
            var auth = FirebaseAuth.getInstance();
            var foundToken = auth.verifyIdToken(Authorization, true);
            
            if(foundToken != null) {
                var foundUser = auth.getUser(foundToken.getUid());
                if(foundUser != null) {
                    auth.revokeRefreshTokens(foundUser.getUid());
                }
            }
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    private boolean isValidUsername(String username) {
        return username != null && !username.isBlank();
    }
}
