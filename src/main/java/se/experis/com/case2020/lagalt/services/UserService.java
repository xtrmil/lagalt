package se.experis.com.case2020.lagalt.services;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

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

            if (userId != null) {
                DocumentReference userDocRef = getUserDocument(userId);
                DocumentSnapshot document = userDocRef.get().get();

                Map<String, Set<String>> userInfo = new HashMap<>();
                UserProfileView user = null;

                if (document.exists()) {
                    user = document.toObject(UserProfileView.class);

                    userDocRef.listCollections().forEach(collection -> {

                        collection.listDocuments().forEach(doc -> {
                            userInfo.computeIfAbsent(collection.getId(), k -> new HashSet<>()).add(doc.getId());
                        });
                    });
                    Set<String> applications = new HashSet<>();
                    if (userInfo.get("appliedTo") != null) {

                        userInfo.get("appliedTo").forEach(application -> {
                            try {
                                applications.add(db.collection("applications").document(application).get().get()
                                        .get("projectId").toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                        user.setAppliedTo(userInfo.get("appliedTo"));
                    }
                    user.setContributedTo(userInfo.get("contributedTo"));
                    user.setMemberOf(userInfo.get("memberOf"));

                    cr.message = "Profile user details for: " + user.getUsername();
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
        } catch (Exception e) {
            resp = HttpStatus.INTERNAL_SERVER_ERROR;
            e.printStackTrace();
        }
        cmd.setResult(resp);
        return new ResponseEntity<>(cr, resp);
    }

    public ResponseEntity<CommonResponse> getPublicUserDetails(HttpServletRequest request, String username) {
        Command cmd = new Command(request);
        CommonResponse cr = new CommonResponse();
        HttpStatus resp;

        try {
            String userId = authService.getUserId(username);

            if (userId != null) {
                UserPublicView user = getUserPublicObject(userId);

                cr.message = "Profile user details for: " + username;
                cr.data = user;
                resp = HttpStatus.OK;
            } else {
                cr.message = "No user named " + username + " found";
                resp = HttpStatus.NOT_FOUND;
            }
        } catch (Exception e) {
            resp = HttpStatus.INTERNAL_SERVER_ERROR;
            e.printStackTrace();
        }
        cmd.setResult(resp);
        return new ResponseEntity<>(cr, resp);
    }

    public ResponseEntity<CommonResponse> updateUserDetails(HttpServletRequest request, UserProfileView partialUser, String Authorization) {
        Command cmd = new Command(request);
        CommonResponse cr = new CommonResponse();
        HttpStatus resp;

        try {
            String userId = authService.getUserIdFromToken(Authorization);

            if (userId != null) {
                DocumentReference documentReference = getUserDocument(userId);
                var user = getUserProfileobject(userId);

                if (partialUser.getTags() != null) {
                    DatabaseService databaseService = new DatabaseService();
                    databaseService.emptyCollection(documentReference.collection("tags"), 10);

                    // TODO not waiting on emptying the collection can cause problems

                    partialUser.getTags().keySet().forEach(tagKey -> {
                        if (EnumUtils.isValidEnum(Tag.class, tagKey)) {
                            addCollectionToUserDocument(userId, "tags", tagKey);
                        }
                    });
                    partialUser.setTags(null);
                }
                if (partialUser.getDescription() != null) {
                    user.setDescription(partialUser.getDescription());
                }
                if (partialUser.getHidden() == null) {
                    user.setHidden(partialUser.getHidden());
                }
                if (partialUser.getImageURL() == null) {
                    user.setImageURL(partialUser.getImageURL());
                }
                if (partialUser.getPortfolio() == null) {
                    user.setPortfolio(partialUser.getPortfolio());
                }
                if (partialUser.getName() == null) {
                    user.setName(partialUser.getName());
                }

                ApiFuture<WriteResult> collectionApiFuture = getUserDocument(userId).set(user);

                cr.data = collectionApiFuture.get().getUpdateTime().toString();
                cr.message = "User data successfully updated";
                resp = HttpStatus.OK;
            } else {
                cr.message = "You are not authenticated";
                resp = HttpStatus.UNAUTHORIZED;
            }
        } catch (Exception e) {
            resp = HttpStatus.INTERNAL_SERVER_ERROR;
            e.printStackTrace();
        }
        System.out.println("---------");
        cmd.setResult(resp);
        return new ResponseEntity<>(cr, resp);
    }

    public void addCollectionToUserDocument(String userId, String category, String documentId) {
        try {
            getUserDocument(userId).collection(category).document(documentId).create(new HashMap<String, Object>());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteFromUserCollection(String userId, String category, String documentId) {
        try {
            DocumentReference documentReference = getUserDocument(userId).collection(category).document(documentId);
            DocumentSnapshot document = documentReference.get().get();

            if (document.exists()) {
                documentReference.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public UserPublicView getUserPublicObject(String userId) {
        UserPublicView user = null;
        try {
            DocumentReference documentReference = getUserDocument(userId);
            DocumentSnapshot userDocument = documentReference.get().get();

            if (userDocument.exists()) {
                Map<String, String> tagsMap = new HashMap<>();
                user = userDocument.toObject(UserPublicView.class);
                CollectionReference tagsReference = documentReference.collection("tags");
                tagsReference.listDocuments().forEach(tag -> {
                    tagsMap.put(tag.getId(), Tag.valueOf(tag.getId().toString()).DISPLAY_TAG);
                });
                user.setTags(tagsMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ;
        }
        return user;
    }

    private UserProfileView getUserProfileobject(String userId) {
        try {
            return getUserDocument(userId).get().get().toObject(UserProfileView.class);
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private DocumentReference getUserDocument(String userId) {
        var db = FirestoreClient.getFirestore();
        return db.collection("users").document(userId);
    }

}