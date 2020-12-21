package se.experis.com.case2020.lagalt.services;

import java.util.ArrayList;
import java.util.List;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;

import org.springframework.stereotype.Service;

@Service
public class DatabaseService {

    public void emptyCollection_old(CollectionReference collection, int batchSize) {
        try {
            ApiFuture<QuerySnapshot> future = collection.limit(batchSize).get();
            int deleted = 0;
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (QueryDocumentSnapshot document : documents) {
                document.getReference().delete();
                ++deleted;
            }
            if (deleted >= batchSize) {
                emptyCollection_old(collection, batchSize);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<ApiFuture<WriteResult>> emptyCollection(CollectionReference collection) {
        List<ApiFuture<WriteResult>> futures = new ArrayList<>();
        try {
            collection.listDocuments().forEach(document -> futures.add(document.delete()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return futures;
    }

}
