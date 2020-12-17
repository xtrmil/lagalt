package se.experis.com.case2020.lagalt.services;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;

import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import se.experis.com.case2020.lagalt.models.CommonResponse;
import se.experis.com.case2020.lagalt.models.enums.Tag;
import se.experis.com.case2020.lagalt.models.user.UserProfileView;
import se.experis.com.case2020.lagalt.models.user.UserPublicView;
import se.experis.com.case2020.lagalt.utils.Command;

@Service
public class UserService {

    @Autowired
    MockAuthService authService;

    public ResponseEntity<CommonResponse> getUserProfile(HttpServletRequest request, String Authorization) {
        Command cmd = new Command(request);
        CommonResponse cr = new CommonResponse();
        HttpStatus resp;

        try {
            Firestore db = FirestoreClient.getFirestore();
            String userId = authService.getUserIdFromToken(Authorization);

            if(userId != null) {
                DocumentReference userDocRef = db.collection("users").document(userId);
                DocumentSnapshot document = userDocRef.get().get();
                
                Map<String, Set<String>> userInfo = new HashMap<>();
                UserProfileView user = null;
                
                if (document.exists()) {
                    user = document.toObject(UserProfileView.class);
                    user.setUser(authService.getUsernameFromToken(Authorization));
                    
                    userDocRef.listCollections().forEach(collection -> {
                        
                        collection.listDocuments().forEach(doc -> {
                            userInfo.computeIfAbsent(collection.getId(), k -> new HashSet<>()).add(doc.getId());
                        });
                    });
                    Set<String> applications = new HashSet<>();
                    if(userInfo.get("appliedTo") != null) {

                        userInfo.get("appliedTo").forEach(application -> {
                            try {
                                applications.add(db.collection("applications").document(application).get().get().get("projectId").toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                        user.setAppliedTo(userInfo.get("appliedTo"));
                    }
                    user.setContributedTo(userInfo.get("contributedTo"));
                    user.setMemberOf(userInfo.get("memberOf"));
                    
                    cr.message = "Profile user details for: " + user.getUser();
                    cr.data = user;
                    resp = HttpStatus.OK;
                } else {
                    cr.message = "User not found";
                    resp = HttpStatus.NOT_FOUND;
                }

            } else {
                resp = HttpStatus.UNAUTHORIZED;
                cr.message = "You are not authorized to see private details for this user";
            }
        } catch(Exception e ) {
            resp = HttpStatus.INTERNAL_SERVER_ERROR;
            e.printStackTrace();
        }
        cmd.setResult(resp);
        return new ResponseEntity<>(cr, resp);
    }
    
    public ResponseEntity<CommonResponse> getPublicUserDetails(HttpServletRequest request, String username) throws ExecutionException, InterruptedException {
        Command cmd = new Command(request);
        CommonResponse cr = new CommonResponse();
        HttpStatus resp;

        UserPublicView user = getUserPublic(username);

        if (user != null) {

            cr.message = "Profile user details for: " + username;
            resp = HttpStatus.OK;
        } else {
            cr.message = "No User with Id " + username + " Found";
            resp = HttpStatus.NOT_FOUND;
        }
        cr.data = user;
        cmd.setResult(resp);
        return new ResponseEntity<>(cr, resp);
    }

    public ResponseEntity<CommonResponse> updateUserDetails(HttpServletRequest request, HttpServletResponse response, UserProfileView user, String Authorization) throws ExecutionException, InterruptedException {
        Command cmd = new Command(request);
        CommonResponse cr = new CommonResponse();
        HttpStatus resp;

        Firestore dbFirestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = dbFirestore.collection("users").document(authService.getUserIdFromToken(Authorization));
        DocumentSnapshot document = documentReference.get().get();

        if (document.exists()) {
            if (authService.belongsToUser(authService.getUserIdFromToken(Authorization), Authorization)) {
                if (user.getSkillKeys() != null) {
                    DatabaseService databaseService = new DatabaseService();
                    databaseService.emptyCollection(documentReference.collection("skills"), 10);
                    user.getSkillKeys().forEach(skill -> {
                        if (EnumUtils.isValidEnum(Tag.class, skill)) {
                            addToUserDocument(user.getUser(), "skills", skill);
                        }
                    });
                    user.setSkillKeys(null);
                }

                Firestore dbFireStore = FirestoreClient.getFirestore();
                ApiFuture<WriteResult> collectionApiFuture = dbFireStore.collection("users").document(user.getUser()).set(user);

                cr.data = collectionApiFuture.get().getUpdateTime().toString();
                cr.message = "User data successfully updated";
                resp = HttpStatus.OK;
            } else {
                resp = HttpStatus.UNAUTHORIZED;
                cr.message = "You are not authorized to edit user " + user.getUser();
            }
        } else {
            resp = HttpStatus.NOT_FOUND;
            cr.message = "User not found";
        }
        cmd.setResult(resp);
        return new ResponseEntity<>(cr, resp);
    }

    public void addToUserDocument(String userId, String category, String documentId) {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        dbFirestore.collection("users").document(userId).collection(category).document(documentId).set(new HashMap<String, Object>());
    }

    // public void deleteFromUserDb(String userId, String category, String documentId) throws ExecutionException, InterruptedException {

    //     Firestore dbFirestore = FirestoreClient.getFirestore();
    //     DocumentReference documents = dbFirestore.collection("users").document(userId).collection(category).document(documentId);
    //     DocumentSnapshot document = documents.get().get();

    //     if (document.exists()) {
    //         Firestore dbFireStore = FirestoreClient.getFirestore();
    //         dbFireStore.collection("users").document(userId).collection(category).document(documentId).delete();
    //     }
    // }

    public UserPublicView getUserPublic(String userId) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = dbFirestore.collection("users").document(userId);
        DocumentSnapshot document = documentReference.get().get();

        UserPublicView user = null;

        if (document.exists()) {
            Map<String, String> tagsMap = new HashMap<>();
            user = document.toObject(UserPublicView.class);
            CollectionReference collectionReference = dbFirestore.collection("users").document(userId).collection("skills");
            collectionReference.listDocuments().forEach(tag -> {
                tagsMap.put(tag.getId(), Tag.valueOf(tag.getId().toString()).DISPLAY_TAG);
            });
            user.setSkills(tagsMap);

        }
        return user;
    }
}