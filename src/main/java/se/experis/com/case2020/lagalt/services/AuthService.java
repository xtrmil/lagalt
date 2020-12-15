package se.experis.com.case2020.lagalt.services;

import java.util.HashMap;

import com.google.api.Http;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.cloud.FirestoreClient;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import se.experis.com.case2020.lagalt.models.CommonResponse;
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
                return ref.exists();
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
            System.err.println("getUserId: " + e.getMessage());
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
        } catch (IllegalArgumentException | FirebaseAuthException e) {
            System.err.println("getUsername: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ResponseEntity<CommonResponse> addUserRecord(String username, String jwtToken) {
        var cr = new CommonResponse();

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
                cr.message = "There is already an account tied to this email";
                return new ResponseEntity<>(cr, HttpStatus.FORBIDDEN);

            } else {
                var usernameAvailabilityStatus = getUserNameAvailability(username);
                if (!usernameAvailabilityStatus.is2xxSuccessful()) {
                    cr.message = "That username is not available";
                    return new ResponseEntity<>(cr, usernameAvailabilityStatus);
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

                cr.data = username;
                return new ResponseEntity<>(cr, HttpStatus.CREATED);
            }

        } catch (IllegalArgumentException e) {
            // invalid token
            cr.message = "Error: You are not authenticated";
            return new ResponseEntity<>(cr, HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            e.printStackTrace();
            cr.message = "An error occured on the server";
            return new ResponseEntity<>(cr, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<CommonResponse> signOut(String jwtToken) {
        var cr = new CommonResponse();
        try {
            var auth = FirebaseAuth.getInstance();
            var foundToken = auth.verifyIdToken(jwtToken, true);
            auth.revokeRefreshTokens(foundToken.getUid());
            cr.message = "You have been signed out";
            return new ResponseEntity<>(cr, HttpStatus.OK);

        } catch(IllegalArgumentException | FirebaseAuthException e) {
            System.err.println("signOut: " + e.getMessage());
            cr.message = "Error: You are not authenticated";
            return new ResponseEntity<>(cr, HttpStatus.UNAUTHORIZED);
        
        } catch(Exception e) {
            e.printStackTrace();
            cr.message = "Could not sign out; an error occured on the server";
            return new ResponseEntity<>(cr, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}