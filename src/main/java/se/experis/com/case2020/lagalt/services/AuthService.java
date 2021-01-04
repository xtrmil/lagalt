package se.experis.com.case2020.lagalt.services;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.cloud.FirestoreClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import se.experis.com.case2020.lagalt.models.CommonResponse;
import se.experis.com.case2020.lagalt.models.user.UserProfileView;
import se.experis.com.case2020.lagalt.utils.RequestLimiter;

@Service
public class AuthService {

    private final String usernameRules = "A username must be between 3 and 20 characters long and contain letters (a-z) and numbers. Underline is also permitted";

    @Autowired
    private ProjectService projectService;

    @Autowired
    private RequestLimiter requestLimiter;

    public ResponseEntity<CommonResponse> signIn(HttpServletRequest request, String jwtToken) {
        var cr = new CommonResponse();
        HttpStatus resp;

        var username = getUsernameFromToken(jwtToken);
        if (username != null) {
            cr.data = username;
            cr.message = "Successfully logged in as " + username;
            resp = HttpStatus.OK;
        } else {
            requestLimiter.addCustomFailedAttempt(request);
            cr.message = "Invalid authentication token";
            resp = HttpStatus.UNAUTHORIZED;
        }
        return new ResponseEntity<>(cr, resp);
    }

    public ResponseEntity<CommonResponse> signUp(String jwtToken, String username) {
        var cr = new CommonResponse();
        HttpStatus resp;

        try {
            System.out.println(jwtToken);
            var userId = getUserIdFromToken(jwtToken);
            System.out.println(userId);
            if (userId != null) {
                var db = FirestoreClient.getFirestore();
                var userRef = db.collection("users").document(userId);
                var userDocument = userRef.get().get();

                if (userDocument.exists()) {
                    // auth user is alredy tied to a db user
                    cr.message = "There is already an account tied to this email";
                    resp = HttpStatus.FORBIDDEN;

                } else if (!isValidUsername(username)) {
                    cr.message = "Invalid username. " + usernameRules;
                    resp = HttpStatus.BAD_REQUEST;
                } else {
                    var usernameAvailabilityStatus = getUserNameAvailability(username);
                    if (!usernameAvailabilityStatus.is2xxSuccessful()) {
                        cr.message = "That username is not available";
                        resp = usernameAvailabilityStatus;
                    } else {
                        var auth = FirebaseAuth.getInstance();
                        var authUser = auth.getUser(userId);
                        var userProfile = new UserProfileView();
                        userProfile.setUsername(username);
                        userProfile.setEmail(authUser.getEmail());
                        userProfile.setName(authUser.getDisplayName());

                        userRef.set(userProfile);
                        var userRecord = new HashMap<String, String>();
                        userRecord.put("uid", userId);

                        // put userRecord that ties username to a uid
                        db.collection("userRecords").document(username.toLowerCase()).set(userRecord);

                        cr.data = username;
                        cr.message = "Successfully created user " + username;
                        resp = HttpStatus.CREATED;
                    }
                }
            } else {
                cr.message = "Error: You are not authenticated";
                resp = HttpStatus.UNAUTHORIZED;
            }
        } catch (Exception e) {
            e.printStackTrace();
            cr.message = "An error occured on the server";
            resp = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(cr, resp);
    }

    public ResponseEntity<CommonResponse> signOut(String jwtToken) {
        var cr = new CommonResponse();
        HttpStatus resp;

        try {
            var auth = FirebaseAuth.getInstance();
            var userId = getUserIdFromToken(jwtToken);

            if (userId != null) {
                auth.revokeRefreshTokens(userId);
                cr.message = "You have been signed out";
                resp = HttpStatus.OK;
            } else {
                cr.message = "Error: You are not authenticated";
                resp = HttpStatus.UNAUTHORIZED;
            }

        } catch (Exception e) {
            e.printStackTrace();
            cr.message = "Could not sign out; an error occured on the server";
            resp = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(cr, resp);
    }

    /**
     * Checks whether the user name (user id) is available or not. Used when signing
     * up
     * 
     * @param username
     * @return 200 if available, 409 if username exists, 400 if username is not
     *         valid 500 if other error
     */
    public HttpStatus getUserNameAvailability(String username) {
        try {
            var db = FirestoreClient.getFirestore();
            if (!isValidUsername(username)) {
                return HttpStatus.BAD_REQUEST;
            } else {
                var existingUser = db.collection("userRecords").document(username.toLowerCase()).get().get();
                return existingUser.exists() ? HttpStatus.CONFLICT : HttpStatus.OK;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    /**
     * Checks whether a user is member of a particular project
     * 
     * @param projectId ownerId-projectTitle
     * @param jwtToken
     * @return
     */
    public boolean isProjectMember(String owner, String projectName, String jwtToken) {
        return isPartOfProjectCollection(owner, projectName, jwtToken, "members");
    }

    /**
     * Checks whether a user is admin/owner of a particular project
     * 
     * @param projectId ownerId-projectTitle
     * @param jwtToken
     * @return
     */
    public boolean isProjectAdmin(String owner, String projectName, String jwtToken) {
        return isPartOfProjectCollection(owner, projectName, jwtToken, "admins")
                || isOwner(owner, projectName, jwtToken);
    }

    /**
     * Checks whether a user is admin/owner of a particular project
     */
    public boolean isProjectAdmin(String projectId, String jwtToken) {
        try {
            String userId = getUserIdFromToken(jwtToken);
            var db = FirestoreClient.getFirestore();
            var ownerId = db.collection("projects").document(projectId).get().get().getString("owner");
            var admin = db.collection("projects").document(projectId).collection("admins").document(userId).get().get();
            return admin.exists() || ownerId.equals(userId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isOwner(String owner, String projectName, String jwtToken) {
        try {
            var db = FirestoreClient.getFirestore();
            String projectId = projectService.getProjectId(owner, projectName);
            String userId = getUserIdFromToken(jwtToken);
            var ownerId = db.collection("projects").document(projectId).get().get().getString("owner");
            return ownerId.equals(userId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isPartOfProjectStaff(String owner, String projectName, String jwtToken) {
        return isOwner(owner, projectName, jwtToken) || isProjectAdmin(owner, projectName, jwtToken)
                || isProjectMember(owner, projectName, jwtToken);
    }

    private boolean isPartOfProjectCollection(String owner, String projectName, String jwtToken, String collection) {
        String userId = getUserIdFromToken(jwtToken);

        if (userId != null) {
            try {
                var db = FirestoreClient.getFirestore();
                String projectId = projectService.getProjectId(owner, projectName);
                var ref = db.collection("projects").document(projectId).collection(collection).document(userId).get()
                        .get();
                return ref.exists();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public String getUserIdFromToken(String jwtToken) {
        try {
            var fbToken = FirebaseAuth.getInstance().verifyIdToken(jwtToken);
            return fbToken.getUid();
        } catch (IllegalArgumentException | FirebaseAuthException e) {
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getUserId(String username) {
        try {
            var db = FirestoreClient.getFirestore();
            var userRecord = db.collection("userRecords").document(username.toLowerCase()).get().get();
            if (userRecord.exists()) {
                return userRecord.getString("uid");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getUsernameFromToken(String jwtToken) {
        try {
            var db = FirestoreClient.getFirestore();
            var fbToken = FirebaseAuth.getInstance().verifyIdToken(jwtToken);
            var user = db.collection("users").document(fbToken.getUid()).get().get();

            if (user.exists()) {
                return user.getString("username").toLowerCase();
            }
        } catch (IllegalArgumentException | FirebaseAuthException e) {
            System.err.println("getUsername: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getUsername(String userId) {
        try {
            var db = FirestoreClient.getFirestore();
            var user = db.collection("users").document(userId).get().get();
            if (user.exists()) {
                return user.getString("username").toLowerCase();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // public for unit test
    public boolean isValidUsername(String username) {
        String regex = "[_0-9a-zA-Z]{3,20}";
        return username.matches(regex);
    }
}