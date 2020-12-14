package se.experis.com.case2020.lagalt.services;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.cloud.FirestoreClient;

import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class AuthService {
    
    public boolean belongsToUser(String userId, String jwtToken) {
        try {
            var fbToken = FirebaseAuth.getInstance().verifyIdToken(jwtToken);
            var db = FirestoreClient.getFirestore();
            var user = db.collection("userRecords").document(fbToken.getUid()).get().get();
            return userId.equals(user.get("userId"));
            
        } catch(Exception e) {
            return false;
        }
    }

    public boolean userExistsInDb(Firestore dbFirestore, String userName) {   // move to AuthService
        DocumentReference documentReference = dbFirestore.collection("users").document(userName);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        try {
            DocumentSnapshot document = future.get();
            return document.exists();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }



}
