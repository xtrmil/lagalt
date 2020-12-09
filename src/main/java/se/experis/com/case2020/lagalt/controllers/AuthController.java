package se.experis.com.case2020.lagalt.controllers;

import java.util.HashMap;
import java.util.Map;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.cloud.FirestoreClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import se.experis.com.case2020.lagalt.services.AuthService;

@RestController
public class AuthController {

    private final String host = "http://localhost:3000"; // temp
    private final String usernameRules = "";

    @Autowired
    private AuthService authService;

    @CrossOrigin(origins = host)
    @GetMapping("/test/{userId}")
    public Boolean test(@PathVariable String userId, @RequestHeader String Authorization) {
        return authService.belongsToUser(userId, Authorization);
    }


    @CrossOrigin(origins = host)
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

    @CrossOrigin(origins = host)
    @PostMapping("/auth")
    public ResponseEntity<String> auth(@RequestHeader String Authorization, @RequestBody UserRecord userRecord) {
        try {
            var auth = FirebaseAuth.getInstance();
            var foundToken = auth.verifyIdToken(Authorization, true);
            
            if(foundToken != null) {
                var db = FirestoreClient.getFirestore();
                var userRecordDocument = db.collection("userRecords").document(foundToken.getUid());
                var existingUser = userRecordDocument.get().get();
                if(!existingUser.exists()) {
                    userRecord.userId = userRecord.userId.trim();
                    var authUser = auth.getUser(foundToken.getUid());
                    // new user
                    if(!isValidUsername(userRecord.userId)) {
                        return new ResponseEntity<>("Invalid user name. " + usernameRules, HttpStatus.BAD_REQUEST);
                    }

                    userRecordDocument.set(userRecord);
                    Map<String, Object> testUser = new HashMap<>();
                    testUser.put("email", authUser.getEmail());
                    testUser.put("userId", userRecord.userId);
                    testUser.put("name", authUser.getDisplayName());
                    db.collection("users").document(userRecord.userId).set(testUser);
                    System.out.println("/auth: New db user created");
                } else {
                    // existing user
                    System.out.println("/auth: No user created. User already exists");
                    System.out.println("/auth: " + existingUser.getData());
                }
            }
            return new ResponseEntity<>("Authenticated ok", HttpStatus.OK);

        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @CrossOrigin(origins = host)
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody String token) {
        try {
            var auth = FirebaseAuth.getInstance();
            var foundToken = auth.verifyIdToken(token, true);
            
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

// @Data
class UserRecord {
    public String userId;
}