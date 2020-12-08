package se.experis.com.case2020.lagalt.controllers;

import java.util.HashMap;
import java.util.Map;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.cloud.FirestoreClient;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final String host = "http://localhost:3000"; // temp
    private final String usernameRules = "";

    @CrossOrigin(origins = host)
    @GetMapping("/loggedInUser")
    public String loggedInUser(@RequestHeader String Authorization) {
        try {
            var fbToken = FirebaseAuth.getInstance().verifyIdToken(Authorization);

            var db = FirestoreClient.getFirestore();
            var user = db.collection("userRecords").document(fbToken.getUid()).get().get();
            if(user.exists()) {
                System.out.println("/loggedInUser: found user " + user.get("username"));
               return (String) user.get("username");
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
    public ResponseEntity<String> auth(@RequestBody UserRequest userRequest) { // TODO ändra till @RequestHeader String Authorization, @RequestBody String username
        try {
            var auth = FirebaseAuth.getInstance();
            var foundToken = auth.verifyIdToken(userRequest.token, true);
            
            if(foundToken != null) {
                var db = FirestoreClient.getFirestore();
                var userRecord = db.collection("userRecords").document(foundToken.getUid());
                var existingUser = userRecord.get().get();
                if(!existingUser.exists()) {
                    var authUser = auth.getUser(foundToken.getUid());
                    // new user
                    if(!isValidUsername(userRequest.username)) {
                        System.out.println();
                        return new ResponseEntity<>("Invalid user name. " + usernameRules, HttpStatus.BAD_REQUEST);
                    }

                    userRecord.set(new UserRecord(userRequest.username));
                    Map<String, Object> testUser = new HashMap<>();
                    testUser.put("email", authUser.getEmail());
                    testUser.put("userId", userRequest.username);
                    testUser.put("name", authUser.getDisplayName());
                    db.collection("users").document(userRequest.username).set(testUser);
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

class UserRequest {
    public String token;
    public String username;

    @Override
    public String toString() {
        return token;
    }
}

class UserRecord {
    public String username;

    public UserRecord(String username) {
        this.username = username.trim();
    }
}