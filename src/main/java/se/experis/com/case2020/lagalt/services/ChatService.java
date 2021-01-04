package se.experis.com.case2020.lagalt.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import se.experis.com.case2020.lagalt.models.ChatMessage;
import se.experis.com.case2020.lagalt.models.CommonResponse;

@Service
public class ChatService {
        
    @Autowired
    private MockAuthService authService;
    
    @Autowired
    private ProjectService projectService;


    public ResponseEntity<CommonResponse> getChatDBPath(String projectOwner, String projectName, String Authorization) {
        CommonResponse cr = new CommonResponse();
        HttpStatus resp;

        try {
            String userId = authService.getUserIdFromToken(Authorization);
            if(userId != null) {

                var projectId = projectService.getProjectId(projectOwner, projectName);
                if(projectId != null) {
                    cr.data = "projects/" + projectId + "/chat/default/default/";
                    resp = HttpStatus.OK;
                } else {
                    cr.message = "Project not found";
                    resp = HttpStatus.NOT_FOUND;
                }
            } else {
                cr.message = "You are not authorized to post in this chat";
                resp = HttpStatus.UNAUTHORIZED;
            }
        } catch(Exception e) {
            cr.message = "Server error";
            resp = HttpStatus.INTERNAL_SERVER_ERROR;
            e.printStackTrace();
        }
        return new ResponseEntity<>(cr, resp);
    }

    public ResponseEntity<CommonResponse> createChatMessage(String projectOwner, String projectName, String text, String Authorization) {
        CommonResponse cr = new CommonResponse();
        HttpStatus resp;
        
        try {
            String userName = authService.getUsernameFromToken(Authorization);
            if(userName != null) {

                var projectDocRef = projectService.getProjectDocumentReference(projectOwner, projectName);
                if(projectDocRef != null) {
                    var chatMessage = new ChatMessage();
                    chatMessage.setText(text.trim());
                    chatMessage.setUser(userName);
                    projectDocRef.collection("chat/default/default").add(chatMessage);
                    resp = HttpStatus.OK;
                } else {
                    cr.message = "Project not found";
                    resp = HttpStatus.NOT_FOUND;
                }
            } else {
                cr.message = "You are not authorized to post in this chat";
                resp = HttpStatus.UNAUTHORIZED;
            }
        } catch(Exception e) {
            cr.message = "Server error";
            resp = HttpStatus.INTERNAL_SERVER_ERROR;
            e.printStackTrace();
        }
        
        return new ResponseEntity<>(cr, resp);
    }
}
