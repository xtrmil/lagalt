package se.experis.com.case2020.lagalt.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import se.experis.com.case2020.lagalt.models.CommonResponse;
import se.experis.com.case2020.lagalt.models.messageboard.MessageBoardPost;
import se.experis.com.case2020.lagalt.models.messageboard.MessageBoardThread;
import se.experis.com.case2020.lagalt.utils.Command;

@Service
public class MessageBoardService {

    @Autowired
    private AuthService authService;

    @Autowired
    private ProjectService projectService;

    public ResponseEntity<CommonResponse> getAllThreads(HttpServletRequest request, String projectOwner,
            String projectName, String Authorization) {
        Command cmd = new Command(request);
        CommonResponse cr = new CommonResponse();
        HttpStatus resp;

        try {
            if (authService.isPartOfProjectStaff(projectOwner, projectName, Authorization)) {
                String projectId = projectService.getProjectId(projectOwner, projectName);

                var defaultMessageBoardDocument = getDefaultMessageBoardReference(projectId);
                var threadCollections = defaultMessageBoardDocument.listCollections();

                if (!defaultMessageBoardDocument.get().get().exists()) {

                    List<MessageBoardThread> threads = new ArrayList<>();
                    threadCollections.forEach(t -> {
                        try {
                            MessageBoardThread thread = new MessageBoardThread();
                            var initialPost = t.document("0").get().get();
                            String title = initialPost.getString("title");
                            String creator = authService.getUsername(initialPost.getString("user"));

                            thread.setLink("/api/v1/projects/" + projectOwner + "/" + projectName + "/messageboard/"
                                    + t.getId());
                            thread.setTitle(title);
                            thread.setNrOfMessages(t.get().get().getDocuments().size());
                            thread.setCreatedBy(creator);
                            threads.add(thread);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    cr.data = threads;
                    resp = HttpStatus.OK;
                } else {
                    cr.message = "No messageboards found";
                    resp = HttpStatus.NO_CONTENT;
                }
            } else {
                cr.message = "You are not authorized to view this message board";
                resp = HttpStatus.UNAUTHORIZED;
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp = HttpStatus.INTERNAL_SERVER_ERROR;
            cr.message = "Server error";
        }
        cmd.setResult(resp);
        return new ResponseEntity<>(cr, resp);
    }

    public ResponseEntity<CommonResponse> getPosts(HttpServletRequest request, String projectOwner, String projectName,
            String threadId, String Authorization) {
        Command cmd = new Command(request);
        CommonResponse cr = new CommonResponse();
        HttpStatus resp;

        try {
            if (authService.isPartOfProjectStaff(projectOwner, projectName, Authorization)) {
                String projectId = projectService.getProjectId(projectOwner, projectName);
                var threadCollection = getDefaultMessageBoardReference(projectId).collection(threadId)
                        .whereEqualTo("deleted", false).get().get();

                if (!threadCollection.isEmpty()) {
                    Set<MessageBoardPost> posts = new HashSet<>();

                    var postDocuments = threadCollection.getDocuments();
                    postDocuments.forEach(p -> {
                        try {
                            MessageBoardPost post = p.toObject(MessageBoardPost.class);
                            post.setCreatedAt(p.getCreateTime());
                            post.setUser(authService.getUsername(post.getUser()));
                            posts.add(post);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    cr.data = posts;
                    resp = HttpStatus.OK;
                } else {
                    cr.message = "Message board not found";
                    resp = HttpStatus.NOT_FOUND;
                }
            } else {
                cr.message = "You are not authorized to post messages on this message board";
                resp = HttpStatus.UNAUTHORIZED;
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp = HttpStatus.INTERNAL_SERVER_ERROR;
            cr.message = "Server error";
        }
        cmd.setResult(resp);
        return new ResponseEntity<>(cr, resp);
    }

    public ResponseEntity<CommonResponse> createThread(HttpServletRequest request, HttpServletResponse response,
            String projectOwner, String projectName, ObjectNode requestBody, String Authorization) {
        return createMessageBoardContent(request, response, projectOwner, projectName,
                requestBody.get("title").asText(), requestBody.get("text").asText(), Authorization, true);
    }

    public ResponseEntity<CommonResponse> createPost(HttpServletRequest request, HttpServletResponse response,
            String projectOwner, String projectName, String threadId, ObjectNode requestBody, String Authorization) {
        return createMessageBoardContent(request, response, projectOwner, projectName, threadId,
                requestBody.get("text").asText(), Authorization, false);
    }

    private ResponseEntity<CommonResponse> createMessageBoardContent(HttpServletRequest request,
            HttpServletResponse response, String projectOwner, String projectName, String threadName, String text,
            String Authorization, boolean createNewThread) {
        Command cmd = new Command(request);
        CommonResponse cr = new CommonResponse();
        HttpStatus resp;
        String threadId = getSafeTitle(threadName);

        try {
            var projectId = projectService.getProjectId(projectOwner, projectName);

            if (projectId != null) {

                String userId = authService.getUserIdFromToken(Authorization);
                if (userId != null && authService.isPartOfProjectStaff(projectOwner, projectName, Authorization)) {
                    CollectionReference threadReference = getDefaultMessageBoardReference(projectId)
                            .collection(threadId);

                    if ((createNewThread && threadReference.get().get().isEmpty())
                            || (!createNewThread && !threadReference.get().get().isEmpty())) {

                        MessageBoardPost post = new MessageBoardPost();
                        post.setText(text);
                        post.setUser(userId);

                        String postId = "" + threadReference.get().get().getDocuments().size();
                        threadReference.document(postId).set(post);

                        if (createNewThread) {
                            threadReference.document(postId).update("title", threadName);
                            cr.message = "New thread created " + threadId + "'";
                        } else {
                            cr.message = "Message posted";
                        }

                        resp = HttpStatus.OK;
                        response.addHeader("Location",
                                "/" + projectOwner + "/" + projectName + "/messageboard/" + threadId);
                    } else {
                        resp = HttpStatus.BAD_REQUEST;
                        cr.message = "Invalid parameters";
                    }
                } else {
                    resp = HttpStatus.UNAUTHORIZED;
                    cr.message = "You are not a member of this project";
                }
            } else {
                resp = HttpStatus.NOT_FOUND;
                cr.message = "Project not found";
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp = HttpStatus.INTERNAL_SERVER_ERROR;
            cr.message = "Server error";
        }
        cmd.setResult(resp);
        return new ResponseEntity<>(cr, resp);
    }

    public ResponseEntity<CommonResponse> deletePost(HttpServletRequest request, String projectOwner,
            String projectName, String threadId, String messageId, String Authorization) {
        Command cmd = new Command(request);
        CommonResponse cr = new CommonResponse();
        HttpStatus resp;
        String projectId = projectService.getProjectId(projectOwner, projectName);
        String userId = authService.getUserIdFromToken(Authorization);

        try {
            DocumentReference messageDocument = getDefaultMessageBoardReference(projectId).collection(threadId)
                    .document(messageId);
            System.out.println(messageId);
            System.out.println(messageDocument);
            DocumentSnapshot post = messageDocument.get().get();

            if (post.exists()) {

                if (userId != null && userId.equals(post.getString("user"))) {
                    messageDocument.update("deleted", true);
                    cr.message = "Successfully deleted message with id: " + messageId;
                    resp = HttpStatus.OK;
                } else {
                    resp = HttpStatus.UNAUTHORIZED;
                    cr.message = "You are not the owner of this post";
                }

            } else {
                cr.message = "No message with id: " + messageId + " found.";
                resp = HttpStatus.NOT_FOUND;
            }

        } catch (Exception e) {
            e.printStackTrace();
            resp = HttpStatus.INTERNAL_SERVER_ERROR;
            cr.message = "Server error";
        }
        cmd.setResult(resp);
        return new ResponseEntity<>(cr, resp);
    }

    private DocumentReference getDefaultMessageBoardReference(String projectId) {
        return projectService.getProjectDocumentReference(projectId).collection("messageBoards").document("general");
    }

    private String getSafeTitle(String original) {
        return original.toLowerCase().replaceAll(" ", "-").replaceAll("[^-a-zA-Z0-9]", "");
    }
}