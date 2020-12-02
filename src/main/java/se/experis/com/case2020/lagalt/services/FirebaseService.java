package se.experis.com.case2020.lagalt.services;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;
import se.experis.com.case2020.lagalt.models.TEST_Person;

import java.util.concurrent.ExecutionException;

@Service
public class FirebaseService {

    public String saveUserDetails(TEST_Person person) throws ExecutionException, InterruptedException {
        Firestore dbFireStore = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> collectionApiFuture = dbFireStore.collection("users").document(person.getName()).set(person);
        return collectionApiFuture.get().getUpdateTime().toString();
    }

    public TEST_Person getUserDetails(String name) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = dbFirestore.collection("users").document(name);
        ApiFuture<DocumentSnapshot> future = documentReference.get();

        DocumentSnapshot document = future.get();

        TEST_Person person = null;

        if(document.exists()){
            person = document.toObject(TEST_Person.class);
            return person;
        }else{
            return null;
        }
    }

    public String updateUserDetails(TEST_Person person) throws ExecutionException, InterruptedException {
        Firestore dbFireStore = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> collectionApiFuture = dbFireStore.collection("users").document(person.getName()).set(person);
        return collectionApiFuture.get().getUpdateTime().toString();
    }

    public String deleteUser(String name){
        Firestore dbFireStore = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> writeResult = dbFireStore.collection("users").document(name).delete();
        return "Document with ID " + name + " Has been deleted";
    }
}
