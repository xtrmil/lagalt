package se.experis.com.case2020.lagalt.services;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.cloud.FirestoreClient;

import org.springframework.stereotype.Service;

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



}
