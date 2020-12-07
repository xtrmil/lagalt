package se.experis.com.case2020.lagalt.services;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.google.gson.JsonObject;
import org.springframework.stereotype.Service;

import se.experis.com.case2020.lagalt.models.User;

import java.util.*;
import java.util.concurrent.ExecutionException;

@Service
public class UserService {

    public String saveUserDetails(User user) throws ExecutionException, InterruptedException {
        Firestore dbFireStore = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> collectionApiFuture = dbFireStore.collection("users").document(user.getUserId()).set(user);
        return collectionApiFuture.get().getUpdateTime().toString();
    }

    public String addToUser(String userId, String category, String projectId) throws ExecutionException, InterruptedException {
        Firestore dbFireStore = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> collectionApiFuture = dbFireStore.collection("users").document(userId).collection(category).document(projectId).set(new HashMap<String, Object>());
        return collectionApiFuture.get().getUpdateTime().toString();
    }

    public String deleteFromUser(String userId, String category, String projectId) {
        Firestore dbFireStore = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> writeResult = dbFireStore.collection("users").document(userId).collection(category).document(projectId).delete();
        return "ID " + projectId + " Has been deleted from document: " + category;
    }

    public User getUserDetails(String userId) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        DocumentReference documents = dbFirestore.collection("users").document(userId);

        ApiFuture<DocumentSnapshot> future = documents.get();

        Map<String, List<String>> userInfo = new HashMap<>();
        DocumentSnapshot document = future.get();
        User user = null;

        if (document.exists()) {
            user = document.toObject(User.class);

        Iterable<CollectionReference> categories = documents.listCollections();
            categories.forEach(collection -> {

            Iterable<DocumentReference> projectIds = collection.listDocuments();
                projectIds.forEach(ids -> {
                    userInfo.computeIfAbsent(collection.getId(), k -> new ArrayList<>()).add(ids.getId());
            });
        });

            user.setAppliedTo(userInfo.get("appliedTo"));
            user.setContributedTo(userInfo.get("contributedTo"));
            user.setFollowing(userInfo.get("following"));
            user.setMemberOf(userInfo.get("memberOf"));
        }
        return (user);
    }


//    public User getUserDetails(String userId) throws ExecutionException, InterruptedException {
//        Firestore dbFirestore = FirestoreClient.getFirestore();
//        DocumentReference documentReference = dbFirestore.collection("users").document(userId);
//        ApiFuture<DocumentSnapshot> future = documentReference.get();
//
//        DocumentSnapshot document = future.get();
//        User user = null;
//
//        if(document.exists()){
//            user = document.toObject(User.class);
//            return user;
//        }else{
//            return null;
//        }
//    }

    public String updateUserDetails(User user) throws ExecutionException, InterruptedException {
        Firestore dbFireStore = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> collectionApiFuture = dbFireStore.collection("users").document(user.getUserId()).set(user);
        return collectionApiFuture.get().getUpdateTime().toString();
    }

    public String deleteUser(String userId) {
        Firestore dbFireStore = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> writeResult = dbFireStore.collection("users").document(userId).delete();
        return "Document with ID " + userId + " Has been deleted";
    }
}
