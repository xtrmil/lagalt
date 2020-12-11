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

    public String saveUserDetails(UserPrivate user) throws ExecutionException, InterruptedException {
        Firestore dbFireStore = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> collectionApiFuture = dbFireStore.collection("users").document(user.getUserId())
                .set(user);
        return collectionApiFuture.get().getUpdateTime().toString();
    }


    public ResponseEntity<CommonResponse> getExtendedUserDetails(HttpServletRequest request, HttpServletResponse response, String userId) throws ExecutionException, InterruptedException {
        Command cmd = new Command(request);
        CommonResponse cr = new CommonResponse();
        HttpStatus resp;


        Firestore dbFirestore = FirestoreClient.getFirestore();
        DocumentReference documents = dbFirestore.collection("users").document(userId);
        ApiFuture<DocumentSnapshot> future = documents.get();
        DocumentSnapshot document = future.get();

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

    public ResponseEntity<CommonResponse> getUserDetails(HttpServletRequest request, HttpServletResponse response, String userId) throws ExecutionException, InterruptedException {
        Command cmd = new Command(request);
        CommonResponse cr = new CommonResponse();
        HttpStatus resp;

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

                deleteCollection(dbFirestore.collection("users").document(user.getUserId()).collection("skills"), 10);
                user.getSkills().forEach(skill -> {
                    if (EnumUtils.isValidEnum(Tag.class, skill)) {
                        addToUser(user.getUserId(), "skills", skill);
                    }
                });
                user.setSkills(null);
            }

            Firestore dbFireStore = FirestoreClient.getFirestore();
            ApiFuture<WriteResult> collectionApiFuture = dbFireStore.collection("users").document(user.getUserId()).set(user);
            cr.data = collectionApiFuture.get().getUpdateTime().toString();
            cr.message = "Userdata successfully updated for user: " + user.getUserId();
            resp = HttpStatus.OK;
            response.addHeader("Location", "/profile/" + user.getUserId());

        } else {
            resp = HttpStatus.NOT_FOUND;
            cr.message = "No User with Id " + user.getUserId() + " Found";
        }
        cmd.setResult(resp);
        return new ResponseEntity<>(cr, resp);
    }

    public void addToUser(String userId, String category, String documentId) {

        Firestore dbFirestore = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> collectionApiFuture = dbFirestore.collection("users").document(userId)
                .collection(category).document(documentId).set(new HashMap<String, Object>());
    }

    public void deleteFromUser(String userId, String category, String documentId) throws ExecutionException, InterruptedException {

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

    void deleteCollection(CollectionReference collection, int batchSize) {
        try {
            ApiFuture<QuerySnapshot> future = collection.limit(batchSize).get();
            int deleted = 0;
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (QueryDocumentSnapshot document : documents) {
                document.getReference().delete();
                ++deleted;
            }
            if (deleted >= batchSize) {
                deleteCollection(collection, batchSize);
            }
        } catch (Exception e) {
            System.err.println("Error deleting collection : " + e.getMessage());
        }
    }

}
