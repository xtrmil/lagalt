package se.experis.com.case2020.lagalt.controllers;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.experis.com.case2020.lagalt.models.CommonResponse;
import se.experis.com.case2020.lagalt.utils.Command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping(value = "/api/v1/projects", produces = MediaType.APPLICATION_JSON_VALUE)
public class ChatController {

    @DeleteMapping("/{projectId}/chat/{chatId}")
    ResponseEntity<CommonResponse> deleteChat(HttpServletRequest request, HttpServletResponse response,@PathVariable("projectId") String projectId ,@PathVariable("chatId") String chatId) throws ExecutionException, InterruptedException {
        Command cmd = new Command(request);
        CommonResponse cr = new CommonResponse();
        HttpStatus resp;

        Firestore dbFirestore = FirestoreClient.getFirestore();
        DocumentReference chatdocument = dbFirestore.collection("projects").document(projectId).collection("chat").document(chatId);

        ApiFuture<DocumentSnapshot> future = chatdocument.get();
        DocumentSnapshot document = future.get();

        if (document.exists()) {
            ApiFuture<WriteResult> writeResult = chatdocument.delete();
            response.addHeader("Location", "/projects/" + projectId + "/chat/" + chatId);
            cr.message = "Successfully deleted chat with id: " + chatId;
            resp = HttpStatus.OK;
        }else{
            cr.message = "No chat with id: " + chatId + " found.";
            resp = HttpStatus.NOT_FOUND;
        }

        cmd.setResult(resp);
        return new ResponseEntity<>(cr, resp);
    }
}
