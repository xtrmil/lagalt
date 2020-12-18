package se.experis.com.case2020.lagalt.services;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import se.experis.com.case2020.lagalt.models.CommonResponse;
import se.experis.com.case2020.lagalt.models.MessageBoardPost;
import se.experis.com.case2020.lagalt.utils.Command;

@Service
public class MessageBoardService {
    public ResponseEntity<CommonResponse> createThread(HttpServletRequest request, HttpServletResponse response, String projectId, ObjectNode objectNode) throws ExecutionException, InterruptedException {
        Command cmd = new Command(request);
        CommonResponse cr = new CommonResponse();
        HttpStatus resp = null;

        Firestore dbFirestore = FirestoreClient.getFirestore();
        DocumentReference messageBoardReference = dbFirestore.collection("projects").document(projectId).collection("messageBoards").document("general");
        DocumentReference nextMessageId = messageBoardReference.collection(objectNode.get("title").asText()).document("nextId");
        
        // nextMessageId.set(new HashMap<String, Integer>() {{
        //     put("nextId", 1);
        // }});
        nextMessageId.set(Map.of("nextId", 1));

        ApiFuture<WriteResult> collectionApiFuture = messageBoardReference.collection(objectNode.get("title").asText()).document("0").set(createPost(objectNode.get("text").asText()));
        cr.data = collectionApiFuture.get().getUpdateTime().toString();
        resp = HttpStatus.OK;
        response.addHeader("Location", "/"+projectId + "/messageBoard/"+ objectNode.get("title").asText());
        cr.message = ("New thread created with id: " + objectNode.get("title").asText());
        cmd.setResult(resp);
        return new ResponseEntity<>(cr, resp);
    }

    public ResponseEntity<CommonResponse> createPost(HttpServletRequest request, HttpServletResponse response, String projectId, String threadId,ObjectNode objectNode) throws ExecutionException, InterruptedException {
        Command cmd = new Command(request);
        CommonResponse cr = new CommonResponse();
        HttpStatus resp = null;

        Firestore dbFirestore = FirestoreClient.getFirestore();
        CollectionReference threadReference = dbFirestore.collection("projects").document(projectId).collection("messageBoards").document("general").collection(threadId);
        DocumentReference nextMessageId = threadReference.document("nextId");
        ApiFuture<DocumentSnapshot> messageFuture = nextMessageId.get();
        DocumentSnapshot messageSnapshot = messageFuture.get();



        ApiFuture<WriteResult> collectionApiFuture = threadReference.document(messageSnapshot.get("nextId").toString()).set(createPost(objectNode.get("text").asText()));
        cr.data = collectionApiFuture.get().getUpdateTime().toString();
        Long next = (Long) messageSnapshot.get("nextId");
        resp = HttpStatus.OK;
        response.addHeader("Location", "/"+projectId + "/messageBoard/"+ objectNode.get("title").asText()+"/" + messageSnapshot.get("nextId"));
        nextMessageId.update("nextId", (next + 1));
        cmd.setResult(resp);
        return new ResponseEntity<>(cr, resp);

    }

     public ResponseEntity<CommonResponse> deletePost(HttpServletRequest request, HttpServletResponse response, String projectId, String threadId,ObjectNode objectNode) throws ExecutionException, InterruptedException {
        Command cmd = new Command(request);
        CommonResponse cr = new CommonResponse();
        HttpStatus resp;

        Firestore dbFirestore = FirestoreClient.getFirestore();
        DocumentReference messageDocument = dbFirestore.collection("projects").document(projectId).collection("messageBoards").document("general")
                .collection(threadId).document(objectNode.get("messageId").asText());
        System.out.println(objectNode.get("messageId").asText());
        System.out.println(messageDocument);
        ApiFuture<DocumentSnapshot> future = messageDocument.get();
        DocumentSnapshot document = future.get();


        if (document.exists()) {
            messageDocument.update("deleted", true);
            response.addHeader("Location", "/projects/" + projectId + "/messageBoards/" + threadId + "/" + objectNode.get("messageId").asText());
            cr.message = "Successfully deleted message with id: " + objectNode.get("messageId").asText();
            resp = HttpStatus.OK;
        } else {
            cr.message = "No message with id: " + objectNode.get("messageId").asText() + " found.";
            resp = HttpStatus.NOT_FOUND;
        }

        cmd.setResult(resp);
        return new ResponseEntity<>(cr, resp);
    }

    public MessageBoardPost createPost(String text){
        MessageBoardPost post = new MessageBoardPost();
        post.setText(text);
        post.setCreatedAt(Timestamp.now());
        post.setEditedAt(Timestamp.now());
        return post;
    }
}
