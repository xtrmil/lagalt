package se.experis.com.case2020.lagalt.services;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;

import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import se.experis.com.case2020.lagalt.models.CommonResponse;
import se.experis.com.case2020.lagalt.models.application.ApplicationAdminView;
import se.experis.com.case2020.lagalt.models.application.ApplicationProfileView;
import se.experis.com.case2020.lagalt.models.enums.ApplicationStatus;
import se.experis.com.case2020.lagalt.models.user.UserPublicView;
import se.experis.com.case2020.lagalt.utils.Command;

@Service
public class ApplicationService {
    
    @Autowired
    private UserService userService;

    @Autowired
    private MockAuthService authService;

    @Autowired
    private ProjectService projectService;
    
    public ResponseEntity<CommonResponse> getApplications(HttpServletRequest request, String owner, String projectName, String Authorization) {
        Command cmd = new Command(request);
        CommonResponse cr = new CommonResponse();
        HttpStatus resp;
        Set<ApplicationAdminView> applicationSet = new HashSet<>();
        
        if (authService.isProjectAdmin(owner, projectName, Authorization)) {
            String projectId = projectService.getProjectId(owner, projectName);
            
            if(projectId != null) {
                var db = FirestoreClient.getFirestore();
                var projectCollections = db.collection("projects").document(projectId).collection("applications").listDocuments();
                
                projectCollections.forEach(application -> {
                    try {
                        DocumentSnapshot applicationDocument = db.collection("applications").document(application.getId()).get().get();
                        var applicationAdminView = applicationDocument.toObject(ApplicationAdminView.class);
                        UserPublicView user = userService.getUserPublicObject(applicationDocument.get("userId").toString());
                        
                        applicationAdminView.setUser(user);
                        applicationSet.add(applicationAdminView);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                if(applicationSet.isEmpty()) {
                    resp = HttpStatus.NO_CONTENT;
                } else {
                    resp = HttpStatus.OK;
                }
                cr.data = applicationSet;
                cr.message = "Successfully retrieved all pending applications for project: " + projectName;
            } else {
                resp = HttpStatus.NOT_FOUND;
                cr.message = "Project not found";
            }
        } else {
            resp = HttpStatus.UNAUTHORIZED;
            cr.message = "Not authorized to retrieve this data";
        }

        cmd.setResult(resp);
        return new ResponseEntity<>(cr, resp);
    }

    public ResponseEntity<CommonResponse> createApplication(HttpServletRequest request, String owner, String projectName, String Authorization, ObjectNode motivation) {
        Command cmd = new Command(request);
        CommonResponse cr = new CommonResponse();
        HttpStatus resp;

        try {
            String projectId = projectService.getProjectId(owner, projectName);
            if(projectId != null) {
                
                String userId = authService.getUserIdFromToken(Authorization);
                if (userId != null) {
                    var db = FirestoreClient.getFirestore();

                    DocumentReference userReference = db.collection("users").document(userId);
                    DocumentReference projectReference = db.collection("projects").document(projectId);

                    ApplicationProfileView applicationProfileView = new ApplicationProfileView();
                    applicationProfileView.setProject(projectId);
                    applicationProfileView.setMotivation(motivation.get("motivation").asText());
                    applicationProfileView.setUser(userId);

                    if(!authService.isPartOfProjectStaff(owner, projectName, Authorization)) {
                        var applicationRecord = db.collection("pendingApplicationRecords").document(projectId).collection("users").document(userId);
                        if(!applicationRecord.get().get().exists()) {
                            
                            var ref = db.collection("applications").document(projectId).collection("applications").document();
                            applicationRecord.set(new HashMap<>());
                            db.collection("applications").document(ref.getId()).set(applicationProfileView);
                            userReference.collection("appliedTo").document(ref.getId()).set(new HashMap<>());
                            projectReference.collection("applications").document(ref.getId()).set(new HashMap<>());
                            
                            cr.data = "Your motivation: " + motivation.get("motivation").asText();
                            cr.message = "Application successfully submitted to project: " + projectId;
                            resp = HttpStatus.OK;
                        } else {
                            cr.message = "You already have a pending application to this project";
                            resp = HttpStatus.CONFLICT;
                        }
                    } else {
                        cr.message = "You are already a member of this project";
                        resp = HttpStatus.CONFLICT;
                    }
                } else {
                    cr.message = "You are not logged in";
                    resp = HttpStatus.UNAUTHORIZED;
                }
            } else {
                cr.message = "Project not found";
                resp = HttpStatus.NOT_FOUND;
            }
        } catch(Exception e) {
            cr.message = "Server error";
            resp = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        cmd.setResult(resp);
        return new ResponseEntity<>(cr, resp);
    }


    public ResponseEntity<CommonResponse> answerApplication(HttpServletRequest request, String owner, String projectName, String applicationId, ObjectNode application,
    String Authorization) {
        Command cmd = new Command(request);
        CommonResponse cr = new CommonResponse();
        HttpStatus resp;
        String projectId = projectService.getProjectId(owner, projectName);

        try {
            String message = application.get("message").asText();
            ApplicationStatus status = ApplicationStatus.valueOf(application.get("status").asText());

            var db = FirestoreClient.getFirestore();
            DocumentSnapshot applicationDocument = db.collection("applications").document(applicationId).get().get();

            if (applicationDocument.exists()) {
                if (authService.isProjectAdmin(owner, projectName, Authorization)) {
                    ApplicationProfileView updatedApplication = new ApplicationProfileView();
                    if (EnumUtils.isValidEnum(ApplicationStatus.class, status.toString())) {
                        updatedApplication.setStatus(status);
                    }
                    updatedApplication.setFeedback(message);
                    updatedApplication.setMotivation(applicationDocument.getData().get("motivation").toString());
                    updatedApplication.setProject(projectId);
                    String applicantId = applicationDocument.get("user").toString();
                    updatedApplication.setUser(applicantId);

                    db.collection("applications").document(applicationId).set(updatedApplication);
                    db.collection("pendingApplicationRecords").document(projectId).collection("users").document(applicantId).delete();
                    cr.message = "Application with id: " + applicationId + "for project with id: " + projectId + " successfully Updated";
                    resp = HttpStatus.OK;
                } else {
                    cr.message = "Not authorized to edit application with id: " + applicationId;
                    resp = HttpStatus.UNAUTHORIZED;
                }
            } else {
                cr.message = "Application with id: " + applicationId + " not found";
                resp = HttpStatus.NOT_FOUND;
            }
        } catch(Exception e) {
            e.printStackTrace();
            resp = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        cmd.setResult(resp);
        return new ResponseEntity<>(cr, resp);
    }
}
