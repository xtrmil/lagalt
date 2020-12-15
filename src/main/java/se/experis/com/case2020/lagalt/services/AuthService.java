package se.experis.com.case2020.lagalt.services;

import java.util.HashMap;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.cloud.FirestoreClient;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import se.experis.com.case2020.lagalt.models.user.UserPrivate;

@Service
public class AuthService {

    /**
     * Checks whether the user name (user id) is available or not. Used when signing
     * up
     * 
     * @param username
     * @return 200 if available, 409 if username exists, 500 if other error
     */
    public HttpStatus getUserNameAvailability(String username) {
        try {
            Firestore db = FirestoreClient.getFirestore();
            var existingUser = db.collection("userRecords").document(username.toLowerCase()).get().get();
            return existingUser.exists() ? HttpStatus.CONFLICT : HttpStatus.OK;

        } catch (Exception e) {
            e.printStackTrace();
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    public boolean belongsToUser(String username, String jwtToken) {
        try {
            Firestore db = FirestoreClient.getFirestore();
            var fbToken = FirebaseAuth.getInstance().verifyIdToken(jwtToken);
            var user = db.collection("userRecords").document(username.toLowerCase()).get().get();

            if(user.exists()) {
                return user.get("uid").equals(fbToken.getUid());
            }
            return false;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Checks whether a user is member of a particular project
     * 
     * @param projectId ownerId-projectTitle
     * @param jwtToken
     * @return
     */
    public boolean isProjectMember(String projectId, String jwtToken) {
        return isPartOfProjectCollection(projectId, jwtToken, "members");
    }

    /**
     * Checks whether a user is admin of a particular project
     * 
     * @param projectId ownerId-projectTitle
     * @param jwtToken
     * @return
     */
    public boolean isProjectAdmin(String projectId, String jwtToken) {
        return isPartOfProjectCollection(projectId, jwtToken, "admins");
    }

    private boolean isPartOfProjectCollection(String projectId, String jwtToken, String collection) {
        String userId = getUserId(jwtToken);

        if (userId != null) {
            try {
                Firestore db = FirestoreClient.getFirestore();
                var ref = db.collection("projects").document(projectId).collection("admins").document(userId).get().get();
                var ownerId = db.collection("projects").document(projectId).get().get().get("ownerId");
                return ref.exists() && ownerId == userId;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    public String getUserId(String jwtToken) {
        try {
            var fbToken = FirebaseAuth.getInstance().verifyIdToken(jwtToken);
            return fbToken.getUid();
        } catch (IllegalArgumentException | FirebaseAuthException e) {
            System.err.println("getUsername: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getUsername(String jwtToken) {
        try {
            Firestore db = FirestoreClient.getFirestore();
            var fbToken = FirebaseAuth.getInstance().verifyIdToken(jwtToken);
            var user = db.collection("users").document(fbToken.getUid()).get().get();

            if (user.exists()) {
                return user.get("username").toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ResponseEntity<String> addUserRecord(String username, String jwtToken) {
        try {
            var userId = getUserId(jwtToken);
            if(userId == null) {
                throw new IllegalArgumentException();
            }
            
            var db = FirestoreClient.getFirestore();

            var userRef = db.collection("users").document(userId);
            var userDocument = userRef.get().get();

            if (userDocument.exists()) {
                // auth user is alredy tied to a db user
                return new ResponseEntity<>("There is already an account tied to this email", HttpStatus.FORBIDDEN);

            } else {
                var usernameAvailabilityStatus = getUserNameAvailability(username);
                if (!usernameAvailabilityStatus.is2xxSuccessful()) {
                    return new ResponseEntity<>("That username is not available", usernameAvailabilityStatus);
                }
                var auth = FirebaseAuth.getInstance();

                var authUser = auth.getUser(userId);
                var userProfile = new UserPrivate();
                userProfile.setUserId(userId);
                userProfile.setUsername(username);
                userProfile.setEmail(authUser.getEmail());
                userProfile.setName(authUser.getDisplayName());
                
                userRef.set(userProfile);
                var userRecord = new HashMap<String, String>();
                userRecord.put("uid", userId);
                
                // put userRecord that ties username to a uid
                db.collection("userRecords").document(username.toLowerCase()).set(userRecord);

                return new ResponseEntity<>(username, HttpStatus.CREATED);
            }

        } catch (IllegalArgumentException e) {
            // invalid token
            return new ResponseEntity<>("Error: You are not authenticated", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("An error occured on the server", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}