package se.experis.com.case2020.lagalt.services;

import java.util.*;
import java.util.concurrent.ExecutionException;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;

import org.springframework.stereotype.Service;

import se.experis.com.case2020.lagalt.models.enums.Tag;
import se.experis.com.case2020.lagalt.models.user.UserProfile;
import se.experis.com.case2020.lagalt.models.user.UserPublic;
import org.apache.commons.lang3.EnumUtils;

@Service
public class UserService {

    public String saveUserDetails(UserProfile user) throws ExecutionException, InterruptedException {
        Firestore dbFireStore = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> collectionApiFuture = dbFireStore.collection("users").document(user.getUserId())
                .set(user);
        return collectionApiFuture.get().getUpdateTime().toString();
    }

    public String addToUser(String userId, String category, String projectId) {
        Firestore dbFireStore = FirestoreClient.getFirestore();
        try {
            ApiFuture<WriteResult> collectionApiFuture = dbFireStore.collection("users").document(userId)
                    .collection(category).document(projectId).set(new HashMap<String, Object>());

            return collectionApiFuture.get().getUpdateTime().toString();
        } catch (ExecutionException e) {
            e.printStackTrace();
            return e.toString();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return e.toString();
        }
    }

    public String deleteFromUser(String userId, String category, String projectId) {
        Firestore dbFireStore = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> writeResult = dbFireStore.collection("users").document(userId).collection(category)
                .document(projectId).delete();
        return "ID " + projectId + " Has been deleted from document: " + category;
    }


    public UserProfile getProfileUserDetails(String userId) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        DocumentReference documents = dbFirestore.collection("users").document(userId);
        ApiFuture<DocumentSnapshot> future = documents.get();
        DocumentSnapshot document = future.get();

        Map<String, Set<String>> userInfo = new HashMap<>();
        UserProfile user = null;

        if (document.exists()) {
            user = document.toObject(UserProfile.class);

            Iterable<CollectionReference> categories = documents.listCollections();
            categories.forEach(collection -> {

                Iterable<DocumentReference> projectIds = collection.listDocuments();
                projectIds.forEach(ids -> {
                    userInfo.computeIfAbsent(collection.getId(), k -> new HashSet<>()).add(ids.getId());
                });
            });

            user.setAppliedTo(userInfo.get("appliedTo"));
            user.setContributedTo(userInfo.get("contributedTo"));
            user.setFollowing(userInfo.get("following"));
            user.setMemberOf(userInfo.get("memberOf"));
            user.setSkills(userInfo.get("skills"));
        }
        return (user);
    }

    public UserPublic getPublicUserDetails(String userId) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = dbFirestore.collection("users").document(userId);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot document = future.get();

        UserPublic user;

        if (document.exists()) {
            user = document.toObject(UserPublic.class);
            CollectionReference collectionReference = dbFirestore.collection("users").document(userId).collection("skills");
            Set<String> skillSet = new HashSet<>();
            Iterable<DocumentReference> skills = collectionReference.listDocuments();
            skills.forEach(skill -> {
                skillSet.add(skill.getId());
            });
            user.setSkills(skillSet);
            return user;
        } else {
            return null;
        }
    }

    public String updateUserDetails(UserProfile user) throws ExecutionException, InterruptedException {
        if (user.getSkills() != null) {
            user.getSkills().forEach(skill -> {
                if (EnumUtils.isValidEnum(Tag.class, skill)) {
                    addToUser(user.getUserId(), "skills", skill);
                }
            });
            user.setSkills(null);
        }

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
