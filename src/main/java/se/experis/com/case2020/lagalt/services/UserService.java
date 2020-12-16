package se.experis.com.case2020.lagalt.services;

import java.util.*;
import java.util.concurrent.ExecutionException;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.EnumUtils;
import se.experis.com.case2020.lagalt.models.CommonResponse;
import se.experis.com.case2020.lagalt.models.enums.Tag;
import se.experis.com.case2020.lagalt.models.user.UserPrivate;
import se.experis.com.case2020.lagalt.models.user.UserPublic;
import se.experis.com.case2020.lagalt.utils.Command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;

@Service
public class UserService {

    MockAuthService authService = new MockAuthService();
    public ResponseEntity<CommonResponse> getPrivateUserDetails(HttpServletRequest request, HttpServletResponse response, String Authorization) throws ExecutionException, InterruptedException {
        Command cmd = new Command(request);
        CommonResponse cr = new CommonResponse();
        HttpStatus resp;

        Firestore dbFirestore = FirestoreClient.getFirestore();
        String userId = authService.getUserId(Authorization);
        if(userId != null) {
            DocumentReference docRef = dbFirestore.collection("users").document(userId);
            DocumentSnapshot document = docRef.get().get();
            
            Map<String, Set<String>> userInfo = new HashMap<>();
            UserPrivate user = null;
            
            if (document.exists()) {
                user = document.toObject(UserPrivate.class);
                
                Iterable<CollectionReference> categories = docRef.listCollections();
                categories.forEach(collection -> {
                    
                    Iterable<DocumentReference> documentIds = collection.listDocuments();
                    documentIds.forEach(id -> {
                        userInfo.computeIfAbsent(collection.getId(), k -> new HashSet<>()).add(id.getId());
                    });
                });
                Set<String> applications = new HashSet<>();
                userInfo.get("appliedTo").forEach(application -> {
                    try {
                        applications.add(dbFirestore.collection("applications").document(application).get().get().get("projectId").toString());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                });
                user.setAppliedTo(userInfo.get("appliedTo"));
                user.setContributedTo(userInfo.get("contributedTo"));
                user.setFollowing(userInfo.get("following"));
                user.setMemberOf(userInfo.get("memberOf"));
                user.setSkills(userInfo.get("skills"));
                
                cr.message = "Profile user details for: " + user.getUserId();
                cr.data = user;
                resp = HttpStatus.OK;
                response.addHeader("Location", "/profile/" + user.getUserId());
            } else {
                cr.message = "User not found";
                resp = HttpStatus.NOT_FOUND;
            }

        } else {
            resp = HttpStatus.UNAUTHORIZED;
            cr.message = "You are not authorized to see private details for this user";
        }
        cmd.setResult(resp);
        return new ResponseEntity<>(cr, resp);
    }
    
    public ResponseEntity<CommonResponse> getPublicUserDetails(HttpServletRequest request, HttpServletResponse response, String username) throws ExecutionException, InterruptedException {
        Command cmd = new Command(request);
        CommonResponse cr = new CommonResponse();
        HttpStatus resp;

        UserPublic user = getUserPublic(username);

        if (user != null) {

            cr.message = "Profile user details for: " + username;
            resp = HttpStatus.OK;
            response.addHeader("Location", "/users/" + username);
        } else {
            cr.message = "No User with Id " + username + " Found";
            resp = HttpStatus.NOT_FOUND;
        }
        cr.data = user;
        cmd.setResult(resp);
        return new ResponseEntity<>(cr, resp);
    }

    public ResponseEntity<CommonResponse> updateUserDetails(HttpServletRequest request, HttpServletResponse response, UserPrivate user, String Authorization) throws ExecutionException, InterruptedException {
        Command cmd = new Command(request);
        CommonResponse cr = new CommonResponse();
        HttpStatus resp;

        Firestore dbFirestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = dbFirestore.collection("users").document(authService.getUserId(Authorization));
        DocumentSnapshot document = documentReference.get().get();

        if (document.exists()) {
            if (authService.belongsToUser(authService.getUserId(Authorization), Authorization)) {
                if (user.getSkills() != null) {
                    DatabaseService databaseService = new DatabaseService();
                    databaseService.emptyCollection(documentReference.collection("skills"), 10);
                    user.getSkills().forEach(skill -> {
                        if (EnumUtils.isValidEnum(Tag.class, skill)) {
                            addToUserDb(user.getUserId(), "skills", skill);
                        }
                    });
                    user.setSkills(null);
                }

                Firestore dbFireStore = FirestoreClient.getFirestore();
                ApiFuture<WriteResult> collectionApiFuture = dbFireStore.collection("users").document(user.getUserId()).set(user);

                cr.data = collectionApiFuture.get().getUpdateTime().toString();
                cr.message = "User data successfully updated";
                resp = HttpStatus.OK;
                response.addHeader("Location", "/profile/" + user.getUsername());
            } else {
                resp = HttpStatus.UNAUTHORIZED;
                cr.message = "You are not authorized to edit user " + user.getUserId();
            }
        } else {
            resp = HttpStatus.NOT_FOUND;
            cr.message = "User not found";
        }
        cmd.setResult(resp);
        return new ResponseEntity<>(cr, resp);
    }

    public void addToUserDb(String userId, String category, String documentId) {

        Firestore dbFirestore = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> collectionApiFuture = dbFirestore.collection("users").document(userId)
                .collection(category).document(documentId).set(new HashMap<String, Object>());
    }

    public void deleteFromUserDb(String userId, String category, String documentId) throws ExecutionException, InterruptedException {

        Firestore dbFirestore = FirestoreClient.getFirestore();
        DocumentReference documents = dbFirestore.collection("users").document(userId).collection(category).document(documentId);
        DocumentSnapshot document = documents.get().get();

        if (document.exists()) {
            Firestore dbFireStore = FirestoreClient.getFirestore();
            ApiFuture<WriteResult> writeResult = dbFireStore.collection("users").document(userId).collection(category)
                    .document(documentId).delete();
        }
    }

    public UserPublic getUserPublic(String userId) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = dbFirestore.collection("users").document(userId);
        DocumentSnapshot document = documentReference.get().get();

        UserPublic user = null;

        if (document.exists()) {
            user = document.toObject(UserPublic.class);
            CollectionReference collectionReference = dbFirestore.collection("users").document(userId).collection("skills");
            Set<String> skillSet = new HashSet<>();
            Iterable<DocumentReference> skills = collectionReference.listDocuments();
            skills.forEach(skill -> {
                skillSet.add(skill.getId());
            });
            user.setSkills(skillSet);
        }
        return user;
    }
}