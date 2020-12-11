package se.experis.com.case2020.lagalt.services;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.cloud.FirestoreClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import se.experis.com.case2020.lagalt.models.user.UserData;
import se.experis.com.case2020.lagalt.models.user.UserProfile;

@Service
public class AuthService {

    @Autowired
    private UserService userService;

    /**
     * Checks whether the user name (user id) is available or not. Used when signing
     * up
     * 
     * @param userId
     * @return 200 if available, 409 if username exists, 500 if other error
     */
    public HttpStatus getUserNameAvailability(String userId) {
        try {
            Firestore db = FirestoreClient.getFirestore();
            var existingUser = db.collection("users").document(userId).get().get();
            return existingUser.exists() ? HttpStatus.CONFLICT : HttpStatus.OK;
        } catch (Exception e) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    public boolean belongsToUser(String userId, String jwtToken) {
        try {
            Firestore db = FirestoreClient.getFirestore();
            var fbToken = FirebaseAuth.getInstance().verifyIdToken(jwtToken);
            var user = db.collection("userRecords").document(fbToken.getUid()).get().get();
            return userId.equals(user.get("userId"));

        } catch (Exception e) {
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
                var ref = db.collection("projects").document(projectId).collection("admins").document(userId).get()
                        .get();
                return ref.exists();
            } catch (Exception e) {
            }
        }
        return false;
    }

    public String getUserId(String jwtToken) {
        try {
            Firestore db = FirestoreClient.getFirestore();
            var fbToken = FirebaseAuth.getInstance().verifyIdToken(jwtToken);
            var user = db.collection("userRecords").document(fbToken.getUid()).get().get();

            if (user.exists()) {
                return user.get("userId").toString();
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    public ResponseEntity<String> addUserRecord(UserData userData, String jwtToken) {
        try {
            var auth = FirebaseAuth.getInstance();
            var firebaseToken = auth.verifyIdToken(jwtToken, true);

            var db = FirestoreClient.getFirestore();

            var userRecordRef = db.collection("userRecords").document(firebaseToken.getUid());
            var userRecord = userRecordRef.get().get();

            if (userRecord.exists()) {
                // auth user is alredy tied to a db user
                return new ResponseEntity<>("There is already an account tied to this email. Try to log in instead",
                        HttpStatus.FORBIDDEN);
            } else {
                var usernameAvailability = getUserNameAvailability(userData.getUserId());
                if (!usernameAvailability.is2xxSuccessful()) {
                    return new ResponseEntity<>("That username is not available", usernameAvailability);
                }

                userRecordRef.set(userData);

                var authUser = auth.getUser(firebaseToken.getUid());
                var userProfile = new UserProfile();
                userProfile.setEmail(authUser.getEmail());
                userProfile.setName(authUser.getDisplayName());
                userProfile.setUserId(userData.getUserId().trim());

                userService.saveUserDetails(userProfile);

                return new ResponseEntity<>(userProfile.getUserId(), HttpStatus.CREATED);
            }

        } catch (IllegalArgumentException | FirebaseAuthException e) {
            // invalid token
            return new ResponseEntity<>("Error: You are not authenticated", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>("An error occured on the server", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
