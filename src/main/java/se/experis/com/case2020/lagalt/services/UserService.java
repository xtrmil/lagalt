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

    public ResponseEntity<CommonResponse> getPrivateUserDetails(HttpServletRequest request, HttpServletResponse response, String username) throws ExecutionException, InterruptedException {
        Command cmd = new Command(request);
        CommonResponse cr = new CommonResponse();
        HttpStatus resp;

        Firestore dbFirestore = FirestoreClient.getFirestore();
        var userId = dbFirestore.collection("userRecords").document(username).get().get().get("uid").toString();

        DocumentReference documents = dbFirestore.collection("users").document(userId);
        DocumentSnapshot document = documents.get().get();

        Map<String, Set<String>> userInfo = new HashMap<>();
        UserPrivate user = null;

        if (document.exists()) {
            user = document.toObject(UserPrivate.class);

            Iterable<CollectionReference> categories = documents.listCollections();
            categories.forEach(collection -> {

                Iterable<DocumentReference> projectIds = collection.listDocuments();
                projectIds.forEach(id -> {
                    userInfo.computeIfAbsent(collection.getId(), k -> new HashSet<>()).add(id.getId());
                });
            });

            user.setAppliedTo(userInfo.get("appliedTo"));
            user.setContributedTo(userInfo.get("contributedTo"));
            user.setFollowing(userInfo.get("following"));
            user.setMemberOf(userInfo.get("memberOf"));
            user.setSkills(userInfo.get("skills"));

            cr.message = "Profile user details for: " + user.getUserId();
            resp = HttpStatus.OK;
            response.addHeader("Location", "/profile/" + user.getUserId());
        } else {
            cr.message = "No Profile with Id " + userId + " Found";
            resp = HttpStatus.NOT_FOUND;
        }

        cr.data = user;
        cmd.setResult(resp);
        return new ResponseEntity<>(cr, resp);
    }

    public ResponseEntity<CommonResponse> getPublicUserDetails(HttpServletRequest request, HttpServletResponse response, String userId) throws ExecutionException, InterruptedException {
        Command cmd = new Command(request);
        CommonResponse cr = new CommonResponse();
        HttpStatus resp;

        UserPublic user = getUserPublic(userId);

        if (user != null) {

            cr.message = "Profile user details for: " + userId;
            resp = HttpStatus.OK;
            response.addHeader("Location", "/users/" + userId);
        } else {
            cr.message = "No User with Id " + userId + " Found";
            resp = HttpStatus.NOT_FOUND;
        }
        cr.data = user;
        cmd.setResult(resp);
        return new ResponseEntity<>(cr, resp);
    }

    public ResponseEntity<CommonResponse> updateUserDetails(HttpServletRequest request, HttpServletResponse response, UserPrivate user) throws ExecutionException, InterruptedException {
        Command cmd = new Command(request);
        CommonResponse cr = new CommonResponse();
        HttpStatus resp;

        Firestore dbFirestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = dbFirestore.collection("users").document(user.getUserId());
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot document = future.get();
        if (document.exists()) {
            if (user.getSkills() != null) {
                DataBaseService dataBaseService = new DataBaseService();
                dataBaseService.deleteCollection(dbFirestore.collection("users").document(user.getUserId()).collection("skills"), 10);
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
        DocumentReference documents = dbFirestore.collection("users").document(userId).collection(category)
                .document(documentId);
        ApiFuture<DocumentSnapshot> future = documents.get();
        DocumentSnapshot document = future.get();

        if (document.exists()) {
            Firestore dbFireStore = FirestoreClient.getFirestore();
            ApiFuture<WriteResult> writeResult = dbFireStore.collection("users").document(userId).collection(category)
                    .document(documentId).delete();
        }
    }

    public UserPublic getUserPublic(String userId) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = dbFirestore.collection("users").document(userId);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot document = future.get();

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