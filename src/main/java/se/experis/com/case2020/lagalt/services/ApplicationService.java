package se.experis.com.case2020.lagalt.services;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
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
    private AuthService authService;

    @Autowired
    private ProjectService projectService;

    public ResponseEntity<CommonResponse> getApplications(HttpServletRequest request, String owner, String projectName,
            String Authorization) {
        Command cmd = new Command(request);
        CommonResponse cr = new CommonResponse();
        HttpStatus resp;
        Set<ApplicationAdminView> applicationSet = new HashSet<>();

        if (authService.isProjectAdmin(owner, projectName, Authorization)) {
            String projectId = projectService.getProjectId(owner, projectName);

            if (projectId != null) {
                var projectCollections = projectService.getProjectDocumentReference(projectId)
                        .collection("activeApplications").listDocuments();

                projectCollections.forEach(application -> {
                    try {
                        DocumentSnapshot applicationDocument = getApplicationDocumentReference(application.getId())
                                .get().get();
                        var applicationView = new ApplicationAdminView();
                        applicationView.setApplicationId(applicationDocument.getId());
                        applicationView.setMotivation(applicationDocument.getString("motivation"));
                        applicationView.setCreatedAt(applicationDocument.getCreateTime());

                        UserPublicView user = userService.getUserPublicObject(applicationDocument.getString("user"));
                        applicationView.setUser(user);

                        applicationSet.add(applicationView);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                if (applicationSet.isEmpty()) {
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

    public ResponseEntity<CommonResponse> createApplication(HttpServletRequest request, String owner,
            String projectName, String Authorization, ObjectNode motivation) {
        Command cmd = new Command(request);
        CommonResponse cr = new CommonResponse();
        HttpStatus resp;

        try {
            String projectId = projectService.getProjectId(owner, projectName);
            if (projectId != null) {

                String userId = authService.getUserIdFromToken(Authorization);
                if (userId != null) {

                    if (!authService.isPartOfProjectStaff(owner, projectName, Authorization)) {
                        var db = FirestoreClient.getFirestore();
                        ApplicationProfileView applicationProfileView = new ApplicationProfileView();
                        applicationProfileView.setProject(projectId);
                        applicationProfileView.setMotivation(motivation.get("motivation").asText());
                        applicationProfileView.setUser(userId);

                        var applicationRecord = db.collection("pendingApplicationRecords").document(projectId)
                                .collection("users").document(userId);
                        if (!applicationRecord.get().get().exists()) {

                            applicationRecord.set(new HashMap<>());
                            var applicationDocRef = db.collection("applications").document();
                            applicationDocRef.set(applicationProfileView);
                            userService.getUserDocument(userId).collection("appliedTo")
                                    .document(applicationDocRef.getId()).set(new HashMap<>());
                            projectService.getProjectDocumentReference(projectId).collection("activeApplications")
                                    .document(applicationDocRef.getId()).set(new HashMap<>());

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
        } catch (Exception e) {
            cr.message = "Server error";
            resp = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        cmd.setResult(resp);
        return new ResponseEntity<>(cr, resp);
    }

    public ResponseEntity<CommonResponse> answerApplication(HttpServletRequest request, String owner,
            String projectName, String applicationId, ObjectNode requestBody, String Authorization) {
        Command cmd = new Command(request);
        CommonResponse cr = new CommonResponse();
        HttpStatus resp;
        String projectId = projectService.getProjectId(owner, projectName);
        String incomingStatusString = requestBody.get("status").asText();

        try {
            if (EnumUtils.isValidEnum(ApplicationStatus.class, incomingStatusString)) {
                var db = FirestoreClient.getFirestore();
                ApplicationStatus incomingStatus = ApplicationStatus.valueOf(incomingStatusString);

                var projectDocumentReference = projectService.getProjectDocumentReference(projectId);
                var projectApplicationReference = projectDocumentReference.collection("activeApplications")
                        .document(applicationId);
                if (projectApplicationReference.get().get().exists()) {
                    DocumentSnapshot applicationDocument = getApplicationDocumentReference(applicationId).get().get();

                    if (ApplicationStatus
                            .valueOf(applicationDocument.getString("status")) == ApplicationStatus.PENDING) {

                        if (authService.isProjectAdmin(owner, projectName, Authorization)) {
                            ApplicationProfileView updatedApplication = new ApplicationProfileView();
                            updatedApplication.setStatus(incomingStatus);

                            updatedApplication.setFeedback(requestBody.get("message").asText());
                            updatedApplication.setMotivation(applicationDocument.getString("motivation"));
                            updatedApplication.setProject(projectId);
                            String applicantId = applicationDocument.getString("user");
                            updatedApplication.setUser(applicantId);

                            if (incomingStatus == ApplicationStatus.APPROVED
                                    || incomingStatus == ApplicationStatus.REJECTED) {
                                if (incomingStatus == ApplicationStatus.APPROVED) {
                                    var newMember = db.collection("users").document(applicantId);
                                    newMember.collection("memberOf").document(projectId).set(new HashMap<>());
                                    newMember.collection("contributedTo").document(projectId).set(new HashMap<>());
                                    projectDocumentReference.collection("members").document(applicantId)
                                            .set(new HashMap<>());
                                }
                                projectDocumentReference.collection("archivedApplications").document(applicationId)
                                        .set(new HashMap<>());
                                projectApplicationReference.delete();
                            }

                            getApplicationDocumentReference(applicationId).set(updatedApplication);
                            db.collection("pendingApplicationRecords").document(projectId).collection("users")
                                    .document(applicantId).delete();
                            cr.message = "Application updated";
                            resp = HttpStatus.OK;

                        } else {
                            cr.message = "Not authorized to edit application";
                            resp = HttpStatus.UNAUTHORIZED;
                        }
                    } else {
                        cr.message = "Application has already been answered";
                        resp = HttpStatus.BAD_REQUEST;
                    }
                } else {
                    cr.message = "Application not found";
                    resp = HttpStatus.NOT_FOUND;
                }
            } else {
                cr.message = "Invalid request body";
                resp = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        cmd.setResult(resp);
        return new ResponseEntity<>(cr, resp);
    }

    private DocumentReference getApplicationDocumentReference(String applicationId) {
        return FirestoreClient.getFirestore().collection("applications").document(applicationId);
    }
}
