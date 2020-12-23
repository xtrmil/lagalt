package se.experis.com.case2020.lagalt.services;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import se.experis.com.case2020.lagalt.models.CommonResponse;
import se.experis.com.case2020.lagalt.models.MessageBoardPost;
import se.experis.com.case2020.lagalt.utils.Command;

@Service
public class MessageBoardService {

    private final String DEFAULT_FORUM = "general";

    @Autowired
    private MockAuthService authService;
    
    @Autowired
    private ProjectService projectService;

    public ResponseEntity<CommonResponse> createThread(HttpServletRequest request, HttpServletResponse response, String owner, String projectName, ObjectNode thread, 
    String Authorization) {
        Command cmd = new Command(request);
        CommonResponse cr = new CommonResponse();
        HttpStatus resp;

        try {
            var projectId = projectService.getProjectId(owner, projectName);
            
            if(projectId != null) {
                
                var projectReference = projectService.getProjectDocumentReference(projectId);
                String userId = authService.getUserIdFromToken(Authorization);
                if(userId != null && authService.isPartOfProjectStaff(owner, projectName, Authorization)) {
                    DocumentReference messageBoardReference = projectReference.collection("messageBoards").document(DEFAULT_FORUM);
                    DocumentReference nextMessageId = messageBoardReference.collection(thread.get("title").asText()).document("nextId");
                    
                    nextMessageId.set(Map.of("nextId", 1));
                    
                    messageBoardReference.collection(thread.get("title").asText()).document("0").set(createPost(thread.get("text").asText()));

                    resp = HttpStatus.OK;
                    response.addHeader("Location", "/"+ projectId + "/messageBoard/"+ thread.get("title").asText());
                    cr.message = ("New thread created with id: " + thread.get("title").asText());
                } else {
                    resp = HttpStatus.UNAUTHORIZED;
                    cr.message = "You are not a member of this project";
                }
            } else {
                resp = HttpStatus.NOT_FOUND;
                cr.message = "Project not found";
            }
        } catch(Exception e) {
            e.printStackTrace();
            resp = HttpStatus.INTERNAL_SERVER_ERROR;
            cr.message = "Server error";
        }
        cmd.setResult(resp);
        return new ResponseEntity<>(cr, resp);
    }

    public ResponseEntity<CommonResponse> createPost(HttpServletRequest request, HttpServletResponse response, String owner, String projectName, String threadId, ObjectNode post, String Authorization) {
        Command cmd = new Command(request);
        CommonResponse cr = new CommonResponse();
        HttpStatus resp = null;

        try {
            var projectId = projectService.getProjectId(owner, projectName);
            if(projectId != null) {

                String userId = authService.getUserIdFromToken(Authorization);
                if(userId != null) {
                    var db = FirestoreClient.getFirestore();
                    
                    CollectionReference threadReference = db.collection("projects").document(projectId).collection("messageBoards").document(DEFAULT_FORUM).collection(threadId);
                    DocumentReference nextMessageId = threadReference.document("nextId");
                    ApiFuture<DocumentSnapshot> messageFuture = nextMessageId.get();
                    DocumentSnapshot messageSnapshot = messageFuture.get();
                    
                    ApiFuture<WriteResult> collectionApiFuture = threadReference.document(messageSnapshot.getString("nextId")).set(createPost(post.get("text").asText()));
                    cr.data = collectionApiFuture.get().getUpdateTime().toString();
                    Long next = (Long) messageSnapshot.get("nextId");
                    resp = HttpStatus.OK;
                    response.addHeader("Location", "/"+projectId + "/messageBoard/"+ post.get("title").asText()+"/" + messageSnapshot.getString("nextId"));
                    nextMessageId.update("nextId", (next + 1));
                } else {
                    resp = HttpStatus.UNAUTHORIZED;
                    cr.message = "You are not a member of this project";
                }
            } else {
                resp = HttpStatus.NOT_FOUND;
                cr.message = "Project not found";
            }
        } catch(Exception e) {
            e.printStackTrace();
            resp = HttpStatus.INTERNAL_SERVER_ERROR;
            cr.message = "Server error";
        }
        cmd.setResult(resp);
        return new ResponseEntity<>(cr, resp);

    }

     public ResponseEntity<CommonResponse> deletePost(HttpServletRequest request, HttpServletResponse response, String owner, String projectName, String threadId, String messageId, String Authorization) {
        Command cmd = new Command(request);
        CommonResponse cr = new CommonResponse();
        HttpStatus resp;
        String projectId = null; // TODO TEMP

        try {
            DocumentReference messageDocument = projectService.getProjectDocumentReference(projectId).collection("messageBoards").document(DEFAULT_FORUM)
                    .collection(threadId).document(messageId);
            System.out.println(messageId);
            System.out.println(messageDocument);
            ApiFuture<DocumentSnapshot> future = messageDocument.get();
            DocumentSnapshot document = future.get();


            if (document.exists()) {
                messageDocument.update("deleted", true);
                response.addHeader("Location", "/projects/" + projectId + "/messageBoards/" + threadId + "/" + messageId);
                cr.message = "Successfully deleted message with id: " + messageId;
                resp = HttpStatus.OK;
            } else {
                cr.message = "No message with id: " + messageId + " found.";
                resp = HttpStatus.NOT_FOUND;
            }

        } catch(Exception e) {
            e.printStackTrace();
            resp = HttpStatus.INTERNAL_SERVER_ERROR;
            cr.message = "Server error";
        }
        cmd.setResult(resp);
        return new ResponseEntity<>(cr, resp);
    }

    public MessageBoardPost createPost(String text) {
        MessageBoardPost post = new MessageBoardPost();
        post.setText(text);
        return post;
    }
}
